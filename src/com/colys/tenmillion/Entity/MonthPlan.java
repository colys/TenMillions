package com.colys.tenmillion.Entity;
import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.LinkedList;
import com.google.gson.reflect.TypeToken;


public class MonthPlan extends DBEntity
{

public MonthPlan () { }


public MonthPlan (Cursor c) {
	Parse(c);
}


@SuppressLint("DefaultLocale")
@Override
public void Parse(Cursor c){
	 int colCount = c.getColumnCount();
	 for(int i=0;i< colCount;i++){
		 if(c.isNull(i)) continue;
		 String colName = c.getColumnName(i).toLowerCase();

          if(colName.equals("year"))  Year = c.getInt(i);  
          if(colName.equals("month"))  Month = c.getInt(i);  
          if(colName.equals("memberid"))  MemberID = c.getString(i);  
          if(colName.equals("pishu"))  PiShu = c.getInt(i);  
          if(colName.equals("fene"))  FenE = c.getInt(i);  
          if(colName.equals("remark"))  Remark = c.getString(i);
          if(colName.equals("name"))  Name = c.getString(i); 
      }
}
public static MonthPlan FromJson(String json) {
        Gson gson=new Gson();
        return gson.fromJson(json, MonthPlan.class);
}

public static LinkedList<MonthPlan> ListFromJson(String json) {
       Type listType = new TypeToken<LinkedList<MonthPlan>>(){}.getType();
       Gson gson=new Gson();
       return gson.fromJson(json, listType);
}

	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, MonthPlan.class);
	}
	
public int Year;
public int Month;
public String MemberID;
public String Name;
public int PiShu;
public int FenE;
public String Remark;
}
