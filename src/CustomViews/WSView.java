package CustomViews;

import DataAccess.*;
import android.annotation.*;
import android.app.*;
import android.os.*;
import android.widget.*;

import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.*;
import java.util.*;
import org.apache.http.message.*;
import android.util.*;


public class WSView
{

	public Activity activity;

	DBHelper dbHelper ;

	public String queryResult;
	
	MyApplication mApp;


	public WSView(Activity activity)
	{
		setActivity(activity);
	}

	private void setActivity(Activity activity)
	{
		this.activity = activity;
		mApp=((MyApplication) activity.getApplication());
		dbHelper = new DBHelper(activity,mApp.GetDataBasePath()); 
	}

	public DBHelper GetSqlite()
	{
		return dbHelper;
	}

	public void visitServices(String method, final String key, final String value, final int what)
	{
		visitServices(method, new String[]{key}, new String[]{value}, what);
	}
	public void visitServices(String method, final int what)
	{
		visitServices(method, new String[0], null, what);
	}
	public void visitServices(final String method, final String[] keys, final String[] values, final int what)
	{
		visitServices(method, keys, values, what,true);
	}
	public void visitServices(final String method, final String[] keys, final String[] values, final int what,boolean showModal)
	{
		new Thread(){
			@Override
			public void run()
			{	
				String queryPlanResult;
				try
				{
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					if(keys!=null){
						for (int i=0;i < keys.length;i++)
						{
							params.add(new BasicNameValuePair(keys[i], values[i]));
						}
					}
					queryPlanResult = WSHelper.GetResponse(method, params);										 

				}
				catch (Exception e)
				{					 
					queryPlanResult = "err:" + e.getMessage();
				}
				Message msg = Message.obtain(handler, what, queryPlanResult);
				handler.sendMessage(msg);
			}
		}.start();	
		if(showModal) LoadingDialog.Show(activity);
	}

	public Handler handler = new Handler(){
		@SuppressLint("HandlerLeak")
		@Override
		//������Ϣ���ͳ����ʱ���ִ��Handler������
		public void handleMessage(Message msg)
		{
			//Log.i("message", Utility.GetNowString("HH:mm:ss")+ "call message handle with what:"+ msg.what );
			if (msg.obj != null)
			{
				queryResult = msg.obj.toString();
				if (queryResult.indexOf("err:") == 0)
				{
					LoadingDialog.Close();
					Toast toast = Toast.makeText(activity , queryResult.substring(4), Toast.LENGTH_SHORT); 
					toast.show();
					if(WsErrorCallback!=null) WsErrorCallback.handleMessage(msg);
					return;
				}
			}
			if (msg.what < 100000)
				LoadingDialog.Close();
			WSView.this.callback.handleMessage(msg);
			
		}
	};
	 
	
	android.os.Handler.Callback callback;
	public android.os.Handler.Callback WsErrorCallback;
 
	@SuppressLint("HandlerLeak")
	public Handler CreateHandle(android.os.Handler.Callback callback)
	{ 
		this.callback = callback;		
		return handler;
	}
	
	public void ToastLong(String str)
	{
		Toast toast = Toast.makeText(activity, str, Toast.LENGTH_LONG); 
		toast.show();
	}


	public void Toast(String str)
	{
		Toast toast = Toast.makeText(activity, str, Toast.LENGTH_SHORT); 
		toast.show();
	}

	public void Toast(int stringID)
	{
		Toast toast = Toast.makeText(activity, stringID, Toast.LENGTH_SHORT); 
		toast.show();
	}
	
	public LinkedList<Member> QueryLocalMember(boolean tree,BasicAccess access)
	{
		return QueryLocalMember(tree,false,access);
		
	}
	 
	public LinkedList<Member> QueryLocalMember(boolean tree,Boolean online,BasicAccess access)
	{
		boolean needClose = access == null;
		//Log.i("tip","Query Local Member Table ");
		String sql;
		if(tree) {
			sql="select * from Member where groupid='"+ mApp.getCurrentGroupID() +"' and  ReferenceID is null";
			
		}
		else {
			sql="select * from member where groupid='"+ mApp.getCurrentGroupID()+
				"'";
			if(online) sql+=" and status > -1";
		}
		LinkedList<Member> memberList;
		if(access == null)  access=new BasicAccess(activity);		
			memberList = access.Visit(DefaultAccess.class).QueryEntityList(Member.class , sql);			 
		if(tree){
			for (Member m :memberList)
			{
				GetCounterMans(m,access);
			}	
		}
		if(needClose) access.Close(true);
		return memberList;
	}


    public void GetCounterMans(Member reference,BasicAccess access)
    {
    	String sql = "select * from Member where  groupid='"+ mApp.getCurrentGroupID() +"' and  referenceID = '" + reference.ID+"'";
        reference.CounterMan =  access.Visit(DefaultAccess.class).QueryEntityList(Member.class , sql); 
        for (Member m : reference.CounterMan)
		{
            GetCounterMans(m,access);
            reference.TotalFenE += m.TotalFenE;
        }
    }

}
