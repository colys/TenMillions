package com.colys.tenmillion;
import CustomViews.*; 
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.os.*;
import android.webkit.*;
import com.colys.tenmillion.Entity.*;
import java.util.*;
import com.google.gson.reflect.*;
import com.google.gson.*;
import android.util.*;

public class MemberMapActivity extends WSActivity
{
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
		if(Utility.UseLocal){
			LinkedList<Member> lst = ws.QueryLocalMember(true,null);
			java.lang.reflect.Type listType = new TypeToken<LinkedList<Member>>(){}.getType();
			Gson gson=new Gson();
			jsAccess.SetJson( gson.toJson(lst,listType));
			LoadView();
		}else{
			ws.visitServices("Member_GetAllMemberJson", new String[]{"tree","groupID"},new String[]{"true",String.valueOf(getCurrentGroupId())}, 0);			
		}
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
		String url ="file:///"+ MainActivity.directory+"Assets/MapView.htm";
		url="file:///android_asset/MapView.htm";
		webView.loadUrl(url);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==RESULT_OK) {
			webView.reload();
			setResult(RESULT_OK);
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			activity.startActivityForResult(intent,0);
		}
		

	}

}
