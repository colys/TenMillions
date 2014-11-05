package com.colys.tenmillion;

import java.util.LinkedList;

import com.colys.tenmillion.Entity.PeopleComing; 

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.PeopleComingAccess;

public class ChoosePeopleComingActivity extends WSActivity  {
	
	ListView listView;
	String strWorkDate;
	String strHouseID;
	private LinkedList<PeopleComing> lst;
	
	protected int getLayout()
	{
		return R.layout.activity_choose_people_coming;
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		listView= (ListView) findViewById(R.id.choose_people_coming_listview);
		strWorkDate = this.getIntent().getExtras().getString("workdate");
		strHouseID = this.getIntent().getExtras().getString("house");
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 Intent intent = new Intent();
				 PeopleComing pc = lst.get(arg2);
				 intent.putExtra("comingid",pc.ID);
				 setResult(RESULT_OK, intent);				
				 finish();
			}
			
		});
		LoadData();		
	}
	
	public void LoadData(){
		BasicAccess access = new BasicAccess(this);
		lst= access.Visit(PeopleComingAccess.class).GetOnlineOrWillComingList(getApp().getCurrentGroupID() , strWorkDate);
		String[]  listItem = new String[lst.size()];
		int i=0;
        for(PeopleComing pc : lst)
        {  				    
        	String str= pc.ArriveDate +"  "+  pc.MemberName+pc.Relation;
        	if(pc.ComeFrom !=null &&  !pc.ComeFrom.isEmpty() ) str +="("+ pc.ComeFrom +")";
        	listItem[i++] = str;        			
        }
        
        ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItem );
        listView.setAdapter(listItemAdapter);
	}
}
