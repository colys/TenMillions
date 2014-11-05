package com.colys.tenmillion;

import java.io.File;
import java.io.FileOutputStream; 
import java.io.InputStream;
import java.io.OutputStream; 

import com.colys.tenmillion.Entity.ConfigItem;
import com.colys.tenmillion.Entity.Group;
import com.colys.tenmillion.Entity.User; 

import CustomViews.*;
import DataAccess.BasicAccess; 
import DataAccess.ConnectivityReceiver;
import DataAccess.DBHelper;
import DataAccess.DefaultAccess; 
import android.app.AlertDialog;
import android.content.*;
import android.content.res.AssetManager;
import android.os.*; 
import android.util.Log;
import android.view.*;
import android.widget.*;

public class LoginActivity extends WSActivity
{
	BasicAccess mAccess;
	
	protected int getLayout() {
		return R.layout.activity_login;
	}
	
	EditText txtPwd,txtAccount;
	Button btnLogin;
	private String strAccount,strPassword;
	ConfigItem configItem_account;
	private EditText txtOldPwd;
	private EditText txtNewPwd;
	private EditText txtAccount_2;
	private String dbDirectory;
	int groupID;
	private String dbPath;
	User configUser;
	int configGroupID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mAccess =new BasicAccess(this);
		ConfigItem configItem_group=null;
		btnLogin = (Button) findViewById(R.id.login_enter_button);
		try
		{
			configItem_account = mAccess.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "select * from Configs where key='" + ConfigItem.Last_Account_Json + "'");
		
