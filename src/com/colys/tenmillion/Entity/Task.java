package com.colys.tenmillion.Entity;


import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Task extends DBEntity
{
  
public Task () { }
  public static Task FromJson(String json) {
                    Gson gson=new Gson();
                    return gson.fromJson(json, Task.class);
   }
  
  
  @SuppressLint("DefaultLocale")
public void Tasks (Cursor c) {
	  Parse(c);
  }


@SuppressLint("DefaultLocale")
@Override
public void Parse(Cursor c){
	 for(int i=0;i< c.getColumnCount();i++){
		 if(c.isNull(i)) continue;
		 String colName = c.getColumnName(i).toLowerCase(); 
        if(colName.equals("id"))  ID = c.getString(i);  
        if(colName.equals("text"))  Text = c.getString(i);  
        if(colName.equals("begindate"))  BeginDate = c.getString(i);  
        if(colName.equals("enddate"))  EndDate = c.getString(i);  
        if(colName.equals("isfinish"))  IsFinish = c.getInt(i)==1? true:false; 	 
        if(colName.equals("groupid"))  GroupID = c.getInt(i);  
                
	 }
}
public String ToJson(){
	Gson gson=new Gson();
	return gson.toJson(this, Task.class);
}
  
  public static LinkedList<Task> ListFromJson(String json) {
	  Type listType = new TypeToken<LinkedList<Task>>(){}.getType();
      Gson gson=new Gson();
      return gson.fromJson(json, listType);
}
  public String ID;
  public int GroupID;
  public String Text;
  public String BeginDate;
  public String EndDate;
  public Boolean IsFinish;
}
