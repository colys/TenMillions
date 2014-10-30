package CustomViews; 
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.LinkedList;

import org.apache.http.util.EncodingUtils;

import com.colys.tenmillion.MainActivity;
import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.PeopleWorkingActivity;
import com.colys.tenmillion.R; 
import com.colys.tenmillion.Utility; 
import com.colys.tenmillion.Entity.ConfigItem;
import com.colys.tenmillion.Entity.UnSyncRecords;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import DataAccess.BasicAccess;
import DataAccess.DBHelper; 
import DataAccess.DefaultAccess;
import android.app.Activity; 
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler; 
import android.os.Message;
import android.support.v4.app.Fragment; 
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;  



public abstract class TabViewFragment extends Fragment {
	
	public boolean isFirstShow = true;
	
	String m ="1";
	
	public WSView ws =new WSView();
	
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){	
		super.onCreateView(inflater, container, savedInstanceState);
		boolean isError = true;	
		View rootView;
		int layout = getLayout();
		if(layout > -1) {
			isFirstShow = true;	
			rootView = inflater.inflate(layout,	container, false);
			if(rootView != null){				
				m_Access =new BasicAccess(rootView.getContext());
				ws.setActivity((Activity) rootView.getContext());
				ws.WsErrorCallback = new android.os.Handler.Callback(){
					@Override
					public boolean handleMessage(Message msg) {
						onHandleErrorMessage(msg);
						return false;
					}
				
				};
				isError = false;
				onCreateView(rootView);
				return rootView;
			}
		} 
		if(isError)
		{ 
			 
			rootView = inflater.inflate(R.layout.empty_list_view,	container, false); 
			TextView tv = (TextView) rootView.findViewById(R.id.empty_list_view_label);
			tv.setText("layout not defind or root is null");
			isFirstShow = false;	
			return rootView;
		}	
		else return null;
	}
	 @Override  
	 public void onActivityCreated(Bundle savedInstanceState) {  
	        super.onActivityCreated(savedInstanceState);  
	        MainActivity.tabViewFragment = this;
	        this.FirstShow();
	 }
	
	public void onCreateView(View rootView) {
		
	}
	 
	MyApplication mMyApplication=null;
	
	public MyApplication getApp(){
		if(mMyApplication == null) mMyApplication = (MyApplication) getActivity().getApplication();
		return mMyApplication;
	}
	
	public int getCurrentGroupId(){
		return getApp().getCurrentGroupID();
	}
		
	
		
	protected abstract int getLayout();
		
	
	Handler handler = ws.CreateHandle(new android.os.Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
			onHandleMessage(msg);
			return false;
		}
	
	});
		
	public Handler GetMessageHandler(){ return handler;}
	
	public void FirstShow(){
		isFirstShow = false;
	}
	
	BasicAccess m_Access ;
	String strUnSyncKeys = null;
	public static boolean m_inSyncProc = false;
	static boolean m_SyncHasData = false;
	
	
	 public void Sync(){
		 if(m_inSyncProc) return;
		 m_inSyncProc =true;
		 m_SyncHasData = false;
		 CheckLocal(true);	
	    }
	 
	 public boolean CheckLocal(boolean server){
		//检查本地
    	 LinkedList<UnSyncRecords> lst = m_Access.Visit(DefaultAccess.class).QueryEntityList(UnSyncRecords.class, "select * from UnSyncRecords limit 20");
    	boolean hasLocal = lst.size() > 0;
    	 if(lst!=null && hasLocal){
    		strUnSyncKeys ="";
    		String strContents="[";
    		for(UnSyncRecords unSyn : lst){
    			strContents+= unSyn.Sql+",";
    			strUnSyncKeys += unSyn.ID +  ",";
    		}
    		strUnSyncKeys=strUnSyncKeys.substring(0,strUnSyncKeys.length() - 1);
    		strContents = strContents.substring(0, strContents.length() -1);
    		strContents+="]";
    		ws.visitServices("SyncTeminalContent",new String[]{"token","time","machine","contents"} ,
    				new String[]{String.valueOf(getApp().getCurrentUser().SyncToken), lst.get(0).SyncTime,"colys-phone",strContents},
    				Utility.Sync_Content_To_Server);
    		LoadingDialog.UpdateMessage("正在与服务器同步。。。");
    	}else{
    		if(strUnSyncKeys !=null){
    			strUnSyncKeys = null;
    			m_SyncHasData = true;
    			ws.Toast("同步本地数据到服务器成功");
    			m_SyncHasData = true;
    		}
    		if(server) CheckServer();
    	}
    	m_Access.Close(true); 
    	return hasLocal;
	 }
	 
	 private void CheckServer(){
		 m_inSyncProc =true;
		 //检查服务器,获取服务器上的最大token
		 ws.visitServices("GetToken", null,null, Utility.Sync_Query_Token,false );
		
	    	
	 }
	    
	    
    private void CheckServerContent(int serverToken){
    	if(serverToken ==0){
    		m_inSyncProc = false; 
    		MainActivity.SyncFinish(m_SyncHasData,false);
    		return; 
    	}
    	getApp().getCurrentUser().SyncToken  = serverToken;
		ConfigItem configItem_Sync=null;
		try
		{
		 configItem_Sync = m_Access.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "select * from Configs where key='" + ConfigItem.Synch_Token + "'");
		}
		catch (Exception e)
		{
			ws.Toast(e.getMessage());
			return;
		}
		m_Access.Close(true);
		long Synch_Token = 0;
		if(configItem_Sync.Value!=null) Synch_Token = Integer.valueOf(configItem_Sync.Value);
		 if(Synch_Token < getApp().getCurrentUser().SyncToken ){
			if(Synch_Token==0 || (serverToken -Synch_Token  ) > 100){
				getApp().getCurrentUser().SyncToken  = getApp().getLoginSyncToken();
				
				ws.visitServices("downloaddb", "m",m, Utility.DOWN_COMPLETE);
				LoadingDialog.UpdateMessage("正在下载这"+m+"个月的数据。。。");
				/* LoadingDialog.Show(getActivity());
				 //直接下载
				 Log.i("tip", "downloading remote db");
					new Thread(){
						@Override
						public void run()
						{	
							Utility.DownFile("http://millions.sinaapp.com/downloadDB.php?m=2", DBHelper.database_directory, "TenMillion.sql",handler  );
						}			 
					}.start();*/
					
					 
			 }else{
				 //sync
				 ws.visitServices("GetSyncContent","startToken",String.valueOf(Synch_Token), Utility.Sync_Server_Content );
				 LoadingDialog.UpdateMessage("正在同步数据。。。");
			 }
		 }else{
			 m_inSyncProc = false;
			 MainActivity.SyncFinish(m_SyncHasData,false);
		 }
	}

  
	public void onHandleErrorMessage(Message msg){
		switch(msg.what){	
			case Utility.Sync_Query_Token:
			case Utility.Sync_Content_To_Server:
				m_inSyncProc = false;
				MainActivity.SyncFinish(m_SyncHasData,true);
				break;
			
			case Utility.Sync_Server_Content:
				ws.Toast("同步数据失败，将不能查看最新的内容！");
				m_inSyncProc = false;
				MainActivity.SyncFinish(m_SyncHasData,true);
				break;
			case Utility.DOWN_COMPLETE:
				//"下载"文件出错
				m_inSyncProc = false;
				break;
			}
		
	}
	private boolean ExecServerContents(String json,boolean updateConfigs){
       m_Access.OpenTransConnect();
       try {		    
		    LinkedList<SynContent> lst = SynContent.ListFromJson(json);	
			m_Access.ExecSynContents(lst);
		    if(updateConfigs) m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='"+ getApp().getCurrentUser().SyncToken +"' where key='"+ ConfigItem.Synch_Token +"'");
		    m_Access.Close(true);
	   	} catch (Exception e) {				 
	   		m_Access.Close(false);
			ws.Toast("Exec Server Contents error : "+e.getMessage());
			return false;
		}
       return true;
	}
	public static boolean isCheckedApkVersion = false;
	public void onHandleMessage(Message msg) {		
		switch(msg.what){
		case Utility.Sync_Query_Token:
			int token ;
			if(ws.queryResult==null || ws.queryResult.isEmpty()) token =0;
			else token = Utility.parseInt(ws.queryResult);
			CheckServerContent(token);
			break;
		case Utility.Sync_Server_Content:
			if(ExecServerContents(ws.queryResult,true)){
				MainActivity.SyncFinish(true,false);
				m_SyncHasData =true;
			}else m_SyncHasData =false;
		    m_inSyncProc = false;
			break;
		case Utility.Sync_Content_To_Server:
			LoadingDialog.Close();
			int splitIndex = ws.queryResult.indexOf("[");			 
			String sycContents = null ;
			try {
				//有需要客户端去同步的内容
				if(splitIndex > 0) {
					getApp().getCurrentUser().SyncToken = Utility.parseInt((ws.queryResult.substring(0,splitIndex)));
					sycContents= ws.queryResult.substring(splitIndex);			
					if(!ExecServerContents(sycContents,false)){
						ContentValues values=new ContentValues();
						values.put("SyncTime", Utility.GetNowString("yyyy-MM-dd HH:mm:ss"));
						values.put("ValueJson", sycContents);
						m_Access.Visit(DefaultAccess.class).ExecuteInsert("ExecServerError", values);
					}
				}else 
					getApp().getCurrentUser().SyncToken = Utility.parseInt(ws.queryResult);
				//update config and delete unsync
				m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from UnSyncRecords where ID in ("+strUnSyncKeys+")");
				m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("update Configs set value='"+ getApp().getCurrentUser().SyncToken +"' where key ='"+ ConfigItem.Synch_Token +"'");
			} catch (Exception e) {
				ws.Toast(e.getMessage());
				ws.Toast("内容已经同步，但是清空unsync表和记录configs表失败!");
				 return;
			}
			m_Access.Close(true);
			CheckLocal(true);	
			break;
			//ws.Toast("同步到服务器成功！");
			
		case Utility.DOWN_START:
			LoadingDialog.UpdateMessage("正在下载近"+m+"个月的数据，请稍候。。。");
			break;
		case Utility.DOWN_POSITION:
			LoadingDialog.UpdateMessage(Utility.GetPercent(msg.arg1,msg.arg2));
			break;		
		case Utility.DOWN_COMPLETE:
				
			m_SyncHasData = true;
			try {
				//execute sql file
				/*String fileName = DBHelper.database_directory+"/TenMillion.sql";
				FileInputStream fin = new FileInputStream(fileName);   
				  
		         int length = fin.available();   
		  
		         byte [] buffer = new byte[length];   
		         fin.read(buffer);		  
		         String res = EncodingUtils.getString(buffer, "UTF-8"); 		  
		         fin.close(); */
		         String[] sqlArr = ws.queryResult.split(";");
		         m_Access.Close(true);
		         m_Access.OpenTransConnect();
		         String[] tableArray = new String[]{"AskForLeave","DayWorkHouse","DayWorkDetail","House","Member","MonthPlan","PeopleComing","PeopleWorking","ShenGouRecords","Tasks","TrainItems","TrainPlan","TrainRecords","TrainReport"};
		         for(String tableName : tableArray){
		        	 m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from "+ tableName);
		         }
		         for(String sql : sqlArr){
		        	 m_Access.Visit(DefaultAccess.class).ExecuteNonQuery(sql);
		         }
				Utility.LogConfigs(m_Access,getApp());
				m_Access.Close(true);
			} catch (Exception e) {
				ws.Toast("update sql error or log Config error"+ e.getMessage());
				LoadingDialog.Close();
				return;
			}
			m_inSyncProc = false;
			MainActivity.SyncFinish(m_SyncHasData,false);
			LoadingDialog.Close();
			break;
		case Utility.Down_ERROR:
			LoadingDialog.Close();
			ws.Toast("下载失败: "+ msg.obj.toString());
			//delete file 
			Log.i("info", "download error delete file");
			File file = new File(getApp().GetDataBasePath());
			if(file.exists()){
				file.delete();
			}
			m_inSyncProc = false;
			getActivity().finish();
			break;
		case Utility.Sync_Query_APK_Version:
			if(ws.queryResult ==null|| ws.queryResult.isEmpty()|| ws.queryResult.equals("null")) return;
			isCheckedApkVersion = true;
			String fileName = ws.queryResult;
			new AlertDialog.Builder(getActivity())
			.setTitle("发现新版本")
			.setMessage("是否马上下载?")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("下载",new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which)
				{	
					Log.i("ApkDownLoad", ws.queryResult);
					Uri uri = Uri.parse(ws.queryResult);
					Intent intent =new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			} ) 
			.setNegativeButton(R.string.cancel_button_text, null)
			.show();

			
			break;
		}

	}
	 public void onContextMenuClosed(Menu menu){
		 
	 }
}
