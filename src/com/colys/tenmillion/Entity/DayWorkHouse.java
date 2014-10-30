package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DayWorkHouse  extends DBEntity {
	 public House House ;
	 public String ID;
	 public String HouseID;
	 public int GroupID;
	 public String Remark;
	 public String WorkDay;
     public LinkedList<DayWorkDetail> Works;
     public LinkedList<PeopleComing> PeopleComingList ;
     
     @SuppressLint("DefaultLocale")
	public void Parse(Cursor c){ 
    	 for(int i=0;i< c.getColumnCount();i++){
    		 if(c.isNull(i)) continue;
    		 String colName = c.getColumnName(i).toLowerCase();
     
                    if(colName.equals("id"))  ID = c.getString(i);  
                    if(colName.equals("houseid"))  HouseID = c.getString(i);  
                    if(colName.equals("groupid"))  GroupID = c.getInt(i);  
                    if(colName.equals("remark"))  Remark = c.getString(i);  
                    if(colName.equals("workday"))  WorkDay = c.getString(i); 	
                    if(colName.equals("housename")){ 
                    	House=new House();
                    	House.ID = HouseID;
                    	House.Name = c.getString(i); 	
                    }
                    
    	 }
      }     
     
     
     public DayWorkHouse() { 
	}


	public static DayWorkHouse FromJson(String json) {
         Gson gson=new Gson();
         return gson.fromJson(json, DayWorkHouse.class);
	 }
	
	 public static LinkedList<DayWorkHouse> ListFromJson(String json) {
	        Type listType = new TypeToken<LinkedList<DayWorkHouse>>(){}.getType();
	        Gson gson=new Gson();
	        return gson.fromJson(json, listType);
	 }
}
