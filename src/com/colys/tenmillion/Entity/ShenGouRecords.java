package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.*; 


public class ShenGouRecords extends DBEntity
{

public ShenGouRecords () { }
@SuppressLint("DefaultLocale")
public ShenGouRecords (Cursor c) {
	Parse(c);
}
	@SuppressLint("DefaultLocale")
	public void Parse(Cursor c){
		for(int i=0;i< c.getColumnCount();i++){
		
			String colName = c.getColumnName(i).toLowerCase();
			Log.i("tip",colName);
			if(c.isNull(i)) continue;
			if(colName.equals("id"))  ID = c.getString(i);  
			if(colName.equals("memberid"))  MemberID = c.getString(i);  
			if(colName.equals("applydate"))  ApplyDate = c.getString(i);  
			if(colName.equals("sgfene"))  SGFenE = c.getInt(i);  
			if(colName.equals("ispatch"))  IsPatch =  c.getInt(i)==1?true:false;           
			if(colName.equals("membername"))  MemberName = c.getString(i);  
		}
		
	}
	

public static ShenGouRecords FromJson(String json) {
               Gson gson=new Gson();
               return gson.fromJson(json, ShenGouRecords.class);
       }

public static LinkedList<ShenGouRecords> ListFromJson(String json) {
              Type listType = new TypeToken<LinkedList<ShenGouRecords>>(){}.getType();
              Gson gson=new Gson();
              return gson.fromJson(json, listType);
       }



public String ToJson(){
	Gson gson=new Gson();
	return gson.toJson(this, ShenGouRecords.class);
}



public String ID;
public String MemberID;
public String ApplyDate;
public int SGFenE;
public Boolean IsPatch=true;
public String MemberName;

}
