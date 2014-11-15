package com.colys.tenmillion; 
import CustomViews.*;
import DataAccess.*;
import android.annotation.*;
import android.content.*;
import android.content.res.*; 
import android.os.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;

import com.colys.tenmillion.Entity.*; 

import java.util.*; 
@SuppressLint("ValidFragment")
public class MonthPeopleComingFragment extends TabViewFragment
{ 
	public MonthPeopleComingFragment(){}


	int year,month;
	PeopleComing selectedPeopleComing;	 
	TextView txtQueryMonth;
	ListView listview ;
	Button btnPrev,btnNext;
	LinkedList< PeopleComing> peopleComingList;
	

	@Override
	public int getLayout()
	{
		return R.layout.activity_month_people_coming;
	}

	@Override
	public void onCreateView(View rootView)
	{	 

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		listview = (ListView) rootView.findViewById(R.id.month_people_coming_listview);
		txtQueryMonth =(TextView) rootView.findViewById(R.id.month_people_coming_query_date);
		btnPrev=(Button) rootView.findViewById(R.id.month_people_coming_prev_month);
		btnNext=(Button) rootView.findViewById(R.id.month_people_coming_next_month);
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
						selectedPeopleComing = (PeopleComing) ((MonthComingGroupListAdapter) listview.getAdapter()).getItem(minfo.position);                	 
						if (selectedPeopleComing.ID == null|| selectedPeopleComing.MemberID == null) return;
						menu.addSubMenu(20, 0, 0, "查看新人资料");
						if (selectedPeopleComing.Status > 0)
						{
							menu.addSubMenu(21, 0, 1, "查看走工作记录");
						}
						menu.addSubMenu(22,0,1,"删除");
					}
				}
			});
	}


	@Override
	public void FirstShow()
	{ 
		super.FirstShow();
		runQueryPlan();
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig)
	{		
	    super.onConfigurationChanged(newConfig);
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
		MonthComingGroupListAdapter listItemAdapter =new MonthComingGroupListAdapter(getActivity(), peopleComingList, year, month);
		listview.setAdapter(listItemAdapter);
		View emptyView = getActivity().findViewById(R.id.month_people_coming_listview_empty);
		listview.setEmptyView(emptyView);
	}


	final int QueryComingList=0;

	@SuppressLint("SimpleDateFormat")
	public void runQueryPlan()
	{
		if (Utility.UseLocal)
		{
			peopleComingList =  m_Access.Visit(PeopleComingAccess.class).GetMonthComingList(year, month, null, null,  getCurrentGroupId());
			BindListView(); 
			m_Access.Close(true);
		}
		else
		{
			ws.visitServices("Coming_GetMonthComingListJson", new String[]{"year","month","groupID"}
							 , new String[]{
								 String.valueOf(year)
								 ,String.valueOf(month)
								 ,String.valueOf(getCurrentGroupId())
							 }
							 , QueryComingList);
		}

	}

	@Override  
	public boolean onContextItemSelected(MenuItem item)
	{  
		int groupId= item.getGroupId();
		if(groupId< 20 || groupId>29) return super.onContextItemSelected(item);
		if (selectedPeopleComing == null) return false;
		Intent tent= new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("comingID", selectedPeopleComing.ID); 
		tent.putExtras(bundle);
		if (groupId == 20)
		{
			tent.setClass(getActivity(), PeopleComingActivity.class);
			startActivityForResult(tent, 0);
		}
		else if (groupId== 21)
		{
			tent.setClass(getActivity(), PeopleWorkingActivity.class);
			startActivityForResult(tent, 0);
		}
		else if(groupId==22){
			try
			{
				m_Access.Visit(PeopleComingAccess.class).Delete(selectedPeopleComing.ID);
				runQueryPlan();
			}
			catch (Exception e)
			{
				ws.Toast(e.getMessage());
			}
			m_Access.Close(true);
		}
		return super.onContextItemSelected(item);
	}


}
