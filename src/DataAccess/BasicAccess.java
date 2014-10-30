package DataAccess;

import android.app.Activity;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.DBEntity;

import CustomViews.*;

public class BasicAccess 
{

	private SQLiteDatabase db;
	DBHelper m_dbhelper;
	Context m_context;
	BasicAccess baseAccess=null;

	public boolean InTrans = false;

	public BasicAccess(Context context)
	{
		m_context = context; 
		MyApplication mapp;
		if( context instanceof  Activity ){
			mapp =(MyApplication) ((Activity)context).getApplication();
		}else
			mapp = (MyApplication) context.getApplicationContext();
		 
		m_dbhelper = new DBHelper(m_context,mapp.GetDataBasePath());

	}

	LinkedList<BasicAccess> accessList =new LinkedList<BasicAccess>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public<T> T Visit(Class<T> accessType)
	{
		if (this.getClass() != BasicAccess.class)
		{
			Log.e("iInvoke error", "Please User BasicAccess!");
			return null;
		}
		for (BasicAccess ba : accessList)
		{
			if (ba.getClass() == accessType)
			{
				return (T) ba;
			}
		}
		T a1 = null;
		Constructor c1;
		try
		{
			c1 = accessType.getDeclaredConstructor(new Class[]{Context.class});
			c1.setAccessible(true);   
			a1 = (T)c1.newInstance(new Object[]{ m_context });  
		}		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		BasicAccess	access =(BasicAccess) a1;
		access.db = this.db;
		access.m_dbhelper = this.m_dbhelper;
		access.m_trans = this.m_trans;
		access.baseAccess = this;
		return a1; 
	}



	public void OpenTransConnect()
	{
		if (m_trans) Close(true);
		m_trans = true;
		for (BasicAccess ba : accessList) ba.m_trans = true;
		Open(); 
	}


	
	<T> T QueryEntity(Class<T> itemClass, String sql) throws Exception
	{
		Cursor c =null;
		T newt = null;
		Open();
		
		c = db.rawQuery(sql, null);
		
		if (c.moveToNext())
		{  
			newt = (T)  itemClass.newInstance();
			DBEntity entity= (DBEntity)newt;
			entity.Parse(c);
			c.close();
			Close();
			return newt;
		}else{
			c.close();
			Close();
			return null;
		}
		
	}

