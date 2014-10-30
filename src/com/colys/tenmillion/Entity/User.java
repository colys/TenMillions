package com.colys.tenmillion.Entity;

import com.google.gson.Gson;

public class User{
	 public int ID;
     public String Account ;
     public String Phone;
     public Group[] Groups; 
     public int SyncToken;
     public String Password;
     public boolean InitTrains=false;
     public boolean InitDayWorks=false; 
     
     public static User FromJson(String json) {
         Gson gson=new Gson();
         return gson.fromJson(json, User.class);
	}
	public String ToJson(){
		Gson gson=new Gson();
		return gson.toJson(this, User.class);
	}
	

}
