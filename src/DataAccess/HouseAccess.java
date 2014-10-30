package DataAccess;

import java.util.LinkedList;

import com.colys.tenmillion.Entity.House;

import android.content.ContentValues;
import android.content.Context;


public class HouseAccess extends BasicAccess{
private  HouseAccess(Context content){
super(content);
}

public House Get(String ID) throws Exception{
	return super.QueryEntity(House.class,  "select * from House where ID ='"+ ID +"'");
}

public LinkedList<House> Query(int[] groupIDArray)
{ 
	if(groupIDArray.length ==0) return null;
    String sql = "select * from House  where  groupid in("+groupIDArray[0];
    for(int i=1;i< groupIDArray.length;i++){
    	sql+=","+groupIDArray[i];
    }
    sql += ") order by OwnDate";
    return super.QueryEntityList(House.class, sql);
}

public LinkedList<House> Query(boolean showTemp, int groupID)
{ 
    String sql = "select * from House where  groupid in (0,"+ groupID+")";
    if (!showTemp) sql += " and ID <> '"+ House.Empty_House_Guid +"'";
    else sql += " or ID = '"+ House.Empty_House_Guid +"'";
    sql += " order by OwnDate";
    return super.QueryEntityList(House.class, sql);
}

public void Add(House m_house) throws Exception{
m_house.ID = CreateGUID();
ContentValues values=new ContentValues();
values.put("ID",m_house.ID);
values.put("Name",m_house.Name);
values.put("Address",m_house.Address);
values.put("Owner",m_house.Owner);
values.put("Price",m_house.Price);
values.put("OwnDate",m_house.OwnDate);
values.put("GroupID",m_house.GroupID);
ExecuteInsert("House",values);
}
public void Update(House m_house) throws Exception{		
ContentValues values=new ContentValues();
values.put("ID",m_house.ID);
values.put("Name",m_house.Name);
values.put("Address",m_house.Address);
values.put("Owner",m_house.Owner);
values.put("Price",m_house.Price);
values.put("OwnDate",m_house.OwnDate);
values.put("GroupID",m_house.GroupID);
ExecuteUpdate("House",values,"ID='"+m_house.ID+"'");
}

public void Delete(String id) throws Exception{
	String	sql = "select count(0) from DayWorkHouse where HouseID = '" + id + "'";
	if(! ExecuteScalar(sql).equals("0")){
		throw new Exception("房子已经在使用，不能删除！");
	}
	ExecuteDelete("House","ID='"+ id +"'");
}
}
