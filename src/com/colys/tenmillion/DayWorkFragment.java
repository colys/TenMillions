package com.colys.tenmillion;

import CustomViews.*;
import DataAccess.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.os.*;
import android.text.Html;
import android.text.Spanned;
import android.util.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import com.colys.tenmillion.Entity.*;
import java.text.*;
import java.util.*;

@SuppressLint("ValidFragment")
public class DayWorkFragment extends TabViewFragment
{ 	
	public DayWorkFragment(){}

	Date queryDate,lastQueyDate;
	String queryDateStr;  
	PopMemberDayWorkActivity memberWorkDialog;
	LinkedList< AskForLeave> askList,willBackList,overDateList;
	LinkedList<DayWorkHouse> houseWorkList;
	View selectListViewItem;
	int editMemberIndex ;
	LinkedList<House> houseList;
	LinkedList<Task> taskList;
	DayWorkHouse selectedDayWorkHouse;
	DayWorkDetail selectDayWork;
	House newHouse;
	ListView listView ;
	TextView txtQueryDate;
	private Button btnReCalc;
	private Button btnPrev;
	private View btnNext;
	AlertDialog.Builder dlg;
	int inAction = -1;
	private int selectedIndex;

	@Override
	public int getLayout()
	{
		return R.layout.activity_day_works;
	}
	 
 
	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreateView(View rootView)
	{
		btnReCalc = (Button) rootView.findViewById(R.id.day_work_search_button);	
		txtQueryDate = (TextView) rootView.findViewById(R.id.day_work_query_date);				
		listView = (ListView) rootView.findViewById(R.id.house_day_works_listview);
		m_Access =new BasicAccess(rootView.getContext());	 
		btnPrev = (Button) rootView.findViewById(R.id.day_work_prev_day);
		btnNext = (Button) rootView.findViewById(R.id.day_work_next_day);
		btnPrev.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0)
				{						
					RunQuery(-1);
				}

			});

		btnNext.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0)
				{						
					RunQuery(1);
					
				}

			});
		
		
		btnReCalc.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v)
				{	
					if(!getApp().getCurrentUser().InitDayWorks){
						ws.Toast("不好意思，您没有权限!");
						return;
					}
					if(dlg!=null ) return;					 
					
					dlg =new AlertDialog.Builder(getActivity()).setTitle("确认")  
						.setMessage("确认要进行重新计算吗？")  
						.setPositiveButton("是", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which)
							{								
								m_Access.Close(true);
								m_Access.OpenTransConnect();						 
								try {
									m_Access.Visit(DayWorkDetailAccess.class).ReCalcOnline(queryDateStr,getCurrentGroupId());
									RunQueryServices();
								} catch (Exception e) {
									ws.Toast(e.getMessage());
									m_Access.Close(false);
									return;
								}
								m_Access.Close(true);
								dlg = null;
							}				
						}) 
						.setNegativeButton("否",  new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dlg = null;
							}
						})  ;
						dlg.show(); 
				}
			}); 
		
		
		
		
		listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v,
												ContextMenuInfo menuInfo)
				{
					
					inAction = 0;
					AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
					selectListViewItem = minfo.targetView;
					if (minfo.position >= houseWorkList.size()) return;
					selectedIndex = minfo.position;
					selectedDayWorkHouse = houseWorkList.get(minfo.position);		                	
					menu.clear();
					menu.setHeaderTitle("您想做什么？");
					int pos =0;
					menu.add(13, 0, pos, "快速记录");					
					if (selectedDayWorkHouse.PeopleComingList != null)
					{
						for (int i=0;i < selectedDayWorkHouse.PeopleComingList.size();i++)
						{
							PeopleComing pc = selectedDayWorkHouse.PeopleComingList.get(i);
							menu.add(10, i, pos++, pc.MemberName + pc.Relation); 
						}					
					}

					if (selectedDayWorkHouse.Works != null)
					{
						for (int i=0;i < selectedDayWorkHouse.Works.size();i++)
						{							
							menu.add(11, i, pos, selectedDayWorkHouse.Works.get(i).MemberName + "工作");
							pos ++;
						}

						for (int i=0;i < selectedDayWorkHouse.Works.size();i++)
						{							
							menu.add(12, i, pos + i, selectedDayWorkHouse.Works.get(i).MemberName + "搬家到");

						}
					}
				}
			});
			
	}
	private void GotoEditDayWorks(int arg2){
		Intent intent = new Intent();
		intent.setClass(getActivity(), EditDayWorkActivity.class);
		Bundle b = new Bundle();
		b.putString("workDay", queryDateStr);
		String h= null;
		if(arg2 > -1) h = houseWorkList.get(arg2).HouseID;
		b.putString("houseID", h);
		intent.putExtras(b);
		startActivityForResult(intent,QuickEditWork);
	}	


	@Override
	public void FirstShow()
	{
		super.FirstShow();
		//Log.i("in action",String.valueOf(inAction));
		if(inAction > -1) 	return;
		try {
			queryDateStr =m_Access.Visit(DayWorkDetailAccess.class).GetLastDate(getCurrentGroupId());
		} catch (Exception e1) {
			ws.Toast(e1.getMessage());
		} 
		if(queryDateStr==null || queryDateStr.isEmpty())		
		{
			queryDateStr = Utility.GetNowString();
		}
		queryDate = Utility.ConvertToCalendar(queryDateStr).getTime();
		InitPopDialog();
		RunQuery(0);
		

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == android.app.Activity.RESULT_OK)
		{
			if(requestCode == OpenEditPeopleWorking){
				if(selectedDayWorkHouse == null) return;
				selectedDayWorkHouse.PeopleComingList = this.m_Access.Visit(PeopleComingAccess.class).GetWorking(selectedDayWorkHouse.HouseID, queryDateStr,getCurrentGroupId());
				TextView editText = (TextView) selectListViewItem.findViewById(R.id.item_house_working_people_coming_1);
				if (editText != null)
				{
					editText.setText(Html.fromHtml( listItemAdapter.FormatPeopleComing(selectedDayWorkHouse)));
				}else{
					ws.Toast("没有找到coming的标签，数据已保存，重查询一下看看！");
				}
				m_Access.Close(true);
			}else {
				RunQuery(0);	
			}
			inAction = -1;
		}
	}


	@SuppressLint("SimpleDateFormat")
	private void RunQuery(int absDay)
	{
		if(!btnPrev.isEnabled()) return ;
		btnPrev.setEnabled(false);
		btnNext.setEnabled(false);
		btnReCalc.setEnabled(false);
		lastQueyDate = (Date) queryDate.clone();
		long absTime = 60 * 60 * 24 * absDay;		
		if (absDay != 0)
		{
			long myTime =(queryDate.getTime() / 1000) + absTime;
			queryDate.setTime(myTime * 1000);
		}		 
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");		
		queryDateStr = sdf.format(queryDate);		 
		try
		{	
		houseList=m_Access.Visit(HouseAccess.class).Query(false,getApp().getCurrentGroupID());
			QueryAskForLeave();
			QueryTask();
			RunQueryServices();	
			
			m_Access.Close(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			MainActivity.ShowError(this.getActivity(),e);
		}
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		btnReCalc.setEnabled(true);
	}

	private void QueryAskForLeave()
	{
		askList  = null;
		if (Utility.UseLocal)
		{
			 askList = m_Access.Visit(AskForLeaveAccess.class).Query(false, queryDateStr,  getApp().getCurrentGroupID());
			 willBackList = m_Access.Visit(AskForLeaveAccess.class).QueryNext2DayBack(queryDateStr,getApp().getCurrentGroupID());
			 overDateList = m_Access.Visit(AskForLeaveAccess.class).QueryOverDayUnBack(queryDateStr,getApp().getCurrentGroupID());
			 
		}
		else
		{			 
			ws.visitServices("AskForLeave_QueryByDayJson", new String[]{"day","groupID"},new String[]{ queryDateStr,String.valueOf(getApp().getCurrentGroupID())}, QueryAskForLeave);
		}

	}	

	private void QueryTask()
	{
		taskList  = null;
		if (Utility.UseLocal)
		{
			 taskList =  m_Access.Visit(TaskAccess.class).GetByDate(queryDateStr, true, getApp().getCurrentGroupID());
			//FillTable();
		}
		else
		{			 
			ws.visitServices("Task_GetDayTasksJson",new String[]{ "day","groupID"},new String[]{ queryDateStr , String.valueOf(getApp().getCurrentGroupID()) }, QueryTask);
		}

	}	
		 
	@SuppressLint("SimpleDateFormat")
	private void RunQueryServices() throws Exception
	{		
		houseWorkList = null; 
		if(Utility.UseLocal )
		{
			houseWorkList = m_Access.Visit(DayWorkDetailAccess.class).QueryHouseWorks(queryDateStr, getApp().getCurrentGroupID());
			FillTable();
			

		}
		else
		{
			
			//String method = queryMethod == null ?"DayWork_QueryHouseWorksJson": queryMethod;
			//ws.visitServices(method, new String[]{"day","groupID"},new String[]{ queryDateStr,String.valueOf(getApp().getCurrentGroupID())}, QueryHouseWork);
		}

	}


	final int QueryHouseWork =1,ChangeHouse=2,QueryAskForLeave =3,QueryTask=4,OpenEditPeopleWorking = 5,QuickEditWork=6;

//
//	public void onHandleMessage(Message msg)
//	{
//		super.onHandleMessage(msg);	
//		btnNext.setEnabled(true);
//		getActivity().findViewById(R.id.day_work_prev_day).setEnabled(true);
//		getActivity().findViewById(R.id.day_work_search_button).setEnabled(true); 				
//		switch (msg.what)
//		{
//			case QueryHouseWork:
//				houseWorkList = DayWorkHouse.ListFromJson(ws.queryResult);
//				//QueryAskForLeave();
//				FillTable();
//				break;
//			case QueryAskForLeave:
//				askList = AskForLeave.ListFromJson(ws.queryResult);
//				//QueryTask();
//				FillTable();
//				break;
//			case QueryTask:
//				taskList = Task.ListFromJson(ws.queryResult);
//				FillTable();
//				break;
//			case ChangeHouse: 
//				ReBindOnChangeHouse();
//				break;
//		}
//	}
	
	private void ReBindOnChangeHouse(DayWorkHouse[] arr ){
		//重载listView 
		LinkedList<DayWorkHouse> newList =new LinkedList<DayWorkHouse>();
		for (DayWorkHouse houseBefore : houseWorkList)
		{
			boolean find = false;
			for (DayWorkHouse houseReturn : arr)
			{
				if (houseReturn.HouseID.equals( houseBefore.HouseID))
				{
					find = true;
					newList.add(houseReturn);
					break;
				}
			}
			if (!find) newList.add(houseBefore); 						
		}
		for (DayWorkHouse houseReturn : arr)
		{
			boolean find = false;					
			for (DayWorkHouse houseBefore : houseWorkList)
			{							
				if (houseReturn.HouseID.equals(houseBefore.HouseID))
				{
					find = true;
					break;
				}
			}
			if (!find) newList.add(houseReturn); 	
		}
		houseWorkList.clear();
		houseWorkList = newList;
		FormartListViewAdapter();
	}

	private void InitPopDialog()
	{
		if(memberWorkDialog==null){
			memberWorkDialog = new PopMemberDayWorkActivity(this.getActivity(),new IPopMemberWorkCallback(){

				@Override
				public void Callback(View v, DayWorkDetail DayWorkDetail) {
					try { 
						m_Access.Visit(DayWorkDetailAccess.class).Update(DayWorkDetail);						
					} catch (Exception e) {
						Toast toast = Toast.makeText(getActivity() , e.getMessage() , Toast.LENGTH_SHORT); 
						toast.show();
						m_Access.Close(false);
					}
					m_Access.Close(true);
					TextView editText = (TextView) selectListViewItem.findViewById(R.id.item_house_working_house_member1);
					if (editText != null)
					{
						editText.setText(Html.fromHtml(DayWorkListAdapter.FormatHouseMemberWork(selectedDayWorkHouse)));			
					}else{
						ws.Toast("没有找到member的标签，数据已保存，重查询一下看看！");
					}
					inAction = -1;					
					
				}
				
			});
		}
		
	}

	private void FillTable()
	{  			
		if (houseWorkList == null || askList  == null || taskList == null) return;
		//houseList = new LinkedList<House>();
		boolean needInit = true;		
		/*for (DayWorkHouse hdw:houseWorkList)
		{
			if (!hdw.House.ID.equals(House.Empty_House_Guid ))
				houseList.add(hdw.House);
		}*/
		for(int i = houseWorkList.size() -1; i> -1;i-- ){
			DayWorkHouse hdw = houseWorkList.get(i);
			//if (!hdw.HouseID.equals(House.Empty_House_Guid )) houseList.add(hdw.House);
			if (hdw.Works != null && hdw.Works.size() > 0) needInit = false;
			else houseWorkList.remove(i);
		}		
		if ( needInit && getApp().getCurrentUser().InitDayWorks)
		{
				new AlertDialog.Builder(getActivity()).setTitle("确认")  
					.setMessage("没有" + this.queryDateStr + "数据，是否需要初始化？")  
					.setPositiveButton("是", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							m_Access.Close(true);
							m_Access.OpenTransConnect();
							try {
								m_Access.Visit(DayWorkDetailAccess.class).InitDayHouseWorks(queryDateStr, getApp().getCurrentGroupID());
								RunQueryServices();	//初始化数据								
								
							} catch (Exception e) {
								ws.Toast(e.getMessage());
								m_Access.Close(false);
							}
							m_Access.Close(true);							
							GotoEditDayWorks(-1);
						}				
					}) 
					.setNegativeButton("否",  new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							queryDate=(Date) lastQueyDate.clone();
							SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
							queryDateStr = sdf.format(queryDate);		 
							txtQueryDate.setText("当前：" + queryDateStr);	
						}
					}).show();
				
				return;
		}			
		FormartListViewAdapter();
		txtQueryDate.setText("当前：" + queryDateStr);	
	}



	@SuppressLint("SimpleDateFormat")
	private void FormartListViewAdapter()
	{
		if (houseWorkList == null || askList  == null || taskList == null) return;
		this.selectDayWork = null;
		this.selectedDayWorkHouse = null;
		this.selectListViewItem = null;
		//java.util.List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		//clear empty
		for (int i=houseWorkList.size() - 1 ;i > -1;i--)
		{
			DayWorkHouse hdw = houseWorkList.get(i);
			if ((hdw.PeopleComingList == null ||  hdw.PeopleComingList.size() == 0) && (hdw.Works == null || hdw.Works.size() == 0)) 
				houseWorkList.remove(i);
		}

//		for (DayWorkHouse hdw:houseWorkList)
//        {  			 
//            HashMap<String, Object> map = new HashMap<String, Object>();  
//            map.put("houseName", hdw.House.Name);             
//            map.put("People", listItemAdapter.FormatPeopleComing(hdw));
//            //member work
//            map.put("Member", listItemAdapter.FormatMemberWork(hdw));                             
//            listItem.add(map);  
//        }  
//
//		if (askList != null)
//		{
//			HashMap<String, Object> map = new HashMap<String, Object>();  	 
//			String qingJia="",guiDui="";
//			for (AskForLeave afl : askList)
//			{
//				if (afl.ApplyDate.equals(queryDateStr))
//				{
//					qingJia += afl.MemberName + " ";
//				}
//				if (afl.IsBack && afl.BackDate.equals(queryDateStr))
//				{
//					guiDui += afl.MemberName + " ";
//				}
//			}
//			String resultStr ="";
//			if(!qingJia.isEmpty())  resultStr +="请假："+qingJia;
//			
//			if(!guiDui.isEmpty()){
//				if(!resultStr.isEmpty())resultStr +="<br>"; 
//				resultStr +="归队：" +guiDui;
//			}
//			 
//			if(willBackList.size()>0){
//				String willBack ="";
//				for (AskForLeave ask : willBackList)
//				{
//					willBack += ask.MemberName + " ";
//				}
//				 
//				
//				if(!resultStr.isEmpty())resultStr +="<br>";  
//				resultStr +="即将到期 ："+willBack;
//			} 
//			if(overDateList.size()>0){
//				String overDay="";
//				for (AskForLeave ask : overDateList)
//				{
//					overDay += ask.MemberName + " ";
//				}				 
//				
//				if(!resultStr.isEmpty())resultStr +="<br>";  
//				resultStr +="到期未续 ："+overDay;
//			}
//		 
//			
//			if (  !resultStr.isEmpty())
//			{
//				map.put("houseName", "请假与归队");			 
//				map.put("People", "");			 
//				map.put("Member", resultStr);
//				listItem.add(map);  
//			}
//		}
//		if (taskList.size() > 0)
//		{
//			HashMap<String, Object> map = new HashMap<String, Object>();  
//			map.put("houseName", "备注");
//			String str="";
//			for (Task task : taskList)
//			{
//				str += "·" + task.Text +"<br>";
//			}
//			str+="";
//			map.put("Member", str);
//			map.put("People", "");
//			listItem.add(map);
//		}

		//生成适配器的Item和动态数组对应的元素  				
		listItemAdapter =new DayWorkListAdapter(getActivity(),houseWorkList,taskList,askList,willBackList,overDateList);
        listView.setAdapter(listItemAdapter);
	}
	DayWorkListAdapter listItemAdapter;
