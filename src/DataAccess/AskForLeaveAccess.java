package DataAccess;
import java.util.Calendar;
import java.util.LinkedList;
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.AskForLeave;
import android.content.ContentValues;
import android.content.Context;

public class AskForLeaveAccess extends BasicAccess{
public  AskForLeaveAccess(Context content){
super(content);
}

public LinkedList<AskForLeave> Query(boolean isUnback, String applyDate, int groupID)
{     
    String sql = "select Member.Name MemberName,AskForLeave.* from AskForLeave join Member on Member.ID = AskForLeave.MemberID where member.groupid = " + groupID;
    if (isUnback) sql += " and isback = 0 and Member.Status > -1";
    if (applyDate != null) sql +=  " and (applydate ='"+ applyDate +"' or backdate = '"+applyDate+"') ";
    sql += " order by ApplyDate";
    return super.QueryEntityList(AskForLeave.class, sql);
}
public LinkedList<AskForLeave> QueryOverDayUnBack(String queryDate, int groupID)
{ 
    String sql = "select Member.Name MemberName,AskForLeave.* from AskForLeave join Member on Member.ID = AskForLeave.MemberID where member.groupid = " + groupID;
     sql += " and isback = 0 and Member.Status > -1 and backdate <= '"+queryDate+"'  order by ApplyDate";
    return super.QueryEntityList(AskForLeave.class, sql);
}

public LinkedList<AskForLeave> QueryNext2DayBack(String queryDate, int groupID)
{     
	Calendar cal =  Utility.ConvertToCalendar(queryDate);
	cal.add(Calendar.DATE, 3);
	String next2Day =Utility.CalendarToString(cal);
    String sql = "select Member.Name MemberName,AskForLeave.* from AskForLeave join Member on Member.ID = AskForLeave.MemberID where member.groupid = " + groupID;
     sql += " and isback = 0 and Member.Status > -1 and backdate > '"+queryDate+"' and backdate < '"+next2Day+"'  order by ApplyDate";
    return super.QueryEntityList(AskForLeave.class, sql);
}

public void Add(AskForLeave m_askforleave) throws Exception{
	m_askforleave.ID = CreateGUID();
	ContentValues values=new ContentValues();
	values.put("ID",m_askforleave.ID);
	values.put("MemberID",m_askforleave.MemberID);
	values.put("ApplyDate",m_askforleave.ApplyDate);
	values.put("BackDate",m_askforleave.BackDate);
	values.put("Remark",m_askforleave.Remark);
	values.put("IsBack",m_askforleave.IsBack);
	ExecuteInsert("AskForLeave",values);
	baseAccess.Visit(MemberAccess.class).UpdateStatus(m_askforleave.MemberID,1); 
}

public void Update(AskForLeave m_askforleave) throws Exception{		
	AskForLeave oldVal = this.QueryEntity(AskForLeave.class, "select * from AskForLeave where ID ='"+m_askforleave.ID+"'");
	 ContentValues values=new ContentValues();
	 if(!Utility.StringEquals(oldVal.MemberID,m_askforleave.MemberID))
	     values.put("MemberID",m_askforleave.MemberID);
	 if(!Utility.StringEquals(oldVal.ApplyDate,m_askforleave.ApplyDate))
	     values.put("ApplyDate",m_askforleave.ApplyDate);
	 if(!Utility.StringEquals(oldVal.BackDate,m_askforleave.BackDate))
	     values.put("BackDate",m_askforleave.BackDate);
	 if(!Utility.StringEquals(oldVal.Remark,m_askforleave.Remark))
	     values.put("Remark",m_askforleave.Remark);
	 if(oldVal.IsBack != m_askforleave.IsBack)
	     values.put("IsBack",m_askforleave.IsBack);
	  ExecuteUpdate("AskForLeave",values,"ID='"+m_askforleave.ID+"'");
	if(m_askforleave.IsBack){
		baseAccess.Visit(MemberAccess.class).UpdateStatus(m_askforleave.MemberID,0); 
	}
}
public void Delete(String id) throws Exception{
	ExecuteDelete("AskForLeave","ID='"+ id +"'");
}

}
