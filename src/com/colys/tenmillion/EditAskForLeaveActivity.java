package com.colys.tenmillion;

import java.util.LinkedList;

import CustomViews.DateClickListener;
import CustomViews.MemberSelectDialog;
import CustomViews.WSActivity;
import DataAccess.AskForLeaveAccess;
import DataAccess.BasicAccess;
import DataAccess.EntityDBHelper; 
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.colys.tenmillion.Entity.AskForLeave;
import com.colys.tenmillion.Entity.Member; 

public class EditAskForLeaveActivity extends WSActivity{
	private EditText txtText,txtBegin,txtEnd,txtMember;
	private CheckBox chkIsfinish;
	private String askForLeaveId;
	private AskForLeave askForLeave;
	MemberSelectDialog memberSelectDialog ;
	
	protected int getLayout()  {
		return R.layout.activity_edit_askforleave;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		txtMember = (EditText) findViewById(R.id.ask_for_leave_edit_member);	
		txtText = (EditText) findViewById(R.id.ask_for_leave_edit_reason);	
		txtBegin = (EditText) findViewById(R.id.ask_for_leave_edit_begin_date);	
		txtEnd = (EditText) findViewById(R.id.ask_for_leave_edit_end_date);	
		chkIsfinish= (CheckBox) findViewById(R.id.ask_for_leave_edit_isfinish);	
		txtBegin.setOnClickListener(new DateClickListener(this));
		txtEnd.setOnClickListener(new DateClickListener(this));
		if(this.getIntent().getExtras()!=null)
			askForLeaveId = this.getIntent().getExtras().getString("askID");
		Button btnSave = (Button) findViewById(R.id.ask_for_leave_edit_save);	
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(askForLeave == null) askForLeave =new AskForLeave();
				if(askForLeave.MemberID==null){
					ws.Toast(R.string.please_choose_member);
					return;
				}
				askForLeave.Remark = txtText.getText().toString();
				askForLeave.ApplyDate = txtBegin.getText().toString();
				askForLeave.BackDate= txtEnd.getText().toString();
				askForLeave.IsBack =chkIsfinish.isChecked();
				if(askForLeave.Remark.isEmpty()){
					ws.Toast(R.string.edit_ask_for_leave_reason_label);
					return;
				}
				if(askForLeave.ApplyDate.isEmpty() || askForLeave.BackDate.isEmpty()){
					ws.Toast(R.string.please_choose_task_date_message);
					return;
				}
				BasicAccess access =new BasicAccess(EditAskForLeaveActivity.this); 
				try {
					access.OpenTransConnect();
				if(askForLeave.ID == null)					
						access.Visit(AskForLeaveAccess.class).Add(askForLeave);					
				else
					access.Visit(AskForLeaveAccess.class).Update(askForLeave);
					setResult(RESULT_OK);
					finish();
				} catch (Exception e) {
					access.Close(false);
					ws.Toast(e.getMessage());
				}
				access.Close(true);
			}
		});
		
		txtMember.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
				memberSelectDialog.Show();				
			}
		});
		runQueryMembers();
		
	}
	
	private void BindMemberDialog(){
		if(memberSelectDialog ==null){
			
			memberSelectDialog =new MemberSelectDialog(EditAskForLeaveActivity.this,					
					memberlst,
					R.string.edit_ask_for_leave_member_label,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(askForLeave ==null) askForLeave =new AskForLeave();
					askForLeave.MemberName = memberSelectDialog.SelectedMember.Name;
					askForLeave.MemberID = memberSelectDialog.SelectedMember.ID;
					txtMember.setText(askForLeave.MemberName);
				}
			});
		}
		if(askForLeaveId != null){
			QueryTask();
		}
	}
	
	private final int QueryTask =0,AddTask =1,UpdateTask =2,QueryMembers =3;
	private LinkedList<Member> memberlst;
	
	public void runQueryMembers(){
		if(Utility.UseLocal){
			memberlst =	ws.QueryLocalMember(false,null);
			BindMemberDialog();			
		}else{
			ws.visitServices("Member_GetAllMemberJson", "tree","false", QueryMembers);			
		}
	}
	
	private void QueryTask(){
		if(Utility.UseLocal){
			String sql ="select Member.Name MemberName,AskForLeave.* from AskForLeave join Member on Member.ID = AskForLeave.MemberID  where AskForLeave.ID='"+ askForLeaveId+"'";
			EntityDBHelper<AskForLeave> helper =new EntityDBHelper<AskForLeave>(ws.GetSqlite(),AskForLeave.class);
			askForLeave = helper.QuerySingle(sql);
			BindTask();
		}else{ 
			ws.visitServices("AskForLeave_GetJson","m_id", String.valueOf(askForLeaveId), QueryTask);
		}
	}
	
	private void BindTask(){
		txtText.setText(askForLeave.Remark);
		txtBegin.setText(askForLeave.ApplyDate);
		txtEnd.setText(askForLeave.BackDate);
		chkIsfinish.setChecked(askForLeave.IsBack);
		txtMember.setText(askForLeave.MemberName);
	}
	
	
	

	public void onHandleMessage(Message msg){
		super.onHandleMessage(msg);	
		switch(msg.what){
			case QueryMembers:
				memberlst = Member.ListFromJson(ws.queryResult);
				BindMemberDialog();					
				break;
			case QueryTask:
				askForLeave = AskForLeave.FromJson(ws.queryResult);
				BindTask();
				break;
			case AddTask:
			case UpdateTask:
				setResult(RESULT_OK);
				finish();
				break;		
		}
	}
}
