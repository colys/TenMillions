package com.colys.tenmillion;

import CustomViews.*;
import DataAccess.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import com.colys.tenmillion.Entity.*;
import java.util.*;

public class PeopleWorkingActivity extends WSActivity
{
	String comingid;
	PeopleComing pc;

	private PeopleWorking SelectedWorkItem =null;
	House selectedHouse;
	Button	addBtn,leaveBtn,finishBtn;
	EditText txtFenE,txtHiddenDay,txtName;
	DateClickListener  dateClickListener =new DateClickListener(PeopleWorkingActivity.this);

	protected int getLayout()
	{
		return R.layout.activity_people_working;
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); 	
		comingid = getIntent().getExtras().getString("comingID");	
		Button	gotoBtn	=(Button) findViewById(R.id.people_working_goto_coming);
		addBtn	= (Button) findViewById(R.id.people_working_add);
		finishBtn	= (Button) findViewById(R.id.people_working_is_finish);
		leaveBtn	= (Button) findViewById(R.id.people_working_isLeave);
		txtHiddenDay = (EditText) PeopleWorkingActivity.this.findViewById(R.id.people_working_hidden_day);

		addBtn.setOnClickListener(new	View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					Intent tent= new Intent();
					Bundle bundle=new Bundle();
					bundle.putString("json", "");
					bundle.putString("comingID", comingid);
					bundle.putString("houseID", pc.HouseID);
					int maxDay =0;
					for (PeopleWorking pw : pc.Workings)
					{
						if (pw.DayCount > maxDay) maxDay = pw.DayCount;
					}
					maxDay++;
					bundle.putInt("day", maxDay);
					tent.putExtras(bundle);
					tent.setClass(PeopleWorkingActivity.this, EditPeopleWorkingActivity.class);
					startActivityForResult(tent, 0);

				}
			});

		gotoBtn.setOnClickListener(new	View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					Intent tent= new Intent();
					Bundle bundle=new Bundle();
					bundle.putString("comingID", comingid);
					tent.putExtras(bundle);
					tent.setClass(PeopleWorkingActivity.this, PeopleComingActivity.class);
					startActivity(tent);

				}
			});

		finishBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub
					//Coming_UpdateComingStatus = 1;  SGFenE
					pc.Status = 2;
					FillEmptyDate();				


				}
			});

		leaveBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					new AlertDialog.Builder(PeopleWorkingActivity.this)
						.setTitle("走是走了，那看懂了吗？")
						.setIcon(android.R.drawable.ic_dialog_info)                
						.setSingleChoiceItems(new String[] {"看懂了","太笨，看不懂"}, 0, 
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
								pc.Status = which + 3;
								FillEmptyDate();
							}
						}
					)
						.setNegativeButton(R.string.cancel_button_text, null)
						.show();



					//pc.LeaveDate = dp
				}
			});


		dateClickListener.SetOnDateSetListener(new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
				{				
					if (dataFlag == 1) pc.WorkDate = txtHiddenDay.getText().toString();
					else if (dataFlag == 2)pc.LeaveDate = txtHiddenDay.getText().toString();
					FillEmptyDate();
				}
			});

