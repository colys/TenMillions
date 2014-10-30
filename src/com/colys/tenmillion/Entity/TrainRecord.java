package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TrainRecord extends DBEntity {

	  public TrainRecord () { }
	  public void Parse (Cursor c) {
		 for(int i=0;i< c.getColumnCount();i++){
			 if(c.isNull(i)) continue;
			 String colName = c.getColumnName(i).toLowerCase();
	 
	                if(colName.equals("itemid"))  ItemID = c.getInt(i);  
	                if(colName.equals("memberid"))  MemberID = c.getString(i);  
	                if(colName.equals("status"))  Status = c.getInt(i);  
	                if(colName.equals("planid"))  PlanID = c.getString(i);  
	                if(colName.equals("id"))  ID = c.getString(i);
	                if(colName.equals("itemname"))  ItemName = c.getString(i);
	                if(colName.equals("membername"))  MemberName = c.getString(i);
		 }
	  }

	  public static TrainRecord FromJson(String json) {
	                    Gson gson=new Gson();
	                    return gson.fromJson(json, TrainRecord.class);
	            }

	  public static LinkedList<TrainRecord> ListFromJson(String json) {
	                   Type listType = new TypeToken<LinkedList<TrainRecord>>(){}.getType();
	                   Gson gson=new Gson();
	                   return gson.fromJson(json, listType);
	            }
	public int ItemID;
	public String MemberID;
	public String MemberName;
	public int Status;
	public String PlanID;
	public String ItemName;
	public String ID;

}
