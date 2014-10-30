package DataAccess;

import java.util.LinkedList;

import com.colys.tenmillion.Entity.DBEntity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; 

public class EntityDBHelper<T> {
	
	DBHelper dbHelper;
	Class<?> itemClass;
	SQLiteDatabase db;
	
	public EntityDBHelper(DBHelper dbHelper,Class<?> itemClass){
		this.dbHelper= dbHelper;
		this.itemClass = itemClass; 
	}
	
	@SuppressWarnings("unchecked")
	public T QuerySingle(String sql){
		
		 Cursor c =null;
		 T newt = null;
		try {
			newt = (T)  itemClass.newInstance();
			db =dbHelper.getReadableDatabase();
			 c = db.rawQuery(sql,null);
			 if (c.moveToNext()) {  
				
				 DBEntity entity= (DBEntity)newt;
				 entity.Parse(c);
				 
			 }				 
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
			 
		if(c!=null && !c.isClosed()) c.close();
		if(db.isOpen()) db.close();
		return newt;
	}
	
	public int QueryCount(String sql){
		int result =0;
		Cursor c =null;
			db =dbHelper.getReadableDatabase();
			c = db.rawQuery(sql,null);
			if (c.moveToNext()) {  
				result = c.getInt(0);
			}	

		if(c!=null && !c.isClosed()) c.close();
		if(db.isOpen()) db.close();
		return result;
	}
	
	public void ExecuteNoQuery(String sql){ 
		db =dbHelper.getReadableDatabase();
		db.execSQL(sql); 
		db.close();

	}
	
	public void ExecuteNoQuery(String[] sqls){ 
		db =dbHelper.getReadableDatabase();
		for(String sql : sqls) db.execSQL(sql); 
		db.close();

	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<T> QueryList(String sql ){
		
		 LinkedList<T> lst = new LinkedList<T>();
		 Cursor c =null;
		try {			
			 db =dbHelper.getReadableDatabase();
			 c = db.rawQuery(sql,null);
			 //Log.i("tip","query from local , sql is : "+ sql);
			 while (c.moveToNext()) {  
				 T newt =(T)  itemClass.newInstance();
				 DBEntity entity= (DBEntity)newt;
				 entity.Parse(c);
				 lst.add(newt); 
			 }
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if(c!=null && !c.isClosed()) c.close();
		if(db.isOpen()) db.close();
		return lst;
	}
}
