package com.colys.tenmillion;
import CustomViews.*;
import DataAccess.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.colys.tenmillion.Entity.*;
import java.util.*;

public class EditMemberActivity extends WSActivity
{
	private EditText txtName,txtRefrence,txtXValue,txtJoinDate,txtSZK,txtWHK;
	Button btnStatus,btnDelete;
	Spinner txtJiangBan;
	private String memberId;
	private Member member;
	private EditText txtPinYin;
	String oldrefid;

	protected int getLayout()
	{
		return R.layout.activity_edit_member;
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		txtName = (EditText) findViewById(R.id.edit_member_name);	
		txtPinYin = (EditText) findViewById(R.id.edit_member_pinyin);	
		txtRefrence = (EditText) findViewById(R.id.edit_member_refrence_name);	
		txtJoinDate = (EditText) findViewById(R.id.edit_member_join_date);	
		txtXValue = (EditText) findViewById(R.id.edit_member_xvalue);
		txtSZK = (EditText) findViewById(R.id.edit_member_szk);
		txtWHK = (EditText) findViewById(R.id.edit_member_whk);
		txtJiangBan = (Spinner) findViewById(R.id.edit_member_jiang_ban);
		//txtRefrence.setOnClickListener(new DateClickListener(this));
		txtJoinDate.setOnClickListener(new DateClickListener(this));
		Button btnSave = (Button) findViewById(R.id.edit_member_save_button);	
		btnStatus = (Button) findViewById(R.id.edit_member_set_status_button);	
		btnDelete=(Button) findViewById(R.id.edit_member_delete_button);
		Button btnSwitch=(Button) findViewById(R.id.edit_member_switch_group_button);
		Button btnBealong=(Button) findViewById(R.id.edit_member_bealong_button);
		btnBealong.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View p1)
			{
				ToBealong();				
			}

		
	});
		btnSwitch.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					GetGroup();
					
				}

			
		});
		txtName.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				String str = txtName.getText().toString();
				if(str.isEmpty()) return;
				txtPinYin.setText(Utility.GetPinYin(str));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				 
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				 
			}
			
		});
		btnStatus.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String isOff =null;
					if (btnStatus.getText().toString().equals(getString(R.string.edit_member_button_set_offline)))
					{
						isOff = "true";
						member.Status = -1;
					}
					else
					{isOff = "false";
						member.Status = 0;
					}
					Log.i("tip", "set offlion" + isOff);
					BasicAccess access =new BasicAccess(EditMemberActivity.this);
					try
					{
						access.Visit(MemberAccess.class).UpdateStatus(member.ID, member.Status);
					}	
					catch (Exception e)
					{
						ws.Toast(e.getMessage());
						access.Close(false);
						return;
					}						
					access.Close(true);
					setResult(RESULT_OK);
					finish();
					//ws.visitServices("Member_SetOnOffline", new String[]{"m_id","isOff"}, new String[]{String.valueOf(memberId),isOff}, SetStatus);
				}
			});
			
		btnDelete.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					BasicAccess access =new BasicAccess(EditMemberActivity.this);
					try
					{
						access.Visit(MemberAccess.class).Delete(member.ID);
					}	
					catch (Exception e)
					{
						ws.Toast(e.getMessage());
						access.Close(false);
						return;
					}						
					access.Close(true);
					setResult(RESULT_OK);
					finish();
				}
			});

		btnSave.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if (member == null) member = new Member();
					member.Name = txtName.getText().toString();
					member.PinYinJ = txtPinYin.getText().toString();
					member.XValue = Integer.valueOf(txtXValue.getText().toString());
					member.WenHuaKe =Integer.valueOf(txtWHK.getText().toString());
					member.SuZhiKe =Integer.valueOf(txtSZK.getText().toString());
					member.JoinDate = txtJoinDate.getText().toString();
					if (member.Name.isEmpty())
					{
						ws.Toast(R.string.please_input_task_content);
						return;
					}
					if (member.JoinDate.isEmpty())
					{
						ws.Toast("please choose join date");
						return;
					}
					if (member.ReferenceID == null && oldrefid!=null)
					{
						ws.Toast("please choose a refrence ");
						return;
					}
					BasicAccess access =new BasicAccess(EditMemberActivity.this);
				//	Log.i("tip", "member:" + member.ToJson());
					try {
						if (member.ID == null){
							member.GroupID = getCurrentGroupId();						
								access.Visit(MemberAccess.class).Add(member);						
							//ws.visitServices("Member_AddJson", "memberJson", member.ToJson(), Add);
						}
						else
							access.Visit(MemberAccess.class).Update(member);
							//ws.visitServices("Member_UpdateJson", "memberJson", member.ToJson(), Update);
					} catch (Exception e) {
						ws.Toast(e.getMessage());
						return;
					}
					setResult(RESULT_OK);
					finish();
				}
			});
		if (this.getIntent().getExtras() != null)
			memberId = this.getIntent().getExtras().getString("memberID");
		if (memberId == null)
		{
			member = new Member();
			btnStatus.setEnabled(false);
		}
		BindSpinner();
		txtRefrence.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v)
				{				
					memberSelectDialog.Show();				
				}
			});
		runQueryMembers();
	}

	private final int Query =0,Add =1,Update =2,SetStatus =3,QueryAllMember=4;
	String[] arrJiangBan ={"没有","1班", "2/4班", "3班" ,"5班"};
	private void BindSpinner()
	{

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrJiangBan);  
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		txtJiangBan.setAdapter(adapter);
		txtJiangBan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
										   int arg2, long arg3)
				{
					if (member == null) return;
					if (arg2 == 0) member.JiangBan = null;
					else
					{
						if (arg2 == 2)	member.JiangBan = "2/4";
						else if(arg2 ==4) member.JiangBan = "5";
						else member.JiangBan = String.valueOf(arg2);
					}
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{		
					if (member == null) return;
					member.JiangBan = null;
				}
			});
	}
	private static LinkedList<Member> memberlst;
	
	private void ToBealong(){	
		BasicAccess access =new BasicAccess(EditMemberActivity.this);
		try
		{
			member.ReferenceID = null;
			access.Visit(MemberAccess.class).Update(member);
		}	
		catch (Exception e)
		{
			ws.Toast(e.getMessage());
			access.Close(false);
			return;
		}						
		access.Close(true);
		setResult(RESULT_OK);
		finish();
	}
	
	private void GetGroup(){
		
			String[] arr = new String[  getApp().getCurrentUser().Groups.length];
			int selectedIndex =0;
			for(int i =0;i< arr.length;i++) {
				arr[i] = getApp().getCurrentUser().Groups[i].Name;
				if(getApp().getCurrentUser().Groups[i].ID == getCurrentGroupId()) selectedIndex = i;
			}
			new AlertDialog.Builder(this)
				.setTitle(R.string.please_choose_a_group)
				.setSingleChoiceItems(arr, selectedIndex, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which)
					{  
						Group group = getApp().getCurrentUser().Groups[which];							 
						dialog.dismiss();
						if(group.ID== getCurrentGroupId()) return;
						BasicAccess access =new BasicAccess(EditMemberActivity.this);
						try
						{
							access.Visit(MemberAccess.class).SwitchGroup(member.ID,group.ID);
						}	
						catch (Exception e)
						{
							ws.Toast(e.getMessage());
							return;
						}						
						access.Close(true);
						ws.Toast("switch ok");
						
					}
				})            
				.show();
		
	}
	

	public void runQueryMembers()
	{
		if (memberlst != null)
		{
			Log.i("tip", "memberlst is not null , use and call bind");
			BindMemberDialog();
			return;
		}
		Log.i("tip", "run query member list");
		if (Utility.UseLocal)
		{
			memberlst =	ws.QueryLocalMember(false,null);
			BindMemberDialog();			
		}
		else
		{
			ws.visitServices("Member_GetAllMemberJson", "tree", "false", QueryAllMember);			
		}
	}
	MemberSelectDialog memberSelectDialog;

	private void BindMemberDialog()
	{
		if (memberSelectDialog == null)
		{
			Log.i("tip", "bind member select dialog");
			memberSelectDialog = new MemberSelectDialog(this,					
				memberlst,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (memberSelectDialog.SelectedMember == null) return;
						member.ReferenceID = memberSelectDialog.SelectedMember.ID;
						txtRefrence.setText(memberSelectDialog.SelectedMember.Name);
						//	askForLeave.MemberID = memberSelectDialog.SelectedMember.ID;

					}
				});
		}
		else
		{
			Log.i("tip", "member dialog exists , not need init!");
		}
		if (memberId != null)
		{
			QueryMember();
		}
	}

	private void QueryMember()
	{
		Log.i("tip", "run query member , id is " + memberId);
		if (Utility.UseLocal)
		{
			String sql ="select * from Member where ID ='" + memberId+"'";
			EntityDBHelper<Member> helper =new EntityDBHelper<Member>(ws.GetSqlite(), Member.class);
			member = helper.QuerySingle(sql);
			BindMember();
		}
		else
		{ 
			ws.visitServices("Member_GetJson", "m_id", String.valueOf(memberId), Query);
		}
	}


	private void BindMember()
	{
		if (member == null)
		{
			ws.Toast("member is no found with id:" + memberId);
			finish();
		}
		Log.i("tip", "bind member value to controls");
		txtName.setText(member.Name);
		txtPinYin.setText(member.PinYinJ);
		oldrefid=member.ReferenceID;
		if (member.ReferenceID !=null )
		{
			for (Member m :memberlst)
			{
				if (m.ID .equals(member.ReferenceID))
				{
					Log.i("tip", "set reference " + m.Name);
					txtRefrence.setText(m.Name);
				}
			}
		}
		if (member.JoinDate != null)
			txtJoinDate.setText(member.JoinDate);
		txtXValue.setText(String.valueOf(member.XValue));
		txtSZK.setText(String.valueOf(member.SuZhiKe));
		txtWHK.setText(String.valueOf(member.WenHuaKe));
		if (member.JiangBan != null)
		{
			Log.i("tip", "set jiangBan : " + member.JiangBan);
			for (int i=0;i < arrJiangBan.length;i++)
			{
				if (arrJiangBan[i] .equals(member.JiangBan + "班"))
				{
					txtJiangBan.setSelection(i);
					break;
				}
			}
		}
		if (member.Status == -1)
		{
			Log.i("tip", "set can online text");
			btnStatus.setText(R.string.edit_member_button_set_online);
		}
	}




	public void onHandleMessage(Message msg)
	{
		super.onHandleMessage(msg);	
		switch (msg.what)
		{
			case QueryAllMember:
				memberlst = Member.ListFromJson(ws.queryResult);
				BindMemberDialog();
				break;
			case Query:
				member = Member.FromJson(ws.queryResult);
				BindMember();
				break;
			case SetStatus:
				ws.Toast("set success");
				BindMember();
				break;	
			case Add:
				ws.Toast(R.string.add_finish_message);
				break;
			case Update:
				//setResult(RESULT_OK);
				//finish();
				ws.Toast(R.string.update_finish_message);
				break;		
		}
	}
}