	<T> LinkedList<T> QueryEntityList(Class<T> itemClass, String sql)
	{
		LinkedList<T> lst = new LinkedList<T>();
		Cursor c =null;
		try
		{		

			Open();
			//Log.d("database", "open cursor :"+ sql);
			c = db.rawQuery(sql, null);
			//Log.i("tip","query from local , sql is : "+ sql);
			while (c.moveToNext())
			{  
				T newt =(T)  itemClass.newInstance();
				DBEntity entity= (DBEntity)newt;
				entity.Parse(c);
				lst.add(newt); 
			}
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		if (c != null && !c.isClosed()) c.close();
		Close();		
		return lst;
	}

	int ExecuteUpdate(java.lang.String table, ContentValues values, String whereClause) throws Exception
	{	
		if (values.size() == 0) return 0;
		int result =-1;
		Open();		
		Log.d("database", "ExecuteUpdate " + table);
		result = db.update(table, values, whereClause, null);	
		if (result > 0)
		{
			SynContent syncontent=new SynContent();
			syncontent.Action = SynContent.Update;
			syncontent.Table = table;
			syncontent.Where = whereClause;
			syncontent.SetFieldCount(values.size());
			int i=0;
			for (String key : values.keySet())
			{
				syncontent.Fields[i] = key;
				syncontent.Values[i] = values.getAsString(key);
				i++;
			}
			RecordUnSync(syncontent);
		}		
		Close();
		return result;
	}

	void ExecuteInsert(java.lang.String table, ContentValues values) throws Exception
	{
		Open();
		Log.d("database", "ExecuteInsert " + table);
		db.insert(table, null, values);
		SynContent syncontent=new SynContent();
		syncontent.Action = SynContent.Add;
		syncontent.Table = table;
		syncontent.SetFieldCount(values.size());
		int i=0;
		for (String key : values.keySet())
		{
			syncontent.Fields[i] = key;
			syncontent.Values[i] = values.getAsString(key);
			i++;
		}
		RecordUnSync(syncontent);		
		Close();
	}


	int ExecuteDelete(java.lang.String table, String whereClause) throws Exception
	{
		Open();
		int result =-1;		
		result = db.delete(table, whereClause, null);
		if (result > 0)
		{
			SynContent syncontent=new SynContent();
			syncontent.Action = SynContent.Delete;
			syncontent.Table = table;
			syncontent.Where = whereClause;
			RecordUnSync(syncontent);
		}
		Close();
		return result;
	}

	private void RecordUnSync(SynContent syncontent)
	{
		ContentValues unSyncValues = new ContentValues();
		unSyncValues.put("SyncTime", Utility.GetNowString("yyyy-MM-dd HH:mm:ss"));
		if (syncontent.Values != null)
		{
			for (int i =0;i < syncontent.Values.length;i++)
			{
				if (syncontent.Values[i] == null || syncontent.Fields[i].toLowerCase().indexOf("is") != 0) continue;
				String val = syncontent.Values[i].toLowerCase();
				if (val.equals("true"))
					syncontent.Values[i] = "1";
				else if (val.equals("false")) 
					syncontent.Values[i] = "0";
			}
		}		
		unSyncValues.put("sql", syncontent.ToJson());
		db.insert("UnSyncRecords", null, unSyncValues);
	}

	public void ExecSynContents(LinkedList<SynContent> contents) throws Exception
	{
		Open();
		for (SynContent syncontent:contents)
		{
			ContentValues values=null;
			if (syncontent.Fields != null)
			{
				values = new ContentValues();
				for (int i=0;i < syncontent.Fields.length;i++)
				{
					values.put(syncontent.Fields[i], syncontent.Values[i]);
				}
			}

			switch (syncontent.Action)
			{
				case SynContent.Add:
					db.insert(syncontent.Table, null, values);
					break;
				case SynContent.Update:
					db.update(syncontent.Table, values, syncontent.Where, null);
					break;
				case SynContent.Delete:
					db.delete(syncontent.Table, syncontent.Where, null);
					break;
			}
		}		
		Close();
	}



	boolean m_trans = false;

	private void Open()
	{
		if (baseAccess != null)
		{
			if (baseAccess.db == null)
			{
				if (db != null) Log.e("invoke error", "baseAccess db is null but current db is not null");
				baseAccess.db = m_dbhelper.getWritableDatabase();	
				Log.d("database", "open writeable db");
				if (this.m_trans)
				{
					baseAccess.db.beginTransaction();
					Log.d("database", "beginTransaction");
					baseAccess.InTrans = true;
				}
				this.db = baseAccess.db; 
				this.m_trans = baseAccess.m_trans;			 
			}
		}
		else
		{
			if (db == null)
			{			  
				db = m_dbhelper.getWritableDatabase();	
				Log.d("database", "open writeable db");
				if (this.m_trans)
				{
					db.beginTransaction();
					Log.d("database", "beginTransaction");
					InTrans = true;
				}
			}
		}

	}

	private void Close()
	{
		if (db == null) return;
		if (this.baseAccess == null)
		{
			if (m_trans) return;
			db.close();
			Log.d("database", "close");
			db = null;
		}
	}

	public void Close(boolean commint)
	{
		if (db == null) return;
		if (this.baseAccess == null || !commint)
		{
			if (m_trans)
			{
				if (commint)
				{
					db.setTransactionSuccessful();
					Log.d("database", "commit transaction");
				}
				else Log.d("database", "rollback transaction");
				db.endTransaction();
				InTrans = false;
				commint = false;
			}
			db.close();
			Log.d("database", "close");
			db = null;
		}
	}


	public Cursor Query(String sql)
	{
		Open();
		//Log.d("database", "open cursor :"+ sql);
		return db.rawQuery(sql, null);
	}

	void ExecuteNonQuery(String sql) throws Exception
	{
		Open();
		//Log.d("database", "exec : "+ sql);
		db.execSQL(sql);		
		Close();
	}

	String ExecuteScalar(String sql) throws Exception
	{
		Open();
		String val = null;
		//Log.d("database", "open cursor :"+ sql);
		Cursor cur = db.rawQuery(sql, null);
		if (cur.moveToNext())
		{
			val = cur.getString(0);
		}
		cur.close();		
		Close();
		return val;
	}

	public String CreateGUID()
	{
		return UUID.randomUUID().toString();
	}
}
