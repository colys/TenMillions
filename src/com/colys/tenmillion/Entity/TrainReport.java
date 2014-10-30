package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TrainReport extends DBEntity {

public TrainReport () { }
public void Parse (Cursor c) {
for(int i=0;i< c.getColumnCount();i++){
if(c.isNull(i)) continue;
String colName = c.getColumnName(i).toLowerCase();

    if(colName.equals("memberid"))  MemberID = c.getString(i);  
    if(colName.equals("item1"))  Item1 =  c.getInt(i)==1; 
    if(colName.equals("item2"))  Item2 = c.getInt(i)==1;  
    if(colName.equals("item3"))  Item3 = c.getInt(i)==1;  
    if(colName.equals("item4"))  Item4 = c.getInt(i)==1;  
    if(colName.equals("item5"))  Item5 = c.getInt(i)==1;  
    if(colName.equals("item6"))  Item6 = c.getInt(i)==1;  
    if(colName.equals("item7"))  Item7 = c.getInt(i)==1;  
    if(colName.equals("item8"))  Item8 = c.getInt(i)==1;  
    if(colName.equals("item9"))  Item9 = c.getInt(i)==1;  
    if(colName.equals("item10"))  Item10 = c.getInt(i)==1;  
    if(colName.equals("item11"))  Item11 = c.getInt(i)==1;  
    if(colName.equals("item12"))  Item12 = c.getInt(i)==1; 	 
    if(colName.equals("suzhike"))
    	SuZhiKe = c.getInt(i);
    if(colName.equals("wenhuake"))
    	WenHuaKe = c.getInt(i); 
    if(colName.equals("membername"))  MemberName = c.getString(i);  
    
}
}

public static TrainReport FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, TrainReport.class);
}

public static LinkedList<TrainReport> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<TrainReport>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}
public String MemberID;
public String MemberName;
public Boolean Item1;
public Boolean Item2;
public Boolean Item3;
public Boolean Item4;
public Boolean Item5;
public Boolean Item6;
public Boolean Item7;
public Boolean Item8;
public Boolean Item9;
public Boolean Item10;
public Boolean Item11;
public Boolean Item12;
public int SuZhiKe,WenHuaKe;

}
