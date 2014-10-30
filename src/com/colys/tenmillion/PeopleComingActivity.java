package com.colys.tenmillion; 

import java.util.LinkedList;

import com.colys.tenmillion.Entity.House;
import com.colys.tenmillion.Entity.PeopleComing;  

import CustomViews.DateClickListener; 
import CustomViews.WSActivity; 
import DataAccess.BasicAccess;
import DataAccess.PeopleComingAccess;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle; 
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText; 
import android.widget.Spinner; 
import CustomViews.*;
import com.colys.tenmillion.Entity.*;
import android.widget.*;
import DataAccess.*;




public class PeopleComingActivity extends WSActivity
{


	String comingid;
	PeopleComing pc;
	String inMemberId;
	MemberSelectDialog memberSelectDialog ;
	LinkedList<Member> memberlst;
	private House selectedHouse;
	private CheckBox chkReturn;
	private EditText txtName,txtRelation,txtJob,txtComeFrom,txtRemark;
	private EditText dpArrive,dpWork,dpLeave;
	private Button btnWill,btnIsCome,btnNot,btnModify;
	private Spinner txtHouse;
	final int AddOrUpdate =0, QueryComing=1,QueryHouse =2,QueryMembers =3;
	private LinkedList<House> houseList;
	BasicAccess access ;

	protected int getLayout()
	{
		return R.layout.activity_people_coming;
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	 
		access =new BasicAccess(this);
		chkReturn=(CheckBox) findViewById(R.id.people_coming_daywork_is_return);
		txtName = (EditText) findViewById(R.id.people_coming_name);
		txtRelation = (EditText) findViewById(R.id.people_coming_relation);
		txtJob = (EditText) findViewById(R.id.people_coming_job);
		txtComeFrom = (EditText) findViewById(R.id.people_coming_comefrom);
		txtRemark = (EditText) findViewById(R.id.people_coming_remark);
		dpArrive = (EditText) findViewById(R.id.people_coming_arrive_date);
		dpWork = (EditText) findViewById(R.id.people_coming_work_date);
		dpLeave = (EditText) findViewById(R.id.people_coming_leave_date);
		if (this.getIntent().getExtras() != null)
		{
			comingid = this.getIntent().getExtras().getString("comingID");
			inMemberId = this.getIntent().getExtras().getString("memberID");
		}
		dpArrive.setInputType(android.text.InputType.TYPE_NULL);
		dpWork.setInputType(android.text.InputType.TYPE_NULL);
		dpLeave.setInputType(android.text.InputType.TYPE_NULL);
		dpArrive.setOnClickListener(new DateClickListener(PeopleComingActivity.this));
		dpWork.setOnClickListener(new DateClickListener(PeopleComingActivity.this));
		dpLeave.setOnClickListener(new DateClickListener(PeopleComingActivity.this));

		btnWill = (Button) findViewById(R.id.people_coming_daywork_willcoming_button);
		btnIsCome = (Button) findViewById(R.id.people_coming_daywork_iscoming_button);
		btnNot = (Button) findViewById(R.id.people_coming_daywork_uncoming_button);
		btnModify = (Button) findViewById(R.id.people_coming_info_modify_button);
		txtHouse = (Spinner) findViewById(R.id.people_coming_daywork_house);
		
		btnNot.setEnabled(false);
		btnIsCome.setEnabled(false);

		
//		if (Utility.UseLocal)
//		{
//			btnWill.setEnabled(false);
//			btnModify.setEnabled(false);
//		}
		btnWill.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{
					if(inMemberId == null){
						memberSelectDialog.Show();
						return;
					}
					if (txtRelation.getText().toString().isEmpty())
					{
						new AlertDialog.Builder(PeopleComingActivity.this)      
							.setTitle(R.string.dialog_tip_title)    
							.setMessage(R.string.please_input_relation)    
							.setPositiveButton(R.string.ok_button_text, null)    
							.show();  
						return;
					}
					if (dpArrive.getText().toString().isEmpty())
					{
						new AlertDialog.Builder(PeopleComingActivity.this)      
							.setTitle(R.string.dialog_tip_title)    
							.setMessage(R.string.please_choose_arrive_date_message)    
							.setPositiveButton(R.string.ok_button_text, null)    
							.show();  
						return;
					}
					pc = new PeopleComing();
					pc.MemberID = inMemberId;
					pc.Status =0;	
					FillFromControl();
					try {
						access.Visit(PeopleComingAccess.class).Add(pc);
						FinishAndBack();
					} catch (Exception e) {
						ws.Toast(e.getMessage()); 
					}
					access.Close(true);
				}
			});

		btnIsCome.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0)
				{
					IsComing(true);
				}
			});

		btnNot.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{				
					IsComing(false);
				}
			});
		btnModify.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{
					FillFromControl();
					RunUpdate();
				}
			}); 
		if(comingid == null) runQueryMembers();
		QueryHouse();
	}
	
	private void BindMemberDialog()
	{

		memberSelectDialog = new MemberSelectDialog(this, memberlst, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					inMemberId = memberSelectDialog.SelectedMember.ID;
				}
			});
			memberSelectDialog.Show();
	}



	public void runQueryMembers()
	{
		if (Utility.UseLocal)
		{
			memberlst=	ws.QueryLocalMember(false,access);
			BindMemberDialog();			
		}
		else
		{
			ws.visitServices("Member_GetAllMemberJson", "tree", "false", QueryMembers);			
		}
	}


	private void FillFromControl()
	{
		pc.ArriveDate = dpArrive.getText().toString();
		pc.ComeFrom = txtComeFrom.getText().toString();
	    pc.HouseID = selectedHouse.ID;
		pc.Job = txtJob.getText().toString();
		pc.WorkDate = dpWork.getText().toString();
		if (pc.WorkDate.isEmpty()) pc.WorkDate = null;
		pc.LeaveDate = dpLeave.getText().toString();
		if (pc.LeaveDate.isEmpty()) pc.LeaveDate = null;
		pc.Name = txtName.getText().toString();
		pc.Relation = txtRelation.getText().toString();
		pc.Remark = txtRemark.getText().toString();
		if(chkReturn.isChecked()) {
			if(pc.Remark.indexOf(return_string_in_remark) !=0){
				pc.Remark= return_string_in_remark+pc.Remark;
			}
		}
	}

