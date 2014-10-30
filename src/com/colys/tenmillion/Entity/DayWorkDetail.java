package com.colys.tenmillion.Entity;
import java.lang.reflect.Type;
import java.util.LinkedList;


import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DayWorkDetail extends DBEntity
{

public DayWorkDetail () { }

public DayWorkDetail (Cursor c) {
	Parse(c);
}


@SuppressLint("DefaultLocale")
@Override
public void Parse(Cursor c){
	int colCount = c.getColumnCount();
	String yestdayHouse = null;
	String HouseID = null;
	for(int i=0;i< colCount;i++){
	   if(c.isNull(i)) continue;
	   String colName = c.getColumnName(i).toLowerCase();
	   //if(colName.equals("workday"))  WorkDay = c.getString(i);  
	   if(colName.equals("houseid"))  HouseID = c.getString(i);  
	   if(colName.equals("wdhid"))  WDHID = c.getString(i);  
	   if(colName.equals("memberid"))  MemberID = c.getString(i);  
	   if(colName.equals("zhengbang"))  ZhengBang = c.getInt(i);  
	   if(colName.equals("baifang"))  BaiFang = c.getInt(i);  
	   if(colName.equals("genjin"))  GenJin = c.getInt(i);  
	   if(colName.equals("pudian"))  PuDian = c.getInt(i);  
	   if(colName.equals("peixun"))  PeiXun = c.getInt(i);  
	   if(colName.equals("remark"))  Remark = c.getString(i);  
	   if(colName.equals("id"))  ID = c.getString(i);  
	   if(colName.equals("daigz"))  DaiGZ = c.getInt(i); 	
	   if(colName.equals("membername"))  MemberName = c.getString(i); 
	   if(colName.equals("yestdayhouse")) yestdayHouse =  c.getString(i); 
	 }
	if(yestdayHouse!=null) IsHouseChanged= !yestdayHouse.equals(HouseID);
}

public static DayWorkDetail FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, DayWorkDetail.class);
}

public String ToJson(){
	Gson gson=new Gson();
	return gson.toJson(this, DayWorkDetail.class);
}

public static LinkedList<DayWorkDetail> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<DayWorkDetail>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}
public boolean IsHouseChanged = false;
public String WDHID;
//public String HouseID;
public String MemberID;
public int ZhengBang;
public int BaiFang;
public int GenJin;
public int PuDian;
public int PeiXun;
public String Remark;
public String ID;
public int DaiGZ;
public String MemberName; 
}
