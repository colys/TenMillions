package com.colys.tenmillion;

import CustomViews.*;
import DataAccess.*;
import android.annotation.SuppressLint;
import android.database.sqlite.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.colys.tenmillion.Entity.*;

import java.util.*;

@SuppressLint("ValidFragment")
public class ClassifyMemberFragment extends TabViewFragment {

	 public ClassifyMemberFragment(){}

	public ClassifyMemberFragment(WSView ws, Handler h) {
		super(ws, h); 
	}

	private ListView listview;
	LinkedList<Member> memberList;

	@Override
	protected int getLayout() { 
		return R.layout.activity_classify_member;
	}
	
	public void onCreateView(View rootView) {
		listview = (ListView) rootView.findViewById(R.id.classify_member_listview);
		Log.i("tip","init classfiy frament : root is :"+ rootView.getClass().getName());
	}
	
	@Override
	public void FirstShow(){
		super.FirstShow();
		try{
		QueryMemberTree();
		Log.i("tip","query member is call, wait");
		}
		catch (SQLiteException e)
		{
			e.printStackTrace();
			MainActivity.ShowError(this.getActivity(),e);
		}
	}
	
	EntityDBHelper<Member> helper;
	
	public void QueryMemberTree(){
		if(Utility.UseLocal){
			memberList = ws.QueryLocalMember(true,null);
			BindListView();
		}else{
			ws.visitServices("Member_GetAllMemberJson",new String[]{ "tree","groupID"},new String[]{"true",String.valueOf(getApp().getCurrentGroupID())}, 0);			
		}
	}
	
	private void BindListView(){ 
		
		/*String[]  listItem = new String[lst.size()];
        for(int i=0;i< lst.size();i++)
        {  				    
        	MonthPlan mp = lst.get(i);
        	if(mp.Remark==null) mp.Remark="";
        	if(mp.Name == null) mp.Name="unknow";
        	listItem[i]= mp.Name +" "+ mp.FenE +"��,"+ mp.PiShu+"�� "+ mp.Remark;  
        }		      */   
        ClassifyMemberListAdapter listItemAdapter =new ClassifyMemberListAdapter(getActivity(),memberList);
        listview.setAdapter(listItemAdapter);
        View emptyView = getActivity().findViewById(R.id.month_people_coming_listview_empty);
        listview.setEmptyView(emptyView);		 
	}

//	public void onHandleMessage(Message msg){
//		super.onHandleMessage(msg);	
//		
//		Log.i("tip","call back ,calc member");
//		memberList = Member.ListFromJson(ws.queryResult);
//		BindListView();
//		Log.i("tip","calc end, show");
//	}

}
