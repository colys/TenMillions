package com.colys.tenmillion.Entity;
import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AskForLeave extends DBEntity
{
	
	
public AskForLeave () { }


 
public AskForLeave (Cursor c) {
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
         if(colName.equals("memberid"))  MemberID = c.getString(i);  
         if(colName.equals("applydate"))  ApplyDate = c.getString(i);  
         if(colName.equals("backdate"))  BackDate = c.getString(i);  
         if(colName.equals("remark"))  Remark = c.getString(i);  
         if(colName.equals("isback"))  IsBack = c.getInt(i)==1?true:false; 	
         if(colName.equals("membername"))  MemberName = c.getString(i); 
      }
}

public static AskForLeave FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, AskForLeave.class);
}
public static LinkedList<AskForLeave> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<AskForLeave>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}

public String ToJson(){
	Gson gson=new Gson();
	return gson.toJson(this, AskForLeave.class);
}

public String ID;
public String MemberID;
public String ApplyDate;
public String BackDate;
public String Remark;
public Boolean IsBack = false;
public String MemberName;
}
