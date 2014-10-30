package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PeopleWorking extends DBEntity
{
		
	public PeopleWorking () { }
	
	@SuppressLint("DefaultLocale")
	public PeopleWorking (Cursor c) {
		Parse(c);
	  }
	

	@SuppressLint("DefaultLocale")
	@Override
	public void Parse(Cursor c){
		int colCount = c.getColumnCount();
		 for(int i=0;i< colCount;i++){
			if(c.isNull(i)) continue;
			String colName = c.getColumnName(i).toLowerCase(); 
	        if(colName.equals("id"))  ID = c.getString(i);  
	        if(colName.equals("daycount"))  DayCount = c.getInt(i);  
	        if(colName.equals("result"))  Result = c.getString(i);  
	        if(colName.equals("comingid"))  ComingID = c.getString(i);  
	        if(colName.equals("houseid"))  HouseID = c.getString(i); 	 
	     }
	}
	
	public static PeopleWorking FromJson(String json) {
	        Gson gson=new Gson();
	        return gson.fromJson(json, PeopleWorking.class);
	}
	public static LinkedList<PeopleWorking> ListFromJson(String json) {
	       Type listType = new TypeToken<LinkedList<PeopleWorking>>(){}.getType();
	       Gson gson=new Gson();
	       return gson.fromJson(json, listType);
	}
	
	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, PeopleWorking.class);
	}
	
	
	public String ID;
	public int DayCount;
	public String Result;
	public String ComingID;
	public String HouseID;

}
