package com.colys.tenmillion;

import CustomViews.*;
import DataAccess.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.Cursor;
import android.database.sqlite.*;
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

public class MainActivity extends FragmentActivity implements OnMenuItemClickListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	Fragment dayWorkFragment = new DayWorkFragment();

	Fragment classifyMemberActivity = new ClassifyMemberFragment();

	MonthPeopleComingFragment monthPeopleComingFragment =new MonthPeopleComingFragment();

	static String directory;

	public static TabViewFragment tabViewFragment;

	static ConnectivityReceiver connectivityReceiver ;

	boolean canUseLocal = true;

	Button btnMenu,btnMenuAdd;

	boolean startDelayed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);// �������
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//titlebar为自己标题栏的布局
		
		MyApplication mMyApplication = (MyApplication) getApplication();
		if(mMyApplication.getCurrentUser() == null){			 
			GotoActivity(LoginActivity.class); 
			return;
		}
			btnMenu = (Button) findViewById(R.id.titlebar_menu);
			btnMenuAdd = (Button) findViewById(R.id.titlebar_add);
			InitMenu();
			MainActivity.InitConnectivity(getApplicationContext());
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
			String sdcard = Utility.GetSDCardPath();
			if (sdcard == null)
			{
				directory = null;
			}
			else
			{
				directory = sdcard + "/.tenMillion/";


			}


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
//						if (lastArg0 == arg0) return;					
//						lastArg0 = arg0;
//						Fragment f= mSectionsPagerAdapter.getItem(arg0);
//						if(!f.isVisible() || f.isHidden() || !f.isAdded()) return;
//						//Log.i("tip", "tab is " + f.getClass().getName());
//						tabViewFragment = (TabViewFragment)f;	
//						if (tabViewFragment.isFirstShow)
//						{
//							Log.i("tip", "run firstShow method");
//							if (Utility.UseLocal || ConnectivityReceiver.hasConnection())
//							{				 
//								tabViewFragment.FirstShow();
//								tabViewFragment.isFirstShow = false;
	//
//							}
////							if (!startDelayed)
////							{
////								StartSyncTimer(true);
////								startDelayed = true;
////							}
//						}
					}

					@Override
					public void onPageSelected(int arg0)
					{
					}

				});

//			if (directory == null) canUseLocal = false;
//			else
//			{
//				if (!ConnectivityReceiver.hasConnection())
//				{
//					File dbFile = GetDBFile();
//					if (!dbFile.exists())	canUseLocal = false;	
//					DoWhereNoNetwork();
	//