//	private String FormatPeopleComing(DayWorkHouse DayWorkHouse)
//	{
//		int peopleSize = DayWorkHouse.PeopleComingList == null ? 0: DayWorkHouse.PeopleComingList.size();
//        String peopleStr = "";
//        for (int i=0;i < peopleSize;)
//		{
//        	PeopleComing pc = DayWorkHouse.PeopleComingList.get(i);
//        	peopleStr += "["+pc.MemberName +pc.ComeFrom + pc.Relation +"] "+ pc.DayResult;
//        	i++;
//            if (i < peopleSize) peopleStr += "\r\n";
//        }
//        return peopleStr;
//	}

//	private String FormatMemberWork(DayWorkHouse DayWorkHouse)
//	{
//
//		String workStr ="";
//		for (DayWorkDetail daywork : DayWorkHouse.Works)
//		{
//			String strItem = daywork.MemberName ;
//			if (daywork.ZhengBang > 0) strItem += daywork.ZhengBang + "Z";
//	        if (daywork.GenJin > 0) strItem += daywork.GenJin + "G";
//	        if (daywork.BaiFang > 0) strItem += daywork.BaiFang + "B";
//	        if (daywork.PeiXun > 0) strItem += daywork.PeiXun + "PX";
//	        if (daywork.PuDian > 0) strItem += daywork.PuDian + "P";
//	        if (daywork.DaiGZ > 0) strItem += daywork.DaiGZ + "D";
//	        if (daywork.Remark != null && daywork.Remark != "") strItem += daywork.Remark;
//	        if(daywork.IsHouseChanged) strItem="<font color='#5c6063'>"+ strItem +"</font>";
//	        workStr += strItem +"&nbsp;&nbsp;";
//		}
//		
//		return workStr ;
//	}

	AlertDialog.Builder chooseHouseDialog = null;


	//长按菜单响应函数  
	@Override  
	public boolean onContextItemSelected(MenuItem item)
	{  
		if (selectedDayWorkHouse == null)  return super.onContextItemSelected(item); 
		inAction = item.getOrder()+1;
		int gid = item.getGroupId();
		Log.i("tip", "groupid is " + gid);
		switch (gid)
		{
			case 10:
				int itemid=item.getItemId();
				Log.i("tip", "item id is " + itemid);
				PeopleComing pc = selectedDayWorkHouse.PeopleComingList.get(itemid);    	
				if (pc == null) Log.e("error", "people comeing is null");
				Intent tent= new Intent();
				Bundle bundle=new Bundle();
				bundle.putString("comingID", pc.ID);
				tent.putExtras(bundle);
				tent.setClass(getActivity(), PeopleWorkingActivity.class);
				startActivityForResult(tent,OpenEditPeopleWorking);
				break;
			case 11:
				//if (Utility.UseLocal) return false;
				editMemberIndex = item.getItemId();
				selectDayWork = (DayWorkDetail)selectedDayWorkHouse.Works.get(editMemberIndex);				
				memberWorkDialog.Show(selectListViewItem);
				memberWorkDialog.setValue(selectDayWork);
				break;
			case 12://搬家
				//if (Utility.UseLocal) return false;
				editMemberIndex = item.getItemId();
				selectDayWork = (DayWorkDetail)selectedDayWorkHouse.Works.get(editMemberIndex);
				String[] houseArr=new String[houseList.size()];
				int selectedHouseIndex =-1;
				for (int i =0;i < houseArr.length;i++)
				{
					houseArr[i] = houseList.get(i).Name;
					if(houseList.get(i).ID.equals(selectedDayWorkHouse.HouseID)){
						selectedHouseIndex = i;
					}
				}
				if (chooseHouseDialog == null)
				{
					chooseHouseDialog = new AlertDialog.Builder(getActivity())
						.setTitle(R.string.day_work_pop_select_house_title)
						.setSingleChoiceItems(houseArr, selectedHouseIndex, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which)
							{  
								newHouse = houseList.get(which);
//								ws.visitServices("DayWork_ChangeHouseJson", new String[]{"memberId","oldHourse","newHourse","day"}, new String[]{
//													 String.valueOf(selectDayWork.MemberID),
//													 String.valueOf(selectDayWork.HouseID),
//													 String.valueOf(newHouse.ID),
//													 queryDateStr
//												 }, ChangeHouse);
								DayWorkHouse[] arr;
								try {
									arr = m_Access.Visit(DayWorkDetailAccess.class).ChangeHouse(selectDayWork.MemberID, selectedDayWorkHouse.HouseID, newHouse.ID, queryDateStr,getCurrentGroupId());
								} catch (Exception e) {
									ws.Toast(e.getMessage()); 
									m_Access.Close(false);
									return;
								}
								m_Access.Close(true);
								dialog.dismiss();
								chooseHouseDialog = null;
								ReBindOnChangeHouse(arr);
								inAction = -1;
							}
						})            
						.setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which)
							{  
								chooseHouseDialog = null;
								inAction = -1;
							}
						});	    		
				}
				chooseHouseDialog.show();
				break;
			case 13:
				GotoEditDayWorks(selectedIndex);
				break;
		}


		return super.onContextItemSelected(item);  
	}  
