package com.colys.tenmillion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.colys.tenmillion.Entity.PeopleComing;
import com.colys.tenmillion.Entity.TrainItem;
import com.colys.tenmillion.Entity.TrainPlan;
import com.colys.tenmillion.Entity.TrainRecord;

import CustomViews.MonthComingGroupListAdapter;
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.TrainPlanAccess;
import android.widget.*;

public class TrainPlanQueryActivity extends WSActivity{
	int year,month;
	TrainPlan selectedPlan;	 
	TextView txtQueryMonth;
	ListView listview ;
	Button btnPrev,btnNext,btnAdd;
	LinkedList<TrainPlan> planList;
	BasicAccess access;

	@Override
	public int getLayout()
	{
		return R.layout.activity_train_plan_list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	 
		access =new BasicAccess(this);
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		listview = (ListView) findViewById(R.id.train_plan_listview);
		txtQueryMonth =(TextView) findViewById(R.id.train_plan_list_query_month);
		btnPrev=(Button) findViewById(R.id.train_plan_list_prev_month);
		btnNext=(Button) findViewById(R.id.train_plan_list_next_month);
		btnAdd =(Button) findViewById(R.id.train_plan_list_add_button);
		btnPrev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(month ==1) {
					year--;
					month =12;
				}else month--;
				runQueryPlan();
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(month ==12) {
					year++;
					month =1;
				}else month++;
				runQueryPlan();
			}
		});
		
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(! getApp().getCurrentUser().InitTrains){
					ws.Toast("不好意思，您没有权限!");
					return;
				}
				try {
					if(access.Visit(TrainPlanAccess.class).ExistsUnFinish()){
						ws.Toast("有未回填的培训计划，请落实");
					}
					else{
						//turn to edit page
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), EditTrainPlanActivity.class);
						startActivityForResult(intent, 0);
					}
				} catch (Exception e) {
					ws.Toast(e.getMessage());
				}
				access.Close(true);
			}
		});
		
		listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
				{ 
					AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
					if (minfo.position == -1)
					{
						return;
					}
					else
					{
						selectedPlan =  planList.get(minfo.position);        	  
						menu.addSubMenu(20, 0, 0, "编辑");
						menu.addSubMenu(21, 0, 0, "删除");
						
					}
				}
			});
		
		runQueryPlan();
	}





	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == android.app.Activity.RESULT_OK)
		{
			if (requestCode == 0)
			{
				runQueryPlan();
			}			
		}
	}

//	public void onHandleMessage(Message msg)
//	{
//		super.onHandleMessage(msg);	
//		switch (msg.what)
//		{			
//			case QueryComingList:
//				if (listview != null)
//				{
//					peopleComingList = PeopleComing.ListFromJson(ws.queryResult);							
//					BindListView();
//				}
//				break;
//			
//		}
//
//	}
	

	private void BindListView()
	{
		txtQueryMonth.setText(year+"年"+month+"月");
		java.util.List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		for(TrainPlan plan : planList){
			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("plan", plan.Name);
			 
			String strTrains = "";
			for(TrainItem item :  plan.GetTrainItems() ){
				strTrains+= item.Name+":";
				for(TrainRecord record :  item.Records){
					strTrains+=" "+ record.MemberName;
				}
				strTrains+="\r\n";
			}
			map.put("trains", strTrains);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter =new SimpleAdapter(
				this,
	    		listItem,
	            R.layout.item_train_plan_records,
	            new String[] {"plan","trains"},
	            new int[] {R.id.item_train_plan_name_label,R.id.item_train_string_label }
	        ); 
		listview.setAdapter(listItemAdapter);
		View emptyView = findViewById(R.id.train_plan_listview_empty);
		listview.setEmptyView(emptyView);
	}


	final int QueryComingList=0;

	@SuppressLint("SimpleDateFormat")
	public void runQueryPlan()
	{
		 
			planList =  access.Visit(TrainPlanAccess.class).QueryMonth(year, month, getCurrentGroupId());
			BindListView(); 
			access.Close(true);
		 
	}

	@Override  
	public boolean onContextItemSelected(MenuItem item)
	{  
		if (selectedPlan == null) return false;
		int groupId= item.getGroupId();
		switch(groupId){
			case 20:
			Intent tent= new Intent();
			Bundle bundle=new Bundle();
			bundle.putString("planID", selectedPlan.ID); 
			tent.putExtras(bundle);		
			tent.setClass(this, EditTrainPlanActivity.class);		
			startActivityForResult(tent, 0);
			break;
			case 21:
				try
				{
					access.Visit(TrainPlanAccess.class).Delete(selectedPlan.ID);
					runQueryPlan();
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

}
