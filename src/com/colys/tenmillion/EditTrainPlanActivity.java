package com.colys.tenmillion;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.colys.tenmillion.Entity.AskForLeave;
import com.colys.tenmillion.Entity.Member;
import com.colys.tenmillion.Entity.TrainItem;
import com.colys.tenmillion.Entity.TrainPlan;
import com.colys.tenmillion.Entity.TrainRecord;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import CustomViews.MemberSelectDialog;
import CustomViews.WSActivity;
import DataAccess.BasicAccess; 
import DataAccess.TrainItemAccess;
import DataAccess.TrainPlanAccess;
import DataAccess.TrainRecordAccess;

public class EditTrainPlanActivity extends WSActivity {
	BasicAccess access =new BasicAccess(this);
	private EditText txtName;
	private ListView listview;
	private Button btnAdd;
	private TrainPlan plan;
	private String id;
	private AlertDialog.Builder   chooseMemberDialog,chooseItemDialog;
	private LinkedList<TrainItem> m_allTrainItems; 
	private boolean[] memberCheckeds;
	LinkedList<Member> members ;
	int selectListViewPos ;
	boolean hasModify = false;
	private Button btnSave;
	@Override
	public int getLayout()
	{
		return R.layout.activity_edit_train_plan;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	 
		txtName =(EditText) findViewById(R.id.edit_train_plan_name);
		btnAdd =(Button) findViewById(R.id.train_plan_list_add_button);		
		btnSave =(Button) findViewById(R.id.train_plan_save_button);		
		
		listview = (ListView) findViewById(R.id.edit_train_plan_listview);
		listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{ 
				AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
				if (minfo.position == -1)
				{
					selectedTrainItem =null;
					selectListViewPos = -1;
					selectedTrainRecord = null;
					return;
				}
				else
				{			
					selectListViewPos = minfo.position;
					selectedTrainItem = plan.GetTrainItems().get(selectListViewPos);
					 
					menu.addSubMenu(1, minfo.position, 0, "添加人员");
					menu.addSubMenu(5, minfo.position, 0, "添加人员(拼音检索)");
					for(int i =0;i< selectedTrainItem.Records.size();i++){						 
							menu.addSubMenu(2, i, 0, "移除"+ selectedTrainItem.Records.get(i).MemberName );						 
					}
					menu.addSubMenu(3, 1, 0, "标记全部为已培训");
					for(int i =0;i< selectedTrainItem.Records.size();i++){						 
						menu.addSubMenu(4, i, 0, "标记"+ selectedTrainItem.Records.get(i).MemberName );						 
				}
				}
			}
		});
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View arg0) {
				if(plan==null) return;
				plan.Name = txtName.getText().toString();
				if(plan.Name ==null) return;
				try {
					access.Visit(TrainPlanAccess.class).Update(plan);
					hasModify=true;
					ws.Toast("OK,已经保存!");
				} catch (Exception e) {
					ws.Toast(e.getMessage());
				}
				access.Close(true);
				
			}
		});
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
					
			

				@SuppressLint("SimpleDateFormat")
				@Override
				public void onClick(View arg0) {
					
					//pop add
					
					String[] ItemArray=new String[m_allTrainItems.size()];
					for (int i =0;i < ItemArray.length;i++)
					{
						ItemArray[i] = m_allTrainItems.get(i).Name;
					}
					if (chooseItemDialog == null)
					{
						chooseItemDialog = new AlertDialog.Builder(EditTrainPlanActivity.this)
							.setTitle(R.string.day_work_pop_select_house_title)
							.setSingleChoiceItems(ItemArray, 0, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which)
								{  
									TrainItem item  = m_allTrainItems.get(which);
									 //if already cloose ,return									 
									if(plan.FindTrainItem(item.Name)!=null ){
										ws.Toast("已经存在了!");
										return;
									}
									/*TrainRecord tr =new TrainRecord();
									tr.ItemID = item.ID;
									tr.ItemName = item.Name;
									tr.PlanID = id;*/
									//plan.AddRecord(tr);
									//构造一个空的
									TrainItem newItem =new TrainItem();
									newItem.ID = item.ID;
									newItem.Name = item.Name;
									newItem.Records=new LinkedList<TrainRecord>();
									plan.GetTrainItems().add(newItem);
									BindListView();
									dialog.dismiss();									
									
								}
							})            
							.setNegativeButton(R.string.cancel_button_text, null);	    		
					}
					chooseItemDialog.show(); 
				}
		});
		m_allTrainItems = access.Visit(TrainItemAccess.class).QueryALL();
		if(getIntent().getExtras()!=null){
			id = getIntent().getExtras().getString("planID");
			runQueryPlan();
		}else{			
			plan = new TrainPlan();
			Calendar cal = Calendar.getInstance() ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			plan.GroupID = getCurrentGroupId();
			plan.ApplyDate = sdf.format(cal.getTime());
			plan.IsFinish = false;
			String mWay = String.valueOf(cal.get(Calendar.DAY_OF_WEEK)); //星期，从周天开始为1
			int way = Integer.parseInt(mWay);
	         int week = cal.get(Calendar.WEEK_OF_MONTH); 				          
	         if (way==1) {
	        	 way=7;  
	             week=week-1;  
	         } else {  
	        	 way=way-1;  
	         }
			if(way > 4){
				//周五开始，录入的算下周培训，否则就是本周培训
				if(week ==4){
					week =1;//最后一周，算下月第一周
					cal.add(Calendar.MONTH, 1);
				}
				else week++;							
			}
			plan.Name = cal.get(Calendar.YEAR)+ "年"+ (cal.get(Calendar.MONTH)+1)+"月第"+ week +"周";
			txtName.setText(plan.Name);
			try {
				hasModify = true;
				access.Visit(TrainPlanAccess.class).Add(plan);
			} catch (Exception e) {
				ws.Toast(e.getMessage());
				access.Close(false);
				return;
			}
			access.Close(true);
			id= plan.ID;
		
		}
		
	}
	
	public void runQueryPlan()
	{
		try
		{
			plan = access.Visit(TrainPlanAccess.class).Get(id);
		}
		catch (Exception e)
		{
			ws.Toast(e.getMessage());
			return;
		}		 
		BindListView();
		 access.Close(true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(hasModify) setResult(RESULT_OK);
		}
		return super.onKeyDown(keyCode, event);
	}
	private void BindListView()
	{
		txtName.setText(plan.Name);
		plan.GetTrainItems();
		java.util.List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		 
			
			HashMap<String,String>  trainMembers =new HashMap<String, String>();
			for(TrainItem trainItem : plan.GetTrainItems()){
				HashMap<String, Object> map = new HashMap<String, Object>();  
				map.put("Item", trainItem.Name);
				String Members = "";
				for(TrainRecord tr : trainItem.Records){
					Members+=" "+tr.MemberName;
					switch(tr.Status){
					case 1:
						Members+="(已培)";
						break;
					case 2:
						Members+="(替)";
						break;
					}
				}
				map.put("members", Members);
				listItem.add(map);				
				 
			}
		
		SimpleAdapter listItemAdapter =new SimpleAdapter(
				this,
	    		listItem,
	            R.layout.item_train_plan_records,
	            new String[] {"Item","members"},
	            new int[] {R.id.item_train_plan_name_label,R.id.item_train_string_label }
	        ); 
		listview.setAdapter(listItemAdapter);
		View emptyView = findViewById(R.id.train_plan_listview_empty);
		listview.setEmptyView(emptyView);
	}
	TrainRecord selectedTrainRecord;
	TrainItem selectedTrainItem ;
	MemberSelectDialog memberDlg;
	public boolean onContextItemSelected(MenuItem item)
	{  
		hasModify = true;
		int groupId= item.getGroupId();
		int pos = item.getItemId();
		if(groupId == 1 ){			
			   
				  members = access.Visit(TrainPlanAccess.class).QueryUnTrainMembers(selectedTrainItem.ID,getApp().getCurrentGroupID());
			  
				 
				 access.Close(true);
				String[] ItemArray =  new String[members.size()];
				memberCheckeds =  new boolean[members.size()];
				for(int i =0;i< members.size();i++){
					ItemArray[i]= members.get(i).Name;
					memberCheckeds[i] = true;
				}
				
				new AlertDialog.Builder(EditTrainPlanActivity.this)
					.setTitle(R.string.please_choose_member)
					.setMultiChoiceItems(ItemArray, memberCheckeds, new DialogInterface.OnMultiChoiceClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
						  
									
						}
					})        
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which)
						{					
							access.OpenTransConnect();
							try {
								for(int i =0;i< members.size();i++){
									
									if(memberCheckeds[i]){
										TrainRecord tr =new TrainRecord();
										tr.ItemID = selectedTrainItem.ID;
										tr.ItemName = selectedTrainItem.Name;
										tr.MemberID = members.get(i).ID;
										tr.PlanID = id;
										tr.Status = 0;
										access.Visit(TrainRecordAccess.class).Add(tr);
										
									}
								}
							} catch (Exception e) {
								ws.Toast(e.getMessage());
								access.Close(false);
								return;
							}							
							runQueryPlan();
							access.Close(true);
						}				
					})
					.setNegativeButton(R.string.cancel_button_text, null).show(); 
		}else if(groupId ==2){
			//delete
			String id = selectedTrainItem.Records.get(pos).ID;
			try {
				access.OpenTransConnect();
				access.Visit(TrainRecordAccess.class).Delete(id);
				selectedTrainItem.Records.remove(pos);
			} catch (Exception e) {
				ws.Toast(e.getMessage());
			}
			access.Close(true);
			BindListView();
		}else if (groupId==3){
			try {
				access.OpenTransConnect();
				for(TrainRecord tr : selectedTrainItem.Records){
					tr.Status =1;
					access.Visit(TrainRecordAccess.class).Update(tr);
				}
				
			} catch (Exception e) {
				access.Close(false);
				ws.Toast(e.getMessage());
			}
			access.Close(true);
			BindListView();
			//标记全部
		}else if (groupId==4){		
			//标记结果			
			selectedTrainRecord = selectedTrainItem.Records.get(pos);
			String[] ItemArray = new String[]{"未开始","已培训","他人替代"};
			 
			 
			new AlertDialog.Builder(EditTrainPlanActivity.this)
			.setTitle("标记"+ selectedTrainRecord.MemberName +"的培训结果为：")
			.setSingleChoiceItems(ItemArray,selectedTrainRecord.Status, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectedTrainRecord.Status = which;
					try {
						access.OpenTransConnect();
						access.Visit(TrainRecordAccess.class).Update(selectedTrainRecord);
					} catch (Exception e) {
						ws.Toast(e.getMessage());
					}
					access.Close(true);
					BindListView();
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancel_button_text, null).show(); 
			
		} else if(groupId ==5){
			//members = access.Visit(TrainPlanAccess.class).QueryReadyTrainMembers(selectedTrainItem.ID);
			members= ws.QueryLocalMember(false, access);
			access.Close(true);
			memberDlg =new MemberSelectDialog(EditTrainPlanActivity.this,					
					members,
					R.string.please_choose_member,
					new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TrainRecord tr =new TrainRecord();
					tr.ItemID = selectedTrainItem.ID;
					tr.ItemName = selectedTrainItem.Name;
					tr.MemberID = memberDlg.SelectedMember.ID;
					tr.MemberName = memberDlg.SelectedMember.Name;
					tr.PlanID = id;
					tr.Status = 0;
					try {
						access.Visit(TrainRecordAccess.class).Add(tr);
						plan.AddRecord(tr);
					} catch (Exception e) {
						ws.Toast(e.getMessage());
					}
					access.Close(true);
					BindListView();
				}
			});
			memberDlg.Show();
		}
		return super.onContextItemSelected(item);
	}
	
	 
}
