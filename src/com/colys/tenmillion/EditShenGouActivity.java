package com.colys.tenmillion;

import java.util.LinkedList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.colys.tenmillion.Entity.ShenGouRecords;
import com.colys.tenmillion.Entity.Member;

import CustomViews.DateClickListener;
import CustomViews.MemberSelectDialog;
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.EntityDBHelper;
import DataAccess.ShenGouRecordsAccess;
import DataAccess.*;

public class EditShenGouActivity extends WSActivity {
	
	private EditText txtFenE,txtApplyDate,txtMember;
	private CheckBox chkPatch;
	private String shenGouId;
	private ShenGouRecords shenGou;
	MemberSelectDialog memberSelectDialog ;
	
	protected int getLayout()  {
		return R.layout.activity_edit_shen_gou;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		txtMember = (EditText) findViewById(R.id.shen_gou_edit_member);	
		txtFenE = (EditText) findViewById(R.id.shen_gou_edit_fen_e);	
		txtApplyDate = (EditText) findViewById(R.id.shen_gou_edit_apply_date);	
		 
		chkPatch= (CheckBox) findViewById(R.id.shen_gou_edit_is_patch);	
		chkPatch.setChecked(true);
		txtApplyDate.setOnClickListener(new DateClickListener(this));
		if(this.getIntent().getExtras()!=null)
		shenGouId = this.getIntent().getExtras().getString("shenGouID");
		Button btnSave = (Button) findViewById(R.id.shen_gou_edit_save);	
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(shenGou == null) shenGou =new ShenGouRecords();
				if(shenGou.MemberID==null){
					ws.Toast(R.string.please_choose_member);
					return;
				} 
				shenGou.ApplyDate = txtApplyDate.getText().toString();
				shenGou.SGFenE= Integer.valueOf( txtFenE.getText().toString());
				shenGou.IsPatch =chkPatch.isChecked();
				if(shenGou.SGFenE ==0 ){
					ws.Toast("please input fen e");
					return;
				}
				if(shenGou.ApplyDate.isEmpty() ){
					ws.Toast("please select apply date");
					return;
				}
				BasicAccess access =new BasicAccess(EditShenGouActivity.this);
				try {
				if(shenGou.ID == null)
					
						access.Visit(ShenGouRecordsAccess.class).Add(shenGou);
					
				else
					access.Visit(ShenGouRecordsAccess.class).Update(shenGou);
					//ws.visitServices("ShenGou_UpdateJson","json", shenGou.ToJson(), UpdateShenGou);
				access.Close(true);
				setResult(RESULT_OK);
				finish();
				} catch (Exception e) {
					ws.Toast(e.getMessage());
				}
			}
		});		
		
		if(shenGouId == null){
			runQueryMembers();
			txtMember.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {				
					memberSelectDialog.Show();				
				}
			});
		}
		else{
			QueryShenGou();
		}
	}
	
	private void BindMemberDialog(){
		if(memberSelectDialog ==null){
			
			memberSelectDialog =new MemberSelectDialog(this,					
					memberlst,
					R.string.edit_ask_for_leave_member_label,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(shenGou ==null) shenGou =new ShenGouRecords();					 
					shenGou.MemberName = memberSelectDialog.SelectedMember.Name;
					shenGou.MemberID = memberSelectDialog.SelectedMember.ID;
					txtMember.setText(shenGou.MemberName);
				}
			});
		}
		
	}
	
	private final int QueryShenGou =0,AddShenGou =1,UpdateShenGou =2,QueryMembers =3;
	private LinkedList<Member> memberlst;
	
	public void runQueryMembers(){
		if(Utility.UseLocal){
			String sql ="select * from Member where status > -1";
			EntityDBHelper<Member> helper =new EntityDBHelper<Member>(ws.GetSqlite(),Member.class);
			memberlst = helper.QueryList(sql);
			BindMemberDialog();			
		}else{
			ws.visitServices("Member_GetAllMemberJson", "tree","false", QueryMembers);			
		}
	}
	
	private void QueryShenGou(){
		if(Utility.UseLocal){
			String sql ="select Member.Name MemberName,ShenGouRecords.* from ShenGouRecords join Member on Member.ID = ShenGouRecords.MemberID  where ShenGouRecords.ID='"+ shenGouId+"'";
			BasicAccess access=new BasicAccess(this);
			try
			{
				shenGou = access.Visit(DefaultAccess.class).QueryEntity(ShenGouRecords.class, sql);
			}
			catch (Exception e)
			{
				ws.Toast(e.getMessage());
				return;
			}
			BindTask();
		}else{ 
			ws.visitServices("ShenGou_GetJson","ID", String.valueOf(shenGouId), QueryShenGou);
		}
	}
	
	private void BindTask(){
		txtFenE.setText(String.valueOf( shenGou.SGFenE));
		txtApplyDate.setText(shenGou.ApplyDate); 
		chkPatch.setChecked(shenGou.IsPatch);
		txtMember.setText(shenGou.MemberName);
	}
	
	
	

	public void onHandleMessage(Message msg){
		super.onHandleMessage(msg);	
		switch(msg.what){
			case QueryMembers:
				memberlst = Member.ListFromJson(ws.queryResult);
				BindMemberDialog();					
				break;
			case QueryShenGou:
				shenGou = ShenGouRecords.FromJson(ws.queryResult);
				BindTask();
				break;
			case AddShenGou:
			case UpdateShenGou:
				setResult(RESULT_OK);
				finish();
				break;		
		}
	}

}
