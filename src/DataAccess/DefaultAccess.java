package DataAccess;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context; 

public class DefaultAccess extends BasicAccess {
	public DefaultAccess(Context context) {
		super(context); 
	}

	public void ExecuteNonQuery(String sql) throws Exception{
		super.ExecuteNonQuery(sql);
	}
	
	public String ExecuteScalar(String sql) throws Exception{
		return super.ExecuteScalar(sql);
	}
	
	public <T> T QueryEntity(Class<T> itemClass,String sql) throws Exception{
		return super.QueryEntity(itemClass, sql);
	}
	
	public <T> LinkedList<T> QueryEntityList(Class<T> itemClass,String sql){
		return super.QueryEntityList(itemClass, sql);
	}
	
	public void ExecuteInsert(java.lang.String table, ContentValues values) throws Exception {
		super.ExecuteInsert(table, values);
	}
}
