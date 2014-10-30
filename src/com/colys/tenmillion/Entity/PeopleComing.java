package com.colys.tenmillion.Entity;
import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.colys.tenmillion.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PeopleComing extends DBEntity
{
	public PeopleComing () { }


	public PeopleComing (Cursor c) {
		Parse(c);
	}


	@SuppressLint("DefaultLocale")
	public void Parse(Cursor c){
		int colCount = c.getColumnCount();
		for(int i=0;i< colCount;i++){
			if(c.isNull(i)) continue;
			String colName = c.getColumnName(i).toLowerCase();
			if(colName.equals("id"))  ID = c.getString(i);  
			if(colName.equals("memberid"))  MemberID = c.getString(i);  
			if(colName.equals("arrivedate"))  ArriveDate = c.getString(i);  
			if(colName.equals("workdate"))  WorkDate = c.getString(i);  
			if(colName.equals("leavedate"))  LeaveDate = c.getString(i);  
			if(colName.equals("name"))  Name = c.getString(i);  
			if(colName.equals("comefrom"))  ComeFrom = c.getString(i);  
			if(colName.equals("relation"))  Relation = c.getString(i);  
			if(colName.equals("status"))  Status = c.getInt(i);  
			if(colName.equals("sgfene"))  SGFenE = c.getInt(i);  
			if(colName.equals("job"))  Job = c.getString(i);  
			if(colName.equals("houseid"))  HouseID = c.getString(i);  
			if(colName.equals("remark"))  Remark = c.getString(i); 	
			if(colName.equals("membername"))  MemberName = c.getString(i); 	 
			if(colName.equals("daycount"))  DayCount = c.getInt(i);  
			if(colName.equals("dayresult"))  DayResult = c.getString(i); 	 
		}


		if (DayResult != null)
		{
			String newResult = "";       
			if(Status>0){
				int compareDay=-1;
				if(WorkDate != null) compareDay=DayCount - Utility.getDateDays(WorkDate,ArriveDate);
				if(compareDay< 1)
				{
					if ( DayResult==null | DayResult.isEmpty()) newResult = "还没开始走工作";
					else newResult = DayResult;
				}else{
					newResult = "第"+ compareDay +"天，"+DayResult;
				}
			}
			  
			switch (Status)
			{
				case 0:
					newResult = Utility.SubStringDateDay( ArriveDate) + "号到";
					break;
				case 1:
					
					break;
				case -1:
					newResult = "不来了";
					break;
				case 2:
					newResult ="["+Name+ "，申购了" + SGFenE + "份]，"+ newResult;
					break;
				case 3:
					newResult = "[认可没从事，" + Utility.SubStringDateDay( LeaveDate) + "号回]，" +newResult ;
					break;
				case 4:
					String strTitle;
					if (WorkDate == null || WorkDate.compareTo(LeaveDate)>0) strTitle = "[没有看";
					else strTitle = "[不认可" ;
					newResult=strTitle+"，"+ Utility.SubStringDateDay( LeaveDate) + "号回]，" + newResult;
					break;

			}
			DayResult = newResult;
		}
	}

	public static PeopleComing FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, PeopleComing.class);
	}
	public static LinkedList<PeopleComing> ListFromJson(String json) {
		Type listType = new TypeToken<LinkedList<PeopleComing>>(){}.getType();
		Gson gson=new Gson();
		return gson.fromJson(json, listType);
	}


	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, PeopleComing.class);
	}


	public String ID;
	public String MemberID;
	public String ArriveDate;
	public String WorkDate;
	public String LeaveDate;
	public String Name;
	public String ComeFrom;
	public String Relation;
	public int Status;
	public int SGFenE;
	public String Job;
	public String HouseID;
	public String Remark;
	public LinkedList<PeopleWorking> Workings;
	public String MemberName ;
/// <summary>
/// 统计一共看了多少天,虚拟属性
/// </summary>
	public int DayCount ;
/// <summary>
/// 当天情况，虚拟的属性
/// </summary>
	public String DayResult ;
}