//				}
//			}
			StartSyncTimer(true);
		
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
						if (tabViewFragment == null) return;
						//tabViewFragment.GetMessageHandler().removeCallbacks(runnable);
						//DoWhereNoNetwork();
					}

					@Override
					public void onNetworkAvailable()
					{
						if (tabViewFragment == null) return;
						//tabViewFragment.GetMessageHandler().removeCallbacks(runnable);
						StartSyncTimer(true);
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
		MyApplication mApp = (MyApplication) getApplication();
		if(mApp.getCurrentUser() == null || mApp.getCurrentUser().ID ==0){
			GotoActivity(LoginActivity.class);
			return;
		}		
		if (connectivityReceiver != null) connectivityReceiver.bind();
		StartSyncTimer(true);
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



	/*
	 private void DoWhereNoNetwork()
	 {
	 Log.i("tip", "network not connect");
	 if (isNoNetworkShow) return;
	 if (!canUseLocal)
	 {	
	 new AlertDialog.Builder(MainActivity.this)      
	 .setTitle(R.string.dialog_tip_title)   
	 .setMessage(R.string.dialog_tip_no_network)    
	 .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {

	 @Override
	 public void onClick(DialogInterface arg0, int arg1)
	 {
	 finish();					
	 }
	 })
	 .show(); 

	 }
	 else
	 {
	 new AlertDialog.Builder(MainActivity.this)      
	 .setTitle(R.string.dialog_tip_title)    
	 .setMessage(R.string.dialog_tip_no_network_use_local)    
	 .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {						
	 @Override
	 public void onClick(DialogInterface dialog, int which)
	 {
	 isNoNetworkShow = false;
	 ToDoOffline(false);
	 }
	 })  
	 .setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
	 @Override
	 public void onClick(DialogInterface arg0, int arg1)
	 {
	 finish();
	 }	
	 }).show(); 
	 }
	 isNoNetworkShow = true;
	 }
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == android.app.Activity.RESULT_OK)
		{
			tabViewFragment.FirstShow();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
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
				BasicAccess access =new BasicAccess(this.getApplicationContext());
				access.OpenTransConnect();
				Cursor cur = access.Visit(DefaultAccess.class).Query("select ValueJson from ExecServerError");				
				try
				{
					while (cur.moveToNext())
					{  
						LinkedList<SynContent> lst = SynContent.ListFromJson(cur.getString(0));						
						access.Visit(DefaultAccess.class).ExecSynContents(lst);					
					}
					cur.close();
					access.Visit(DefaultAccess.class).ExecuteNonQuery("delete from ExecServerError");		
				}
				catch (Exception e)
				{
					tabViewFragment.ws.Toast(e.getMessage());
					cur.close();
					access.Close(false);
					break;
				}				
				access.Close(true);
				StartSyncTimer(true);
				break;
				//return SyncMenuClick(item);
			case R.id.action_sync_clear:
				if (tabViewFragment.m_inSyncProc)
				{
					return false;
				}
				BasicAccess access1 =new BasicAccess(this.getApplicationContext());
				String cc;
				try
				{
					cc = access1.Visit(DefaultAccess.class).ExecuteScalar("select count(0) from UnSyncRecords");
				}
				catch (Exception e)
				{
					cc = "";
					tabViewFragment.ws.Toast(e.getMessage());
					break;
				}
				if (!cc.equals("0"))
				{
					tabViewFragment.
						ws.Toast("请先同步本地内容");
					break;
				}
				tabViewFragment.getApp().getCurrentUser().SyncToken = 0;
				try
				{
					access1.Visit(DefaultAccess.class).QueryEntity(ConfigItem.class, "update Configs set value = '0' where key='" + ConfigItem.Synch_Token + "'");
				}
				catch (Exception e)
				{
					tabViewFragment.ws.Toast(e.getMessage());
					break;
				}
				access1.Close(true);
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

		// Inflate the menu; this adds items to the action bar if it is present.
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
			if (tabViewFragment.m_inSyncProc)
			{
				Toast toast = Toast.makeText(this, "同步中...请稍后!" , Toast.LENGTH_SHORT); 
				toast.show();
				return false;
			}
			if (comfirmExit)
			{
				tabViewFragment.GetMessageHandler().removeCallbacks(runnable);
				System.exit(0);
			}
			else
			{
				if (ConnectivityReceiver.hasConnection())
				{
					if (tabViewFragment.CheckLocal(true)) return false;
				}
				comfirmExit = true;
				Toast toast = Toast.makeText(this, "再按一次退出程序!" , Toast.LENGTH_SHORT); 
				toast.show();
				tabViewFragment.GetMessageHandler().postDelayed(runnable, 2000);
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
		//Uri uri = Uri.parse("http://onemillion.apphb.com/map.htm"); //urlΪ��Ҫ��ӵĵ�ַ
		//Intent intent =new Intent(Intent.ACTION_VIEW, uri);
		startActivityForResult(intent, 0);
	}


	/*private boolean SyncMenuClick(MenuItem item)
	 {
	 if (directory == null) return false;
	 offLineMenuItem = item;
	 if (item.getTitle().equals(getString(R.string.action_sync_off)))
	 {
	 File file = new File(directory);
	 if (!file.isDirectory()) file.mkdir();			  

	 if (!ConnectivityReceiver.hasConnection())
	 {
	 File dbFile =GetDBFile();
	 if (dbFile.exists())
	 {
	 ToDoOffline(false);
	 }
	 else
	 {
	 new AlertDialog.Builder(MainActivity.this)      
	 .setTitle(R.string.dialog_tip_title)   
	 .setMessage(R.string.dialog_tip_no_network)    
	 .setPositiveButton(R.string.ok_button_text, null)
	 .show(); 
	 }
	 }
	 else
	 {
	 //is wifi .getTypeName().equals("WIFI") 
	 if (!ConnectivityReceiver.IsWifi())
	 {
	 new AlertDialog.Builder(MainActivity.this)      
	 .setTitle(R.string.dialog_tip_title)    
	 .setMessage(R.string.dialog_tip_not_wifi)    
	 .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {						
	 @Override
	 public void onClick(DialogInterface dialog, int which)
	 {
	 ToDoOffline(true);
	 }
	 })  
	 .setNegativeButton(R.string.cancel_button_text, null) 
	 .show(); 
	 }
	 else
	 {
	 ToDoOffline(true);
	 }
	 }
	 }
	 else
	 {
	 if (ConnectivityReceiver.hasConnection())   SetAsOffline(false);
	 }
	 return false;
	 }*/

	/*private void ToDoOffline(boolean downLoad)
	 {

	 if (downLoad)
	 {
	 //query current date if exists
	 File dbFile =GetDBFile();
	 if (dbFile.exists())
	 {
	 Calendar queryDay = Calendar.getInstance();
	 if (queryDay.get(Calendar.HOUR) < 21)
	 {
	 queryDay.add(Calendar.DATE, -1);
	 }
	 SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
	 String strDate = sdf.format(queryDay.getTime());
	 String sql ="select count(0) from DayWorks where WorkDay='" + strDate  + "'";
	 EntityDBHelper<DayWorks> helper =new EntityDBHelper<DayWorks>(new DBHelper(this), DayWorks.class);
	 int count =0;
	 try
	 {
	 count = helper.QueryCount(sql);
	 }
	 catch (SQLiteException e)
	 {
	 e.printStackTrace();
	 MainActivity.ShowError(this, e);
	 }
	 if (count > 0)
	 {
	 Toast toast = Toast.makeText(this, "已有数据，直接离线显示!" , Toast.LENGTH_SHORT); 
	 toast.show();
	 SetAsOffline(true);
	 return;
	 }
	 }

	 Log.i("tip", "downloading remote db");
	 new Thread(){
	 @Override
	 public void run()
	 {	
	 Utility.DownFile("http://onemillion.apphb.com/DownloadDB.ashx", directory, "TenMillion.db", tabViewFragment.GetMessageHandler());
	 }			 
	 }.start();
	 LoadingDialog.Show(tabViewFragment.getActivity());

	 }
	 else
	 {
	 SetAsOffline(true); 
	 }
	 }


	 //����Ϊ����ģʽ
	 public static void SetAsOffline(boolean off)
	 {
	 if (directory == null) return;
	 Utility.UseLocal = off;		
	 if (tabViewFragment != null)
	 {
	 String tipMsg =tabViewFragment. getString(R.string.action_sync_set_success);

	 if (off)
	 { 
	 offLineMenuItem.setTitle(R.string.action_sync_on);
	 tipMsg += tabViewFragment. getString(R.string.action_sync_off);				

	 }
	 else
	 {
	 Log.i("tip", "use local db");
	 offLineMenuItem.setTitle(R.string.action_sync_off);
	 tipMsg += tabViewFragment. getString(R.string.action_sync_on);
	 }

	 Toast toast = Toast.makeText(tabViewFragment.getActivity() , tipMsg , Toast.LENGTH_SHORT); 
	 toast.show();
	 tabViewFragment.FirstShow();
	 }

	 }
	 */
	public static void ShowError(Context context, Exception ex)
	{	
		Toast toast = Toast.makeText(context , ex.getMessage(), Toast.LENGTH_SHORT); 
		toast.show();
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


	public static void SyncFinish(boolean hasData, boolean hasError)
	{		
		if (tabViewFragment.getActivity() == null || tabViewFragment.getActivity().getApplicationContext() == null || tabViewFragment.getActivity().isFinishing()) return;
		//StartSyncTimer(false);
		if (hasData)
		{
			tabViewFragment.FirstShow();
		}
		if (!TabViewFragment.isCheckedApkVersion && ConnectivityReceiver.hasConnection())
		{
			//检测新版本
			String version = Utility.getVersionName(tabViewFragment.getActivity());
			tabViewFragment.ws.visitServices("APKHaveNewVersion", new String[]{ "version"}, new String[]{version}, Utility.Sync_Query_APK_Version, false);

		}
	}

	static void StartSyncTimer(boolean now)
	{
		if (tabViewFragment != null)
		{
//			int seconds = 0;
//			if (now) seconds = 100;
//			else
//			{
//				if (ConnectivityReceiver.IsWifi())
//				{
//					seconds = 300000;
//				}
//				else seconds = 900000;
//			}
			//runnable.run();
			if (ConnectivityReceiver.hasConnection())
			{
	    		tabViewFragment.Sync();
	    	}
			//tabViewFragment.GetMessageHandler().post(runnable);
			//tabViewFragment.GetMessageHandler().postDelayed(runnable, seconds);
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
			Fragment fragment = null;
			switch (position)
			{
				case 0:
					fragment = monthPeopleComingFragment;
					break;
				case 1:
					fragment = dayWorkFragment;
					break;
				case 2:
					fragment = classifyMemberActivity;
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


	@Override  
	public void onContextMenuClosed(Menu menu)
	{//关闭上下文菜单 
		super.onContextMenuClosed(menu);
		if(tabViewFragment!=null) tabViewFragment.onContextMenuClosed(menu);
	}  
}