//
//	public void onHandleMessage(Message msg) {
//		super.onHandleMessage(msg);
//		switch(msg.what){	
//			case Utility.PopWin_Daywork_Edit:	
//				if(msg.obj==null){
//					inAction = -1;
//					break;
//				}
//				try {
//					DayWorkDetail DayWorkDetail =(DayWorkDetail) msg.obj;
//					m_Access.Visit(DayWorkDetailAccess.class).Update(DayWorkDetail);						
//				} catch (Exception e) {
//					Toast toast = Toast.makeText(getActivity() , e.getMessage() , Toast.LENGTH_SHORT); 
//					toast.show();
//					m_Access.Close(false);
//				}
//				m_Access.Close(true);
//				TextView editText = (TextView) selectListViewItem.findViewById(R.id.item_house_working_house_member1);
//				if (editText != null)
//				{
//					editText.setText(Html.fromHtml(listItemAdapter.FormatHouseMemberWork(selectedDayWorkHouse)));			
//				}else{
//					ws.Toast("没有找到member的标签，数据已保存，重查询一下看看！");
//				}
//				inAction = -1;
//				break;
//		}
//		
//		
//	}
	@Override
	public void onContextMenuClosed(Menu menu){
		if(inAction ==0) inAction = -1;
	 }
	
}


