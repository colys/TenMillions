package com.colys.tenmillion.Entity;
import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class House extends DBEntity
{
	
	public static final String Empty_House_Guid = "83833471-D359-49F8-9555-46FE69DCBDA4";
	
	/// <summary>
    /// 经理室房子guid
    /// </summary>
    public static final String Manager_House_Guid = "E5136AB7-7DE5-4CBD-BD45-F1290AE02306";

public House () { }

public static House FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, House.class);
}


@SuppressLint("DefaultLocale")
public House (Cursor c) {
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
     if(colName.equals("name"))  Name = c.getString(i);  
     if(colName.equals("address"))  Address = c.getString(i);  
     if(colName.equals("owner"))  Owner = c.getString(i);  
     if(colName.equals("price"))  Price = c.getInt(i);  
     if(colName.equals("owndate"))  OwnDate = c.getString(i); 	 
     if(colName.equals("groupid"))  GroupID = c.getInt(i);  
   }
}

public String ToJson(){
	Gson gson=new Gson();
	return gson.toJson(this, House.class);
}

public static LinkedList<House> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<House>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}
public String ID;
public String Name;
public String Address;
public String Owner;
public int Price;
public String OwnDate;
public int GroupID;
}
