package DataAccess;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.DayWorkHouse;

import android.content.ContentValues;
import android.content.Context;

public class DayWorkHouseAccess extends BasicAccess{
	public  DayWorkHouseAccess(Context content){
	super(content);
	}
	
	public void Add(DayWorkHouse m_dayworkhouse) throws Exception {
	ContentValues values=new ContentValues();
	m_dayworkhouse.ID=CreateGUID();
	values.put("ID",m_dayworkhouse.ID);
	values.put("HouseID",m_dayworkhouse.HouseID);
	values.put("GroupID",m_dayworkhouse.GroupID);
	if(m_dayworkhouse.Remark != null)    values.put("Remark",m_dayworkhouse.Remark);
	if(m_dayworkhouse.WorkDay != null)    values.put("WorkDay",m_dayworkhouse.WorkDay);
	ExecuteInsert("DayWorkHouse",values);
	}
	public void Update(DayWorkHouse m_dayworkhouse) throws Exception {
	DayWorkHouse oldVal = this.QueryEntity(DayWorkHouse.class, "select * from DayWorkHouse where ID ='"+m_dayworkhouse.ID+"'");
	ContentValues values=new ContentValues();
	if(!Utility.StringEquals(oldVal.HouseID,m_dayworkhouse.HouseID))
	 values.put("HouseID",m_dayworkhouse.HouseID);
	if(oldVal.GroupID != m_dayworkhouse.GroupID)
	 values.put("GroupID",m_dayworkhouse.GroupID);
	if(!Utility.StringEquals(oldVal.Remark,m_dayworkhouse.Remark))
	 values.put("Remark",m_dayworkhouse.Remark);
	if(!Utility.StringEquals(oldVal.WorkDay,m_dayworkhouse.WorkDay))
	 values.put("WorkDay",m_dayworkhouse.WorkDay);
	ExecuteUpdate("DayWorkHouse",values,"id='"+m_dayworkhouse.ID+"'");
	}
	public void Delete(String id) throws Exception {
	  ExecuteDelete("DayWorkHouse","id='"+id+"'");
	}
}
