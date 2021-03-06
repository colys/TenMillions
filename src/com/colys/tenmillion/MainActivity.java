package com.colys.tenmillion;

import CustomViews.*;
import DataAccess.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.view.ViewPager.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.PopupMenu.*;

import com.colys.tenmillion.Entity.*;

import java.io.*;
import java.text.*;
import java.util.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import javax.security.auth.*;

public class MainActivity extends FragmentActivity implements OnMenuItemClickListener ,TabViewFragment.OnFragmentListener
{



	String m ="1";

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	static String directory;

	//public static TabViewFragment tabViewFragment;

	static ConnectivityReceiver connectivityReceiver ;

	public static final int Login_Request_code = 100;

	boolean canUseLocal = true;

	Button btnMenu,btnMenuAdd;

	boolean startDelayed = false;

	private	WSView ws ;

	MyApplication mApp ;

	BasicAccess m_Access;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);				
		mApp = (MyApplication) getApplication();
		m_Access = new BasicAccess(this);
		ws = new WSView(this);
		handler = ws.CreateHandle(handlerCallback);
		ws.WsErrorCallback = new android.os.Handler.Callback(){
			@Override
			public boolean handleMessage(Message msg)
			{
				onHandleErrorMessage(msg);
				return false;
			}

		};
		File file = new File(mApp.GetDataBasePath());
		if (!file.exists())
		{
			copyAssetsToFilesystem("TenMillion.db", mApp.GetDataBasePath());
		}
		String dbVal;
		try
		{
			dbVal = m_Access.Visit(DefaultAccess.class).ExecuteScalar("select Value from Configs where key='database_version'");
		}
		catch (Exception e)
		{
			dbVal = "2.0";
		}

		if (!dbVal .equals("2.1"))
		{
			m_Access.Close(true);
			copyAssetsToFilesystem("TenMillion.db", mApp.GetDataBasePath());
		}
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//titlebar为自己标题栏的布局
		btnMenu = (Button) findViewById(R.id.titlebar_menu);
		btnMenuAdd = (Button) findViewById(R.id.titlebar_add);
		InitMenu();

		btnMenu.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					popup.show();
				}
			});	
		btnMenuAdd.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					openOptionsMenu();
				}
			});	


		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter); 
		mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

				int lastArg0 = -1;


				@Override
				public void onPageScrollStateChanged(int arg0)
				{

				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2)
				{

				}

				@Override
				public void onPageSelected(int arg0)
				{

				}

			});
	}

	static Fragment[] pageArr =new Fragment[2];

	private void RefreshData()
	{
		for (int i=0;i < pageArr.length;i++)
		{
			if (pageArr[i] != null)
			{
				ws.Toast("refresh data of " + pageArr[i].getClass().getName());
				((TabViewFragment)pageArr[i]).FirstShow();
			}
		}
	}

	public static void InitConnectivity(Context context)
	{
		if (connectivityReceiver == null)
		{
			connectivityReceiver = new ConnectivityReceiver(context);

			connectivityReceiver.setOnNetworkAvailableListener(new ConnectivityReceiver.OnNetworkAvailableListener() {

					@Override
					public void onNetworkUnavailable()
					{
						Toast toast = Toast.makeText(connectivityReceiver.getContext(), "网络连接已断开，将不会同步更改", Toast.LENGTH_SHORT); 
						toast.show();
						//tabViewFragment.GetMessageHandler().removeCallbacks(runnable);
						//DoWhereNoNetwork();
					}

					@Override
					public void onNetworkAvailable()
					{
						Toast toast = Toast.makeText(connectivityReceiver.getContext(), "网络连接已恢复", Toast.LENGTH_SHORT); 
						toast.show();
						//tabViewFragment.GetMessageHandler().removeCallbacks(runnable);
						((MainActivity)connectivityReceiver.getContext()).StartSyncTimer(true);
						//Toast toast = Toast.makeText(MainActivity.this , "网络已经连接，您可以继续使用本地数据，也可点击菜单的联网模式！", Toast.LENGTH_SHORT); 
						//toast.show();
					}
				});

		}
	}


	@Override
	public void onResume()
	{
		super.onResume();

		if (mApp.getCurrentUser() == null || mApp.getCurrentUser().ID == 0)
		{
			GotoActivity(LoginActivity.class, Login_Request_code);
		}
		if (connectivityReceiver == null) InitConnectivity(this);
		connectivityReceiver.bind();

	}

	@Override
	public void onPause()
	{
		if (connectivityReceiver != null) connectivityReceiver.unbind(); 
//		if(tabViewFragment!=null){
//			tabViewFragment.GetMessageHandler().removeCallbacks(runnable);  
//		}
		super.onPause();

	}
	boolean isNoNetworkShow=false;

	PopupMenu popup ;

	public void InitMenu()
	{
		if (popup == null)
		{
			popup = new PopupMenu(this, btnMenu);// This activity implements OnMenuItemClickListener
			popup.setOnMenuItemClickListener(this);
			getMenuInflater().inflate(R.menu.main, popup.getMenu());

			offLineMenuItem = popup.getMenu().getItem(4);
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item)
	{
		switch (item.getItemId())
		{

			case R.id.action_task:
				GotoActivity(TaskActivity.class);
				break;
	        case R.id.action_plan:
				GotoActivity(MonthPlanActivity.class);
				break;	 
	        case R.id.action_train_report:
	        	GotoActivity(TrainReportActivity.class);
	        	break;
	        case R.id.action_train_plan:
	        	GotoActivity(TrainPlanQueryActivity.class);
	        	break;

			case R.id.action_sync:
				//ExecServerError 
				m_Access.OpenTransConnect();
				Cursor cur = m_Access.Visit(DefaultAccess.class).Query("select ValueJson from ExecServerError");				
				try
				{
					while (cur.moveToNext())
					{  
						LinkedList<SynContent> lst = SynContent.ListFromJson(cur.getString(0));						
						m_Access.Visit(DefaultAccess.class).ExecSynContents(lst);					
					}
					cur.close();
					m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from ExecServerError");		
				}
				catch (Exception e)
				{
					ws.Toast(e.getMessage());
					cur.close();
					m_Access.Close(false);
					break;
				}				
				m_Access.Close(true);
				StartSyncTimer(true);
				break;
				//return SyncMenuClick(item);
			case R.id.action_sync_clear:
				if (m_inSyncProc)
				{
					return false;
				}

				String cc;
				try
				{
					cc = m_Access.Visit(DefaultAccess.class).ExecuteScalar("select count(0) from UnSyncRecords");
				}
				catch (Exception e)
				{
					cc = "";
					ws.Toast(e.getMessage());
					break;
				}
				if (!cc.equals("0"))
				{
					ws.Toast("请先同步本地内容");
					break;
				}
				mApp.getCurrentUser().SyncToken = 0;
				try
				{
					m_Access.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "update Configs set value = '0' where key='" + ConfigItem.Synch_Token + "'");
				}
				catch (Exception e)
				{
					ws.Toast(e.getMessage());
					break;
				}
				m_Access.Close(true);
				StartSyncTimer(true);
				/*File dbFile = new File(directory + "/TenMillion.db");
				 if (dbFile.exists())
				 {
				 dbFile.delete();
				 }
				 SetAsOffline(false);*/
				break;
			case R.id.action_shen_gou:
				GotoActivity(ShenGouActivity.class);
				break;	
			case R.id.action_map:
				GotoActivity(MemberMapActivity.class);
				break;
			case R.id.action_house:
				GotoActivity(HouseActivity.class);
				break;
			case R.id.action_options:
				openOptionsMenu();
				break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.options, menu);
		return super.onCreateOptionsMenu(menu);
	}
	boolean comfirmExit=false;

	@SuppressWarnings("static-access")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (m_inSyncProc)
			{
				Toast toast = Toast.makeText(this, "同步中...请稍后!" , Toast.LENGTH_SHORT); 
				toast.show();
				return false;
			}
			if (comfirmExit)
			{
				ws.handler.removeCallbacks(runnable);
				System.exit(0);
			}
			else
			{
				if (ConnectivityReceiver.hasConnection())
				{
					if (CheckLocal(false)) return false;
				}
				comfirmExit = true;
				Toast toast = Toast.makeText(this, "再按一次退出程序!" , Toast.LENGTH_SHORT); 
				toast.show();
				ws.handler.postDelayed(runnable, 2000);
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	static MenuItem offLineMenuItem;
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.action_new_member:
				GotoActivity(EditMemberActivity.class);
				break;
	        case R.id.action_new_people_coming:
				GotoActivity(PeopleComingActivity.class);
				break;	 

	        case R.id.action_new_task:
				GotoActivity(EditTaskActivity.class);
				break;	 
			case R.id.action_new_ask_for_leave:
				GotoActivity(EditAskForLeaveActivity.class);
				break;
			case R.id.action_execute_sql:
				GotoActivity(ExecuteSqlActivity.class);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void closeOptionsMenu()
	{
		super.closeOptionsMenu();
	}

	private void GotoActivity(Class<?> c)
	{
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), c);
		startActivityForResult(intent, 0);
	}

	private void GotoActivity(Class<?> c, int code)
	{
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), c);
		startActivityForResult(intent, code);
	}


	@Override
    public void onConfigurationChanged(Configuration newConfig)
	{

	    super.onConfigurationChanged(newConfig);

	}


	Runnable runnable=new Runnable() {

	    @Override
	    public void run()
		{
	    	comfirmExit = false;	    	
		}
	};


	public  void SyncFinish(boolean hasData, boolean hasError)
	{		
		if (hasData)
		{
			ws.Toast("refresh data");
			RefreshData();
			//tabViewFragment.FirstShow();
		}
		if (!isCheckedApkVersion && ConnectivityReceiver.hasConnection())
		{
			//检测新版本
			String version = Utility.getVersionName(this);
			ws.visitServices("APKHaveNewVersion", new String[]{ "version"}, new String[]{version}, Utility.Sync_Query_APK_Version, false);

		}
		if (hasError) Log.d("sync", "error");
		else Log.d("sync", "finish,has data " + hasData);
	}

	void StartSyncTimer(boolean now)
	{	 
		if (ConnectivityReceiver.hasConnection())
		{
    		Sync();
    	}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}


		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			TabViewFragment fragment = null;
			switch (position)
			{
				case 0:
					fragment = new MonthPeopleComingFragment();
					break;
				case 1:
					fragment = new DayWorkFragment();
					break;
				case 2:
					fragment = new ClassifyMemberFragment();
					break;

			}
			return fragment;
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}


	String strUnSyncKeys = null;
	public static boolean m_inSyncProc = false;
	static boolean m_SyncHasData = false;


	public void Sync()
	{
		if (m_inSyncProc) return;
		Log.d("sync", "start");
		m_inSyncProc = true;
		m_SyncHasData = false;
		CheckLocal(true);	
	}

	public boolean CheckLocal(boolean server)
	{
		//检查本地
		LinkedList<UnSyncRecords> lst = m_Access.Visit(DefaultAccess.class).QueryEntityList(UnSyncRecords.class, "select * from UnSyncRecords limit 20");
    	boolean hasLocal = lst.size() > 0;
		if (lst != null && hasLocal)
		{
    		strUnSyncKeys = "";
    		String strContents="[";
    		for (UnSyncRecords unSyn : lst)
			{
    			strContents += unSyn.Sql + ",";
    			strUnSyncKeys += unSyn.ID +  ",";
    		}
    		strUnSyncKeys = strUnSyncKeys.substring(0, strUnSyncKeys.length() - 1);
    		strContents = strContents.substring(0, strContents.length() - 1);
    		strContents += "]";
    		ws.visitServices("SyncTeminalContent", new String[]{"token","time","machine","contents"} ,
							 new String[]{String.valueOf(mApp.getCurrentUser().SyncToken), lst.get(0).SyncTime,"colys-phone",strContents},
							 Utility.Sync_Content_To_Server);
    		LoadingDialog.UpdateMessage("正在与服务器同步。。。");
    	}
		else
		{
    		if (strUnSyncKeys != null)
			{
    			strUnSyncKeys = null;
    			m_SyncHasData = true;
    			ws.Toast("同步本地数据到服务器成功");
    			m_SyncHasData = true;
    		}
    		if (server) CheckServer();
    	}
    	m_Access.Close(true); 
    	return hasLocal;
	}

	private void CheckServer()
	{
		m_inSyncProc = true;
		//检查服务器,获取服务器上的最大token
		ws.visitServices("GetToken", null, null, Utility.Sync_Query_Token, false);


	}


    private void CheckServerContent(int serverToken)
	{
    	if (serverToken == 0)
		{
    		m_inSyncProc = false; 
    		SyncFinish(m_SyncHasData, false);
    		return; 
    	}
    	mApp.getCurrentUser().SyncToken  = serverToken;
		ConfigItem configItem_Sync=null;
		try
		{
			configItem_Sync = m_Access.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "select * from Configs where key='" + ConfigItem.Synch_Token + "'");
		}
		catch (Exception e)
		{
			ws.ToastLong(e.getMessage());
			return;
		}
		m_Access.Close(true);
		long Synch_Token = 0;
		if (configItem_Sync.Value != null) Synch_Token = Integer.valueOf(configItem_Sync.Value);
		if (Synch_Token < mApp.getCurrentUser().SyncToken)
		{
			if (Synch_Token == 0 || (serverToken - Synch_Token) > 100)
			{
				mApp.getCurrentUser().SyncToken  = mApp.getLoginSyncToken();

				ws.visitServices("downloaddb", "m", m, Utility.DOWN_COMPLETE);
				LoadingDialog.UpdateMessage("正在下载这" + m + "个月的数据。。。");
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


			}
			else
			{
				//sync
				ws.visitServices("GetSyncContent", "startToken", String.valueOf(Synch_Token), Utility.Sync_Server_Content);
				LoadingDialog.UpdateMessage("正在同步数据。。。");
			}
		}
		else
		{
			m_inSyncProc = false;
			SyncFinish(m_SyncHasData, false);
		}
	}


	public void onHandleErrorMessage(Message msg)
	{
		switch (msg.what)
		{	
			case Utility.Sync_Query_Token:
			case Utility.Sync_Content_To_Server:
				m_inSyncProc = false;
				SyncFinish(m_SyncHasData, true);
				break;

			case Utility.Sync_Server_Content:
				ws.Toast("同步数据失败，将不能查看最新的内容！");
				m_inSyncProc = false;
				SyncFinish(m_SyncHasData, true);
				break;
			case Utility.DOWN_COMPLETE:
				//"下载"文件出错
				m_inSyncProc = false;
				break;
		}

	}


	private boolean ExecServerContents(String json, boolean updateConfigs)
	{
		m_Access.OpenTransConnect();
		try
		{		    
		    LinkedList<SynContent> lst = SynContent.ListFromJson(json);	
			m_Access.ExecSynContents(lst);
		    if (updateConfigs) m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("update configs set value ='" + mApp.getCurrentUser().SyncToken + "' where key='" + ConfigItem.Synch_Token + "'");
		    m_Access.Close(true);
	   	}
		catch (Exception e)
		{				 
	   		m_Access.Close(false);
			ws.Toast("Exec Server Contents error : " + e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean isCheckedApkVersion = false;

	private boolean copyAssetsToFilesystem(String assetsSrc, String des)
	{  
		// Log.i(tag, "Copy "+assetsSrc+" to "+des);  
		InputStream istream = null;  
		OutputStream ostream = null;  
		try
		{  
			AssetManager am = this.getAssets();  
			istream = am.open(assetsSrc);  
			ostream = new FileOutputStream(des);  
			byte[] buffer = new byte[1024];  
			int length;  
			while ((length = istream.read(buffer)) > 0)
			{  
				ostream.write(buffer, 0, length);  
			}  
			istream.close();  
			ostream.close();  
		}  
		catch (Exception e)
		{  
			e.printStackTrace();  
			try
			{  
				if (istream != null)  
					istream.close();  
				if (ostream != null)  
					ostream.close();  
			}  
			catch (Exception ee)
			{  
				ee.printStackTrace();  
			}  
			return false;  
		}  
		return true;  
	}  

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == Login_Request_code)
		{
			if (resultCode == android.app.Activity.RESULT_OK)
			{
				//登录成功,同步数据
				ws.Toast("login success, request server data for synch");
				StartSyncTimer(true);
			}
			else finish();				
			return;
		}
		if (resultCode == android.app.Activity.RESULT_OK)
		{
			RefreshData();			 
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	Handler handler;

	android.os.Handler.Callback handlerCallback = new android.os.Handler.Callback(){

		@SuppressLint("HandlerLeak")
		@Override
		public boolean handleMessage(Message msg)
		{	
			switch (msg.what)
			{
				case Utility.Sync_Query_Token:
					int token ;
					if (ws.queryResult == null || ws.queryResult.isEmpty()) token = 0;
					else token = Utility.parseInt(ws.queryResult);
					CheckServerContent(token);
					break;
				case Utility.Sync_Server_Content:
					if (ExecServerContents(ws.queryResult, true))
					{
						SyncFinish(true, false);
						m_SyncHasData = true;
					}
					else m_SyncHasData = false;
					m_inSyncProc = false;
					break;
				case Utility.Sync_Content_To_Server:
					LoadingDialog.Close();
					int splitIndex = ws.queryResult.indexOf("[");			 
					String sycContents = null ;
					try
					{
						//有需要客户端去同步的内容
						if (splitIndex > 0)
						{
							mApp.getCurrentUser().SyncToken = Utility.parseInt((ws.queryResult.substring(0, splitIndex)));
							sycContents = ws.queryResult.substring(splitIndex);			
							if (!ExecServerContents(sycContents, false))
							{
								ContentValues values=new ContentValues();
								values.put("SyncTime", Utility.GetNowString("yyyy-MM-dd HH:mm:ss"));
								values.put("ValueJson", sycContents);
								m_Access.Visit(DefaultAccess.class).ExecuteInsert("ExecServerError", values);
							}
						}
						else 
							mApp.getCurrentUser().SyncToken = Utility.parseInt(ws.queryResult);
						//update config and delete unsync
						m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from UnSyncRecords where ID in (" + strUnSyncKeys + ")");
						m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("update Configs set value='" + mApp.getCurrentUser().SyncToken + "' where key ='" + ConfigItem.Synch_Token + "'");
					}
					catch (Exception e)
					{
						ws.Toast(e.getMessage());
						ws.Toast("内容已经同步，但是清空unsync表和记录configs表失败!");
						return true;
					}
					m_Access.Close(true);
					CheckLocal(true);	
					break;			
				case Utility.DOWN_START:
					LoadingDialog.UpdateMessage("正在下载近" + m + "个月的数据，请稍候。。。");
					break;
				case Utility.DOWN_POSITION:
					LoadingDialog.UpdateMessage(Utility.GetPercent(msg.arg1, msg.arg2));
					break;		
				case Utility.DOWN_COMPLETE:				
					m_SyncHasData = true;
					try
					{				
						String[] sqlArr = ws.queryResult.split(";");
						m_Access.Close(true);
						m_Access.OpenTransConnect();
						String[] tableArray = new String[]{"AskForLeave","DayWorkHouse","DayWorkDetail","House","Member","MonthPlan","PeopleComing","PeopleWorking","ShenGouRecords","Tasks","TrainItems","TrainPlan","TrainRecords","TrainReport"};
						for (String tableName : tableArray)
						{
							m_Access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from " + tableName);
						}
						for (String sql : sqlArr)
						{
							m_Access.Visit(DefaultAccess.class).ExecuteNonQuery(sql);
						}
						Utility.LogConfigs(m_Access, mApp);					
						m_Access.Close(true);
					}
					catch (Exception e)
					{
						ws.ToastLong("sync error \r\n " + e.getMessage());
						LoadingDialog.Close();
						return true;
					}
					m_inSyncProc = false;
					SyncFinish(m_SyncHasData, false);
					LoadingDialog.Close();
					break;
				case Utility.Down_ERROR:
					LoadingDialog.Close();
					ws.Toast("下载失败: " + msg.obj.toString());
					//delete file 
					Log.i("info", "download error delete file");
					File file = new File(mApp.GetDataBasePath());
					if (file.exists())
					{
						file.delete();
					}
					m_inSyncProc = false;
					finish();
					break;
				case Utility.Sync_Query_APK_Version:
					if (ws.queryResult == null || ws.queryResult.isEmpty() || ws.queryResult.equals("null")) return true;
					isCheckedApkVersion = true;
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("发现新版本")
						.setMessage("是否马上下载?")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("下载", new DialogInterface.OnClickListener() {					
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								Log.i("ApkDownLoad", ws.queryResult);
								Uri uri = Uri.parse(ws.queryResult);
								Intent intent =new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
							}
						}) 
						.setNegativeButton(R.string.cancel_button_text, null)
						.show();	

					break;
			}
			return true;
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		for (int i=0;i < pageArr.length;i++)
		{
			if (pageArr[i] != null)
				getSupportFragmentManager().putFragment(outState, pageArr[i].getClass().getName(), pageArr[i]);
		}
	}
	


	@Override
	public void onFragmentStart(TabViewFragment f)
	{
		pageArr[0] = pageArr[1];
		pageArr[1] = f;
	}
	

	@Override
	public void onFragmentCreated(TabViewFragment f)
	{
		f.Init(ws, m_Access);
	}
	@Override
	public void onContextMenuClosed(Menu menu) {
		if(pageArr[1]!=null){
			((TabViewFragment)pageArr[1]).onContextMenuClosed(menu);
		}
		super.onContextMenuClosed(menu);
	}

}
