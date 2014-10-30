package DataAccess;

import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.TrainItem;

public class TrainItemAccess extends BasicAccess{
public  TrainItemAccess(Context content){
super(content);
}

public LinkedList<TrainItem> QueryALL(){
	return this.QueryEntityList(TrainItem.class, "select * from TrainItems ");
}

public TrainItem Get(int id) throws Exception{
	return this.QueryEntity(TrainItem.class, "select * from TrainItems  where ID ='"+id+"'");
}

public void Add(TrainItem m_trainitem) throws Exception {
ContentValues values=new ContentValues();
if(m_trainitem.Name != null)    values.put("Name",m_trainitem.Name);
ExecuteInsert("TrainItems",values);
}
public void Update(TrainItem m_trainitem) throws Exception {
TrainItem oldVal = this.QueryEntity(TrainItem.class, "select * from TrainItems where ID ='"+m_trainitem.ID+"'");
ContentValues values=new ContentValues();
if(!Utility.StringEquals(oldVal.Name,m_trainitem.Name))
 values.put("Name",m_trainitem.Name);
ExecuteUpdate("TrainItems",values,"ID='"+m_trainitem.ID+"'");
}
public void Delete(String id) throws Exception {
  ExecuteDelete("TrainItems","ID='"+ id +"'");
}
}
