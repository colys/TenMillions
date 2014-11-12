package com.colys.tenmillion;

import java.io.File;

import com.colys.tenmillion.Entity.ConfigItem;
import com.colys.tenmillion.Entity.User;

import DataAccess.BasicAccess;
import DataAccess.DefaultAccess;
import android.R.integer;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class MyApplication extends Application {
	
	int mCurrentGroupID;
	
	public void setCurrentGroupID(int groupid){
		mCurrentGroupID = groupid;
	}
	
	public int getCurrentGroupID(){
		return mCurrentGroupID;
	}
	
	int mLoginSyncToken;
	
	public void setLoginSyncToken(int token){
		mLoginSyncToken = token;
	}
	public int getLoginSyncToken(){
		return mLoginSyncToken ;
	}
	
	User mCurrentUser;
	public void setCurrentUser(User u){mCurrentUser = u;}
	
	public User getCurrentUser(){
		if(mCurrentUser == null) Log.e("User", "Current User is null");
		return mCurrentUser;
	}
	
	public String database_directory;
	public String database_path;
	
	public String GetDataBaseDirectory(){ return  database_directory;}
	
	public String GetDataBasePath(){ 
		if(database_path == null) 
			Log.e("database", "path is null");
		return  database_path;}
	
	public void SetDBPath(String directory,String name){
		database_directory = directory;
		database_path =database_directory + "/"+ name; 
	}
	
	@Override  
    public void onCreate() { 
		super.onCreate();  
		String dbDirectory = String.format("/data/data/%s/databases",this.getApplicationInfo().packageName);
		File dirfile = new File(dbDirectory);
		if(!dirfile.exists()){
			dirfile.mkdir();
		}
		//String dbPath = dbDirectory +"/TenMillion.db";		
		MyApplication mApp = (MyApplication) getApplicationContext();
		mApp.SetDBPath(dbDirectory,"TenMillion.db");
        /*get login info
		String sql="select value from configs where key='"+ ConfigItem.Last_Account_Json +"'";
		BasicAccess access = new BasicAccess(getApplicationContext());
		try {
			String json = access.Visit(DefaultAccess.class).ExecuteScalar(sql);
			mCurrentUser = User.FromJson(json);
			mLoginSyncToken = mCurrentUser.SyncToken;
			sql="select value from configs where key='"+ ConfigItem.Last_Account_group +"'";
			String strGroupid = access.Visit(DefaultAccess.class).ExecuteScalar(sql);
			mCurrentGroupID = Integer.parseInt(strGroupid);
		} catch (Exception e) {
			Toast toast = Toast.makeText(this.getApplicationContext(), "初始化Application失败，"+ e.getMessage(), Toast.LENGTH_LONG); 
			toast.show();
		}*/
    }  
	
	@Override
	public void onTerminate(){
		
	}
}
