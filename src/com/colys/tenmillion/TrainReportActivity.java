package com.colys.tenmillion;

import java.util.LinkedList;

import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.DefaultAccess;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.colys.tenmillion.Entity.Member;
import com.colys.tenmillion.Entity.TrainReport;
import com.colys.tenmillion.MemberMapActivity.JavascriptAccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TrainReportActivity extends WSActivity {
	WebView webView;
	JavascriptAccess jsAccess;
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		webView = (WebView) findViewById(R.id.member_map_view);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);	
		jsAccess =new JavascriptAccess(this);
		webView.addJavascriptInterface(jsAccess,"millions");
		QueryMember();
	}
	
	public void QueryMember(){ 
			BasicAccess access=new BasicAccess(this);
			LinkedList<TrainReport> lst= access.Visit(DefaultAccess.class).QueryEntityList(TrainReport.class, "select SuZhiKe,WenHuaKe, member.Name MemberName, TrainReport.* from TrainReport join member on member.ID = TrainReport.MemberID where Member.groupid='"+ getCurrentGroupId() +"' and  Member.Status > -1" );
			java.lang.reflect.Type listType = new TypeToken<LinkedList<Member>>(){}.getType();
			Gson gson=new Gson();
			jsAccess.SetJson( gson.toJson(lst,listType));
			LoadView();
		 
	}
	

	protected int getLayout()
	{
		return R.layout.activity_member_map;
	}
	
	
	
	public void onHandleMessage(Message msg){
		
		//Log.i("tip","service back , msg what is "+ msg.what+"; data is : "+ ws.queryResult.substring(0,100)+"...");
		if(msg.what ==0){
			Log.i("tip","call load web page");
			jsAccess.SetJson(ws.queryResult);
			LoadView();
		}
	}
	
	private void LoadView(){
		String url="file:///android_asset/train.html";
		webView.loadUrl(url);
		
	}
	
	public class JavascriptAccess{
		Activity activity;
		public JavascriptAccess(Activity a){
			activity= a;
		}

		String membersJsonStr;
		public void SetJson(String val){
			membersJsonStr =val;
		}
		
		@JavascriptInterface
		public String GetJson(){
			Log.i("tip","get json call from js");
			return membersJsonStr;
		}
		@JavascriptInterface
		public void ViewMember(String id){ 
			if(id == null) return;
			Intent intent = new Intent();
			Bundle bundle=new Bundle(); 
			bundle.putString("memberID", id); 
			intent.putExtras(bundle);
			intent.setClass(activity,EditMemberActivity.class);
			activity.startActivity(intent);
		}
	}
}