		configItem_group = mAccess.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "select * from Configs where key='" + ConfigItem.Last_Account_group + "'");
		}
		catch (Exception e)
		{
			ws.Toast("db error"+e.getMessage());
			return;
		}
		if(configItem_group!=null && configItem_group.Value!=null && !configItem_group.Value.isEmpty()) configGroupID = Integer.parseInt(configItem_group.Value);
		mAccess.Close(true);
		txtAccount =(EditText) findViewById(R.id.login_user_name);
		txtPwd = (EditText) findViewById(R.id.login_user_password);
		if(configItem_account != null && configItem_account.Value!=null && !configItem_account.Value.isEmpty()){
			configUser = User.FromJson(configItem_account.Value);
			strAccount =configUser.Account;
			txtAccount.setText(strAccount);
			txtPwd.setFocusable(true);
			txtPwd.setFocusableInTouchMode(true);
			txtPwd.requestFocus();			
			txtPwd.requestFocusFromTouch();
		}
		TextView btnChangePwd = (TextView) findViewById(R.id.login_change_password);
		
		btnChangePwd.setOnClickListener(new View.OnClickListener(){			

			@Override
			public void onClick(View p1)
			{
				txtOldPwd = new EditText(LoginActivity.this);
				txtNewPwd = new EditText(LoginActivity.this);
				txtAccount_2 = new EditText(LoginActivity.this);
				if(strAccount!=null) txtAccount_2.setText(strAccount);
				txtOldPwd.setWidth(500);
				txtNewPwd.setWidth(500);
				txtAccount_2.setWidth(500);
				txtAccount_2.setHint(R.string.login_update_password_account);
				txtOldPwd.setHint(R.string.login_please_input_old_password);
				txtNewPwd.setHint(R.string.login_please_input_new_password); 		
				LinearLayout layout = new LinearLayout(LoginActivity.this);
				layout.addView(txtAccount_2);
				layout.addView(txtOldPwd);
				layout.addView(txtNewPwd);
				layout.setOrientation(LinearLayout.VERTICAL);
				new AlertDialog.Builder(LoginActivity.this)
					.setTitle(R.string.login_update_password_title)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(layout)
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which)
						{		
							ws.visitServices("Login_UpdatePassword",
									new String[]{"account","oldPwd","newPwd"},
									new String[]{
										txtAccount_2.getText().toString(),
										txtOldPwd.getText().toString(),
										txtNewPwd.getText().toString()
									},
									Action_ChangePassword);
						}
					}).show();
			
			}
		});
		
		
		txtPwd.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if( arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					btnLogin.performClick();
				}
				return false;
			}
		});
		
		
		btnLogin.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View p1)
			{
				if(!btnLogin.isEnabled()) return;
				btnLogin.setEnabled(false);				
				strAccount =txtAccount.getText().toString();
				strPassword =txtPwd.getText().toString();
				if(strAccount.isEmpty() || strPassword.isEmpty()) return;
				if(Utility.HasConnection(getApplicationContext())){
					ws.visitServices("LoginJson",
							new String[]{"account","pwd"},
							new String[]{strAccount,strPassword},
							Action_Login);
				}else{
					LoginOffline();
				}
			}
		});
		Log.d("BindEvent","btnLogin.setOnClickListener");
		new Thread(){
			@Override
			public void run()
			{	
				Utility.GetPinYin("吕");
			}
		}.start();
		
	}
	
	
	private void LoginOffline(){
		Log.i("login","offline login...");
				//ConfigItem configItem_user = mAccess.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class,"select * from Configs where key='"+ ConfigItem.Last_Account_user +"'");
		//ConfigItem configItem_pwd = mAccess.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class,"select * from Configs where key='"+ ConfigItem.Last_Account_Password +"'");
		//mAccess.Close(true);
		if(configUser!=null && configUser.Groups.length > 0&& configGroupID>0){
			 if(strAccount.equals(configUser.Account)&&   strPassword.equals(configUser.Password)){
				 user = configUser;
				 Log.i("login","choose a group");
				 GetGroup();
				 //groupID =configGroupID;
				 //GotoMain(false);
			 }else{
				 ws.Toast("账户密码不对或者没有权限！");
				 btnLogin.setEnabled(true);
			 }
		 }else{
			 ws.Toast("这是您第一次登录，请连接网络再试！");
			 btnLogin.setEnabled(true);
		 }
		 
	}
	public void onHandleErrorMessage(Message msg){
		switch(msg.what){	
		case Action_Login:
			btnLogin.setEnabled(true);
			break;
		}
	}

	User user = null;
	private final int Action_Login =1,Action_ChangePassword=2;
	public void onHandleMessage(Message msg){
		switch( msg.what){
			case Action_Login:
				if(ws.queryResult!=null && !ws.queryResult.equals("null")) user = User.FromJson(ws.queryResult);
				if(user!=null && user.Groups!=null && user.Groups.length >0 ){
					GetGroup();
				}else{
					ws.Toast(R.string.login_account_not_exists);
					btnLogin.setEnabled(true);
				}
				break;
			case Action_ChangePassword:
				if(ws.queryResult.equals("true"))
					ws.Toast(R.string.login_update_password_ok);
				else
					ws.Toast(R.string.login_account_not_exists);
				break; 
 
		}
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			setResult(RESULT_CANCELED);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	 
	
	private void GetGroup(){
		if(user.Groups.length > 1){
			String[] arr = new String[user.Groups.length];
			int selectedIndex =0;
			for(int i =0;i< arr.length;i++) {
				arr[i] = user.Groups[i].Name;
				if(user.Groups[i].ID == configGroupID) selectedIndex = i;
			}
			 new AlertDialog.Builder(this)
				.setTitle(R.string.please_choose_a_group)
				.setSingleChoiceItems(arr, selectedIndex, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which)
					{  
						Group group = user.Groups[which];							 
						dialog.dismiss();
						groupID = group.ID;
						try
						{
							GotoMain(true);
						}
						catch (Exception e)
						{
							ws.Toast(e.getMessage());
						}
					}
				})            
				.show();
		}else{ 
			groupID = user.Groups[0].ID;
			try
			{
				GotoMain(true);
			}
			catch (Exception e)
			{
				ws.Toast(e.getMessage());
			}
		}
	}
	
	private void GotoMain(boolean updateConfig) throws Exception{
		//log last login to db
		 MyApplication mApp = (MyApplication) getApplication();
		Utility.UseLocal = true;
		mApp.setCurrentGroupID(groupID);
//		Intent intent = new Intent();
//		intent.setClass(getApplicationContext(), MainActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		user.Password = this.strPassword;
		mApp.setCurrentUser (user);		
		mApp.setLoginSyncToken (user.SyncToken);
		ConfigItem configItem_token = mAccess.Visit(DefaultAccess.class) .QueryEntity(ConfigItem.class,"select * from Configs where key='"+ ConfigItem.Synch_Token +"'");
		 if(configItem_token!=null && configItem_token.Value!=null)
			 mApp.getCurrentUser().SyncToken =Integer.valueOf( configItem_token.Value);
		 else 
			 mApp.getCurrentUser().SyncToken = 0;
		 if(updateConfig){
				try {
					Utility.LogConfigs(mAccess,mApp);
				} catch (Exception e) {
					ws.Toast("Log Config Error: " + e.getMessage());
					return;
				}
			}
		//startActivity(intent);
		 setResult(RESULT_OK);
		finish();
	}
	
	
	 
	
}
