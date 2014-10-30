package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TrainPlan extends DBEntity
{

public TrainPlan () { }

public void Parse  (Cursor c) {
	for(int i=0;i< c.getColumnCount();i++){
		if(c.isNull(i)) continue;
		String colName = c.getColumnName(i).toLowerCase();	
	    if(colName.equals("id"))  ID = c.getString(i);  
	    if(colName.equals("name"))  Name = c.getString(i);  
	    if(colName.equals("applydate"))  ApplyDate = c.getString(i);  
	    if(colName.equals("isfinish"))  IsFinish = c.getInt(i)==1?true:false; 	
	    if(colName.equals("groupid"))  GroupID = c.getInt(i); 	 
    
	}
}

public static TrainPlan FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, TrainPlan.class);
}

public static LinkedList<TrainPlan> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<TrainPlan>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}
public String ID;
public String Name;
public String ApplyDate;
public Boolean IsFinish;
LinkedList<TrainRecord> Records;
public int GroupID;


LinkedList<TrainItem> trainItems;

public  TrainItem FindTrainItem(String ItemName){
	for(TrainItem ti : GetTrainItems()){
		if(ti.Name.equals(ItemName)) return ti;
	}
	return null;
}

public  TrainItem FindTrainItem(int ItemID){
	for(TrainItem ti : GetTrainItems()){
		if(ti.ID == ItemID) return ti;
	}
	return null;
}

public void SetRecord(LinkedList<TrainRecord> lst){
	Records =lst;
}

public void AddRecord(TrainRecord tr){
	if(Records == null) Records =new LinkedList<TrainRecord>();
	Records.add(tr);
	TrainItem item =FindTrainItem(tr.ItemID);
	if(item ==null){
		item =new TrainItem();
		item.ID = tr.ItemID;
		item.Name = tr.ItemName;
		item.Records =new LinkedList<TrainRecord>();
		if(trainItems == null)trainItems = new LinkedList<TrainItem>();
		this.trainItems.add(item);
	}
	item.Records.add(tr);
}

//进行存储格式转换
public  LinkedList<TrainItem> GetTrainItems(){
	if(trainItems!=null) return trainItems;
	trainItems = new LinkedList<TrainItem>();
	if(this.Records ==null ) this.Records =new LinkedList<TrainRecord>();
	for(TrainRecord tr : this.Records){
		boolean isAdd = false;
		for(TrainItem ti : trainItems){
			if(ti.Name.equals(tr.ItemName)){	
				isAdd= true;
				break;
			}
		}
		if(!isAdd){
			TrainItem newTi=new TrainItem();
			newTi.ID = tr.ItemID;
			newTi.Name = tr.ItemName;
			newTi.Records =new LinkedList<TrainRecord>();
			trainItems.add(newTi );
		}
		
	}
	
	for(TrainItem ti : trainItems){
		for(TrainRecord tr : this.Records){		
			if(tr.ItemName.equals(ti.Name)){
				ti.Records.add(tr);
			}
		}
	}
	return trainItems;
}
}