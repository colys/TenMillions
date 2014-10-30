package com.colys.tenmillion.Entity;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class UnSyncRecords extends DBEntity {
	public String SyncTime;
	public String Sql;
	public int ID;
	
	@SuppressLint("DefaultLocale")
	@Override
	public void Parse(Cursor c){
		int colCount = c.getColumnCount();
		 for(int i=0;i< colCount;i++){
		 if(c.isNull(i)) continue;
		 String colName = c.getColumnName(i).toLowerCase();
	     if(colName.equals("synctime"))  SyncTime = c.getString(i);  
	     if(colName.equals("sql"))  Sql = c.getString(i);   
	     if(colName.equals("id"))  ID = c.getInt(i);   
	   }
	}
}