//	private void AddOrUpdate(String method)
//	{
//		FillFromControl();
//		ws.visitServices(method, "comingJson", pc.ToJson(), AddOrUpdate);
//	}



	private void QueryHouse()
	{
		if (Utility.UseLocal)
		{
			if (houseList == null)
			{
				houseList=access.Visit(HouseAccess.class).Query(false,getCurrentGroupId());
			}
			BindHouse();
		}
		else
		{
			ws.visitServices("DayWork_QueryHouseJson", new String[]{"showTemp","groupID"},new String[]{ "false",String.valueOf(getCurrentGroupId())} , QueryHouse);
		}
	}

	private void IsComing(final boolean isCome)
	{
		pc.Status = isCome ? 1 : -1;
		FillFromControl();
		if (isCome)
		{
			if (selectedHouse == null)
			{
				ws.Toast(R.string.please_choose_house_message);
			}
		}
		RunUpdate();
		//ws.visitServices("Coming_PeopleIsComingJson", new String[]{"isCome", "comingJson"}, new String[]{String.valueOf(isCome), pc.ToJson()}, AddOrUpdate);
	}
	
	private void RunUpdate(){
		
		try {
			access.Visit(PeopleComingAccess.class).UpdateComing(pc,getCurrentGroupId());
			FinishAndBack();
		} catch (Exception e) {
			ws.Toast(e.getMessage());
			e.printStackTrace();
		}
		access.Close(true);
	}

	private void QueryComing()
	{
		if (Utility.UseLocal)
		{
			String sql ="select PeopleComing.*,Member.Name MemberName from PeopleComing join Member on Member.ID = PeopleComing.MemberID where PeopleComing.ID = '" + comingid+"'";
			try
			{
				pc = access.Visit(DefaultAccess.class).QueryEntity(PeopleComing.class, sql);
				BindPeopleComing();
			}
			catch (Exception e)
			{
				ws.Toast(e.getMessage());
				return;
			}
			
		}
		else
		{ 
			ws.visitServices("Coming_GetComingJson", new String[]{"id", "withWorking"}, new String[]{String.valueOf(comingid), "true"}, QueryComing);
		}
	}

	private void BindPeopleComing()
	{
		txtName.setText(pc.Name);
		txtRelation.setText(pc.Relation);
		txtComeFrom.setText(pc.ComeFrom);
		txtJob.setText(pc.Job);
		txtRemark.setText(pc.Remark);
		dpArrive.setText(pc.ArriveDate);
		dpWork.setText(pc.WorkDate);
		dpLeave.setText(pc.LeaveDate);
		for (int i=0;i < houseList.size();i++)
		{
    		if (houseList.get(i).ID.equals(pc.HouseID))
			{
    			txtHouse.setSelection(i);
    			break;
    		}    		
    	}
		btnWill.setEnabled(false);
		if(pc.Status == 0){
			btnIsCome.setEnabled(true);
			btnNot.setEnabled(true);			 
		}else if(pc.Remark!=null && pc.Remark.indexOf(return_string_in_remark)==0  ){
			chkReturn.setChecked(true);
		}
	}
	public static final String return_string_in_remark="[回]";
	private void FinishAndBack(){
		btnWill.setEnabled(false);
		btnIsCome.setEnabled(false);
		btnNot.setEnabled(false);
		setResult(RESULT_OK);
		finish();
	}

	public void onHandleMessage(Message msg)
	{
		super.onHandleMessage(msg);
		switch (msg.what)
		{
			case AddOrUpdate:
				btnWill.setEnabled(false);
				btnIsCome.setEnabled(false);
				btnNot.setEnabled(false);
				setResult(RESULT_OK);
				finish();			
				break;
			case QueryComing:
				pc = PeopleComing.FromJson(ws.queryResult);
				BindPeopleComing();
				btnWill.setEnabled(false);
				if (pc.Status > 0)
				{
					btnIsCome.setEnabled(false);
					btnNot.setEnabled(false);
				}
				else
				{
					btnNot.setEnabled(true);
					btnIsCome.setEnabled(true);
				}
				break;
			case QueryHouse:
				houseList = House.ListFromJson(ws.queryResult);
				BindHouse();			
				break;
			case QueryMembers:
				memberlst = Member.ListFromJson(ws.queryResult);
				BindMemberDialog();					
				break;
		}
	}	

	private void BindHouse()
	{
		String[] arr = new String[houseList.size()];
		for (int i=0;i < houseList.size();i++) arr[i] = houseList.get(i).Name;
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		txtHouse.setAdapter(adapter);
		txtHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


				@Override
				public void onItemSelected(AdapterView<?> arg0,
										   View arg1, int arg2, long arg3)
				{
					selectedHouse = houseList.get(arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{
					selectedHouse = null;
				}

			}
		);

		//query data
		if (comingid != null){	
			QueryComing();	
		
		}
		else btnModify.setEnabled(false);


	}

}