//		//query data
		if (comingid !=null )	QueryComing();	


	}

	int dataFlag=0;




	private void FillEmptyDate()
	{
		if (pc.WorkDate == null)
		{
			Toast toast = Toast.makeText(PeopleWorkingActivity.this , R.string.please_choose_work_date_message, Toast.LENGTH_LONG); 
			toast.show();
			dateClickListener.onClick(txtHiddenDay);
			dataFlag = 1;
			return ;

		}
		if (pc.LeaveDate == null)
		{
			Toast toast = Toast.makeText(PeopleWorkingActivity.this , R.string.please_choose_leave_date_message, Toast.LENGTH_LONG); 
			toast.show();
			dateClickListener.onClick(txtHiddenDay);
			dataFlag = 2;
			return ;			 
		}


		//ִ�б���
		if (pc.Status == 2)
		{	//�깺�ˣ�Ҫ����������ݶ�
			//����
			if (pc.Name == null || pc.Name.isEmpty())
			{
				txtName = new EditText(PeopleWorkingActivity.this);
				txtName.setInputType(InputType.TYPE_CLASS_TEXT);
				new AlertDialog.Builder(PeopleWorkingActivity.this)
					.setTitle(R.string.people_coming_name_label)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(txtName)
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which)
						{						 
							pc.Name = txtName.getText().toString();
							FillEmptyDate();
							return;
						}
					})
					.setNegativeButton(R.string.cancel_button_text, null)
					.show();
				return;
			}

			//�ݶ�
			if (pc.SGFenE == 0)
			{
				txtFenE = new EditText(PeopleWorkingActivity.this);
				txtFenE.setInputType(InputType.TYPE_CLASS_NUMBER);
				new AlertDialog.Builder(PeopleWorkingActivity.this)
					.setTitle(R.string.how_much_sg_fen_e_message_title)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(txtFenE)
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which)
						{						 
							pc.SGFenE = Integer.valueOf(txtFenE.getText().toString());
							FillEmptyDate();
							return;
						}
					})
					.setNegativeButton(R.string.cancel_button_text, null)
					.show();
				return;
			}
		}
		SaveFinish();

	}






	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			int str = requestCode == 0 ? R.string.add_finish_message : R.string.update_finish_message ;
			Toast toast = Toast.makeText(PeopleWorkingActivity.this , str, Toast.LENGTH_LONG); 
			toast.show();
			setResult(RESULT_OK); 
			QueryComing();
		}
	}

	private void QueryComing()
	{
		if (Utility.UseLocal)
		{
			String sql ="select PeopleComing.*,Member.Name MemberName from PeopleComing join Member on Member.ID = PeopleComing.MemberID where PeopleComing.ID = '" + comingid+"'";
			EntityDBHelper<PeopleComing> helper =new EntityDBHelper<PeopleComing>(ws.GetSqlite(), PeopleComing.class);
			pc = helper.QuerySingle(sql);
			sql = "select * from PeopleWorking where ComingID = '" + comingid + "' order by dayCount";
			EntityDBHelper<PeopleWorking> workhelper =new EntityDBHelper<PeopleWorking>(ws.GetSqlite(), PeopleWorking.class);
			pc.Workings = workhelper.QueryList(sql);
			BindPeopleWorks();
		}
		else
		{ 
			ws.visitServices("Coming_GetComingJson", new String[]{"id","withWorking"}, new String[]{String.valueOf(comingid),"true"}, QueryComing);
		}

	}

	private void SaveFinish()
	{
		BasicAccess access =new BasicAccess(this);
		try {
			access.OpenTransConnect();
			access.Visit(PeopleComingAccess.class).UpdateComing(pc,getApp().getCurrentGroupID());
			access.Close(true);
			Intent intent = new Intent(PeopleWorkingActivity.this, DayWorkFragment.class);  
			setResult(RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			ws.Toast(e.getMessage());
			access.Close(false); 
		}
		
		//ws.visitServices("Coming_UpdateComingJson", "comingJson", pc.ToJson(), SaveComing);		
	}

	final int SaveComing=0, QueryComing=1;

	private void BindPeopleWorks()
	{
		if (pc.Status != 1 )
		{
			addBtn.setEnabled(false);
			leaveBtn.setEnabled(false);
			finishBtn.setEnabled(false);
		}
		if (pc.Workings != null)
		{
			Activity activity = (Activity) PeopleWorkingActivity.this;
			//list
			ListView list = (ListView) activity.findViewById(R.id.people_working_listview);
			//��ɶ�̬���飬�������  
			java.util.List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
	        for (PeopleWorking pw:pc.Workings)
	        {  
	            HashMap<String, Object> map = new HashMap<String, Object>();  
	            map.put("DayCount", pw.DayCount + ". ");  
	            map.put("Result", pw.Result);  		          
	            listItem.add(map);  
	        }  
			//����������Item�Ͷ�̬�����Ӧ��Ԫ��  				
	        SimpleAdapter listItemAdapter =new SimpleAdapter(
				activity,
        		listItem,
	            R.layout.item_people_working,
	            new String[] {"DayCount","Result"},
	            new int[] {R.id.people_working_listitem_day,R.id.people_working_listitem_result}  
	        );

	        //��Ӳ�����ʾ  
	        list.setAdapter(listItemAdapter);
	        if (pc.Status > 0)
			{
		        //��ӳ������  
		        list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {							
						@Override
						public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
						{ 
							AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
							if (minfo.position == -1) return;
							SelectedWorkItem = pc.Workings.get(minfo.position);				                
							menu.setHeaderTitle("您想做什么？");
							menu.add(0, 0, 0, "修改信息");
							menu.add(1, 1, 0, "删除");
						}
					});
	        }
			if (pc.Workings.size() > 0)
			{
				PeopleWorking lastwk= pc.Workings.getLast();
				if (lastwk.Result == null || lastwk.Result.isEmpty())
				{
					SelectedWorkItem = lastwk;
					GotoSelectWork();
				}
			}
		}//end if
	}

	public void onHandleMessage(Message msg)
	{
		super.onHandleMessage(msg);
		switch (msg.what)
		{
			case SaveComing:
				Intent intent = new Intent(PeopleWorkingActivity.this, DayWorkFragment.class);  
				setResult(RESULT_OK, intent);
				finish();
				break;
			case QueryComing:
				pc = PeopleComing.FromJson(ws.queryResult);
				BindPeopleWorks();				
				break;
		}

	}

	//�����˵���Ӧ����  
    @Override  
    public boolean onContextItemSelected(MenuItem item)
	{  
		switch(item.getGroupId()){
			case 0:
        if (SelectedWorkItem != null)
		{
        	GotoSelectWork();
        }        
		break;
		case 1:
			BasicAccess access=new BasicAccess(this);
				try
				{
					access.Visit(PeopleWorkingAccess.class).Delete(SelectedWorkItem.ID);
					QueryComing();
				}
				catch (Exception e)
				{
					ws.Toast(e.getMessage());
				}
				access.Close(true);
			break;
		
		}
        return super.onContextItemSelected(item);  
    }  
	private void GotoSelectWork()
	{
		Intent tent= new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("json", SelectedWorkItem.ToJson());
		tent.putExtras(bundle);
		tent.setClass(PeopleWorkingActivity.this, EditPeopleWorkingActivity.class);
		startActivityForResult(tent, 1);
	}

}
