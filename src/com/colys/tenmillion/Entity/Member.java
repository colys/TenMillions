package com.colys.tenmillion.Entity;
import java.lang.reflect.Type;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Member extends DBEntity
{
public Member () { }
 
public Member (Cursor c) {
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
		  if(colName.equals("referenceid"))  ReferenceID = c.getString(i);  
		  if(colName.equals("status"))  Status = c.getInt(i);  
		  if(colName.equals("xvalue"))  XValue = c.getInt(i);  
		  if(colName.equals("pinyinj"))  PinYinJ = c.getString(i);  
		  if(colName.equals("joindate"))  JoinDate = c.getString(i);  
		  if(colName.equals("jiangban"))  JiangBan = c.getString(i); 	
		  if(colName.equals("groupid"))  GroupID = c.getInt(i);  
		  if(colName.equals("ninegg"))  NineGG = c.getString(i); 	
          if(colName.equals("suzhike"))  SuZhiKe = c.getInt(i);  
          if(colName.equals("wenhuake"))  WenHuaKe = c.getInt(i); 
       }
	 TotalFenE =  XValue;
}

public static Member FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, Member.class);
}
public static LinkedList<Member> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<Member>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}

	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, Member.class);
	}
	
public String ID;
public int GroupID;
public String Name;
public String ReferenceID;
public int Status;
public int XValue;
public String PinYinJ;
public String JoinDate;
public String JiangBan;
public String NineGG;
public int SuZhiKe;
public int WenHuaKe;
public LinkedList<Member> CounterMan ;

public int TotalFenE;
}
