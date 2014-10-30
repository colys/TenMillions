package DataAccess; 
import com.colys.tenmillion.MyApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	
	private static final int DATABASE_VERSION = 3;
	
	
	
	public DBHelper(Context context,String path) { 
		//CursorFactory����Ϊnull,ʹ��Ĭ��ֵ
		super(context,path, null, DATABASE_VERSION);
	}

	//��ݿ��һ�α�����ʱonCreate�ᱻ����
	@Override
	public void onCreate(SQLiteDatabase db) {
/*		db.execSQL("CREATE TABLE IF NOT EXISTS person" +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)");
				*/
		
	}
	 
	//���DATABASE_VERSIONֵ����Ϊ2,ϵͳ����������ݿ�汾��ͬ,�������onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*db.execSQL("ALTER TABLE person ADD COLUMN other STRING");*/
	 
	}
}
