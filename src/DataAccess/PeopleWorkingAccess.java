package DataAccess;
 
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.PeopleWorking;

import android.content.ContentValues;
import android.content.Context;


public class PeopleWorkingAccess extends BasicAccess{
	private  PeopleWorkingAccess(Context content){
    super(content);
}

public void Add(PeopleWorking m_peopleworking) throws Exception{
    m_peopleworking.ID = CreateGUID();
    ContentValues values=new ContentValues();
    values.put("ID",m_peopleworking.ID);
    values.put("DayCount",m_peopleworking.DayCount);
    values.put("Result",m_peopleworking.Result);
    values.put("ComingID",m_peopleworking.ComingID);
    values.put("HouseID",m_peopleworking.HouseID);
    ExecuteInsert("PeopleWorking",values);
}

public void UpdateHouseByDate(String comingId, String date,String houseID) throws Exception{
	String sql ="select PeopleWorking.* from PeopleWorking "
			+ "join PeopleComing on  PeopleComing.ID = PeopleWorking.ComingID "
			+ "where PeopleComing.ID ='"+ comingId +"' and DayCount = (julianday(datetime('"+ date +"'))-julianday(datetime(ArriveDate))+1)  ";
	PeopleWorking pw = QueryEntity(PeopleWorking.class, sql);
	ContentValues values=new ContentValues();
	values.put("HouseID",houseID);
	int result = ExecuteUpdate("PeopleWorking",values,"ID='"+pw.ID+"'");
	 if(result == 0){
		 sql ="select max(DayCount) from PeopleWorking where ComingID='"+comingId +"'";
		 String strDayCount = ExecuteScalar(sql);
		 int dayCount = 1;
		 if(strDayCount!=null) dayCount = Integer.parseInt(strDayCount)+1;
		 //insert peoplecoming work
		pw = new PeopleWorking();
		pw.ComingID = comingId;
		pw.DayCount = dayCount;
		pw.HouseID = houseID;
		Add(pw);
	 }
	
}
public void Update(PeopleWorking m_peopleworking) throws Exception{
	 PeopleWorking oldVal = this.QueryEntity(PeopleWorking.class, "select * from PeopleWorking where ID ='"+m_peopleworking.ID+"'");
	 ContentValues values=new ContentValues();
	 if(oldVal.DayCount != m_peopleworking.DayCount)
	     values.put("DayCount",m_peopleworking.DayCount);
	 if(!Utility.StringEquals(oldVal.Result,m_peopleworking.Result))
	     values.put("Result",m_peopleworking.Result);
	 if(!Utility.StringEquals(oldVal.ComingID,m_peopleworking.ComingID))
	     values.put("ComingID",m_peopleworking.ComingID);
	 if(!Utility.StringEquals(oldVal.HouseID,m_peopleworking.HouseID))
	     values.put("HouseID",m_peopleworking.HouseID);
	  ExecuteUpdate("PeopleWorking",values,"ID='"+m_peopleworking.ID+"'");
}
public void Delete(String id) throws Exception{
      ExecuteDelete("PeopleWorking","ID='"+ id +"'");
}

}
