package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.database.Cursor;

public class TrainItem extends DBEntity {

public TrainItem () { }

public void Parse (Cursor c) {
for(int i=0;i< c.getColumnCount();i++){
	 if(c.isNull(i)) continue;
	 String colName = c.getColumnName(i).toLowerCase();

         if(colName.equals("id"))  ID = c.getInt(i);  
         if(colName.equals("name"))  Name = c.getString(i); 	 }
}

public static TrainItem FromJson(String json) {
             Gson gson=new Gson();
             return gson.fromJson(json, TrainItem.class);
     }

public static LinkedList<TrainItem> ListFromJson(String json) {
            Type listType = new TypeToken<LinkedList<TrainItem>>(){}.getType();
            Gson gson=new Gson();
            return gson.fromJson(json, listType);
     }
public int ID;
public String Name;
public LinkedList<TrainRecord> Records;

}