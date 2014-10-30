package com.colys.tenmillion.Entity;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class ConfigItem extends DBEntity {
	public static final String Synch_Token="sync_token";
	//public static final String Last_Account_Name = "lastAccount";
	public static final String Last_Account_Json = "lastAccount_Json";
	public static final String Last_Account_group = "lastAccount_GroupID";
	//public static final String Last_Account_user = "lastAccount_UserID";
	//public static final String Last_Account_Password="lastAccount_Password";
	
	public String Key;
	public String Value;
	
	@SuppressLint("DefaultLocale")
	@Override
	public void Parse(Cursor c){
		int colCount = c.getColumnCount();
		 for(int i=0;i< colCount;i++){
		 if(c.isNull(i)) continue;
		 String colName = c.getColumnName(i).toLowerCase();
	     if(colName.equals("key"))  Key = c.getString(i);  
	     if(colName.equals("value"))  Value = c.getString(i);   
	   }
	}
}
