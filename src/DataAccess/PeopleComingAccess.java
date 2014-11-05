package DataAccess;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.House;
import com.colys.tenmillion.Entity.Member;
import com.colys.tenmillion.Entity.PeopleComing;
import com.colys.tenmillion.Entity.PeopleWorking;
import com.colys.tenmillion.Entity.ShenGouRecords;
import com.colys.tenmillion.Entity.Task;
import com.colys.tenmillion.Entity.TrainPlan;

public class PeopleComingAccess extends BasicAccess{
	private   PeopleComingAccess(Context content){
		super(content);
	}

	//查询目前正在看工作和将要走工作的
public 	LinkedList<PeopleComing> GetOnlineOrWillComingList(int groupID,String workDate){
	String sql = "select Member.Name MemberName, PeopleComing.*,PeopleWorking.DayCount,PeopleWorking.Result as DayResult "+
            " from PeopleComing "+
            " left join PeopleWorking on PeopleWorking.ComingID = PeopleComing.ID and PeopleWorking.DayCount =(select max(DayCount) from PeopleWorking where PeopleWorking.ComingID = PeopleComing.ID ) "+
            " join Member on Member.ID = PeopleComing.MemberID "+
		" where Member.groupid=" + groupID + " and  ( PeopleComing.status in (0,1) or (PeopleComing.Status =2 and PeopleComing.leaveDate = '"+workDate+"')) ";
	return super.QueryEntityList(PeopleComing.class, sql);
}

public LinkedList<PeopleComing> GetMonthComingList(int year, int month, String memberFilter, String fixWorkDate, int groupID)
{
	Calendar  c =  Calendar.getInstance();
	c.set(year, month - 1, 1);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	String beginDateStr = sdf.format(c.getTime());
	//c.add(Calendar.MONTH, 1);
    c.roll(Calendar.DATE, -1);
	String endDateStr =  sdf.format(c.getTime());
    String sql = "select Member.Name MemberName, PeopleComing.*,PeopleWorking.DayCount,PeopleWorking.Result as DayResult "+
               " from PeopleComing "+
               " left join PeopleWorking on PeopleWorking.ComingID = PeopleComing.ID and PeopleWorking.DayCount =(select max(DayCount) from PeopleWorking where PeopleWorking.ComingID = PeopleComing.ID ) "+
               " join Member on Member.ID = PeopleComing.MemberID "+
		" where Member.groupid=" + groupID + " and ( (PeopleComing.status > -1 and (PeopleComing.ArriveDate between '" + beginDateStr + "' and '" + endDateStr + "' or PeopleComing.LeaveDate between '" + beginDateStr + "' and '" + endDateStr + "' ) ) or  (PeopleComing.status =1 and PeopleComing.ArriveDate <'" + beginDateStr + "' ) or  (PeopleComing.status = 0 and PeopleComing.ArriveDate > '" + endDateStr + "'  ) )";
    if (fixWorkDate!=null)
    {
        sql += " and (( PeopleComing.status = 1 and arriveDate <= '"+fixWorkDate+"') or (PeopleComing.status > 1 and leaveDate = '"+fixWorkDate+"')) ";
    }
    if (memberFilter!=null) sql += " and MemberID in (" + memberFilter + ")";
    sql += " order by arriveDate";
     
    return super.QueryEntityList(PeopleComing.class, sql);
}


public LinkedList<PeopleComing> GetWorking(String houseId, String fixWorkDate,int groupid)
{
	Calendar workDate = Utility.ConvertToCalendar(fixWorkDate);
	workDate.add(Calendar.DATE, 1);
	String nextDayStr = Utility.CalendarToString(workDate);
	workDate.add(Calendar.DATE, -2);
	String prevDayStr = Utility.CalendarToString(workDate);
    if (houseId == House.Empty_House_Guid || houseId == House.Manager_House_Guid) return null;
     

//    string sql = @"select Member.Name MemberName, PeopleComing.*,PeopleWorking.DayCount,PeopleWorking.Result as DayResult from PeopleComing 
//                   join PeopleWorking on PeopleWorking.ComingID = PeopleComing.ID and date(ArriveDate,'+'||(PeopleWorking.DayCount-1)||' day') = '" + fixWorkDate.ToString("yyyy-MM-dd") + @"'
//                   join Member on Member.ID = PeopleComing.MemberID
//                   where PeopleComing.Status > -1 and PeopleComing.ArriveDate < '" + fixWorkDate.AddDays(1).ToString("yyyy-MM-dd") + "'  and ( PeopleComing.leaveDate is null or PeopleComing.leaveDate > '" + fixWorkDate.AddDays(-1).ToString("yyyy-MM-dd 23:59:00") + "')";
    String sql = "select Member.Name MemberName, PeopleComing.*,PeopleWorking.DayCount,PeopleWorking.Result as DayResult from PeopleComing " +
    		"join PeopleWorking on PeopleWorking.ComingID = PeopleComing.ID and date(ArriveDate ,'+'||(PeopleWorking.DayCount-1)||' day') = '" + fixWorkDate + "' " +
    				"join Member on Member.ID = PeopleComing.MemberID " +
    				"where Member.GroupID ='"+ groupid +"' and PeopleComing.Status > -1 and PeopleComing.ArriveDate < '" + nextDayStr + "'  and ( PeopleComing.leaveDate is null or PeopleComing.leaveDate > '" + prevDayStr + "')";
    if (houseId != null) sql += " and PeopleWorking.HouseID= '" + houseId + "'";           
    sql += " order by arriveDate";
    return super.QueryEntityList(PeopleComing.class, sql);
}

public void FillEmptyDayResult(String fixWorkDate,int groupid) throws Exception
{
	Calendar workDate = Utility.ConvertToCalendar(fixWorkDate);
	workDate.add(Calendar.DATE, 1);
	String nextDayStr = Utility.CalendarToString(workDate);
    String sql = "select PeopleComing.id ComingID,julianday('" + fixWorkDate+ "')- julianday(ArriveDate) +1  dayCount,houseID,'' result " +
    		" from PeopleComing join member on member.id =PeopleComing.memberid " +
    				" where Member.GroupID="+ groupid +
    				" and PeopleComing.status =1 " +
    				"and ArriveDate < '" + nextDayStr + "' " +
    				" and WorkDate is not null " +
    				"and  not exists(" +
    				"	select 1 from PeopleWorking where PeopleWorking.ComingID = PeopleComing.ID and date(ArriveDate,'+'||(DayCount-1)||' days') = '" + fixWorkDate + 
    				"' )";
    LinkedList<PeopleWorking> lst =QueryEntityList(PeopleWorking.class,sql);
    for(PeopleWorking work :lst){
    	String HouseIDS = super.baseAccess. Visit(PeopleWorkingAccess.class).ExecuteScalar("select HouseID from PeopleWorking where ComingID='"+ work.ComingID+"' order by DayCount desc limit 1");
    	if(HouseIDS!=null) work.HouseID = HouseIDS;
    	super.baseAccess. Visit(PeopleWorkingAccess.class).Add(work);
    }
}

public int UpdateComing(PeopleComing coming,int groupid) throws Exception
{
	 
	 if (coming.Status > 1)
     {
         if (coming.WorkDate == null) throw new Exception("people is leave must be set work data");
     }
     if (coming.Status == 2)
     {
         if (coming.Name==null || coming.Name.isEmpty()) throw new Exception("people is join , must be set a name");
         if (coming.MemberID == null) throw new Exception("people is join , must be set a refrence man");
     }
	if (coming.Status > -1){
		Update(coming);
	}else{ 
		 ContentValues values=new ContentValues();
		 values.put("ID",coming.ID);	
		 values.put("Status",-1);
		 ExecuteUpdate("PeopleComing",values,"ID='"+coming.ID+"'");
	}
	
    
       if (coming.Status == 2)
       {
    	   if (coming.LeaveDate == null) throw new Exception("people is success or leave , must be set leave data");
           if (coming.SGFenE > 0)
           {
               Member newMember = new Member();
               newMember.Name = coming.Name;
               newMember.ReferenceID = coming.MemberID;
               newMember.XValue = 0;//����깺��¼��ʱ����޸�
               newMember.JoinDate = coming.LeaveDate;
               newMember.GroupID = groupid;
               if(newMember.PinYinJ ==null || newMember.PinYinJ.isEmpty()){
            	   newMember.PinYinJ = Utility.GetPinYin(newMember.Name);
               }
               baseAccess.Visit(MemberAccess.class).Add(newMember);
               //��ӵ���¼��
               ShenGouRecords shenGou =new ShenGouRecords();
               shenGou.ApplyDate = Utility.GetNowString();
    		   shenGou.SGFenE = coming.SGFenE;
			   shenGou.MemberID = newMember.ID;
			   shenGou.IsPatch = false;
			   baseAccess.Visit(ShenGouRecordsAccess.class).Add(shenGou );
           }
          
       }
       if(coming.Status ==3){
    	   //��Ӻ��ڸ��������
    	   Task task = new Task();
    	   Calendar cal = Utility.ConvertToCalendar(coming.LeaveDate);
    	   cal.add(Calendar.DATE, 3);    	   
    	   task.BeginDate = Utility.CalendarToString(cal);
    	   task.IsFinish = false;
    	   task.GroupID =groupid; 
    	   task.Text = "后期跟进："+  coming.MemberName +  coming.Relation +"("+  coming.ComeFrom +"),"+coming.LeaveDate.substring(8)+"号回";
    	   baseAccess.Visit(TaskAccess.class).Add(task );
       }else if(coming.Status==4){
		   Task task = new Task();
    	   Calendar cal = Utility.ConvertToCalendar(coming.LeaveDate);
    	   cal.add(Calendar.DATE, 1);    	   
    	   task.BeginDate = Utility.CalendarToString(cal);
    	   task.IsFinish = false;
    	   task.GroupID =groupid; 
    	   task.Text = "会后："+  coming.MemberName +  coming.Relation +"("+  coming.ComeFrom +")";
    	   baseAccess.Visit(TaskAccess.class).Add(task );
	   }
      
    return 1;
}
public PeopleComing Get(String id) throws Exception{
	return this.QueryEntity(PeopleComing.class, "select PeopleComing.*,Member.Name MemberName from PeopleComing Join Member on Member.ID =PeopleComing.MemberID  where PeopleComing.ID ='"+id+"'");
}


public void Add(PeopleComing m_peoplecoming) throws Exception{
m_peoplecoming.ID = CreateGUID();
ContentValues values=new ContentValues();
values.put("ID",m_peoplecoming.ID);
values.put("MemberID",m_peoplecoming.MemberID);
values.put("ArriveDate",m_peoplecoming.ArriveDate);
values.put("WorkDate",m_peoplecoming.WorkDate);
values.put("LeaveDate",m_peoplecoming.LeaveDate);
values.put("Name",m_peoplecoming.Name);
values.put("ComeFrom",m_peoplecoming.ComeFrom);
values.put("Relation",m_peoplecoming.Relation);
values.put("Status",m_peoplecoming.Status);
values.put("SGFenE",m_peoplecoming.SGFenE);
values.put("Job",m_peoplecoming.Job);
values.put("HouseID",m_peoplecoming.HouseID);
values.put("Remark",m_peoplecoming.Remark);
ExecuteInsert("PeopleComing",values);
}
public void Update(PeopleComing m_peoplecoming) throws Exception{
	
	PeopleComing oldVal = this.QueryEntity(PeopleComing.class, "select * from PeopleComing where ID ='"+m_peoplecoming.ID+"'");
	 ContentValues values=new ContentValues();
	 if(!Utility.StringEquals(oldVal.MemberID,m_peoplecoming.MemberID))
	     values.put("MemberID",m_peoplecoming.MemberID);
	 if(!Utility.StringEquals(oldVal.ArriveDate,m_peoplecoming.ArriveDate))
	     values.put("ArriveDate",m_peoplecoming.ArriveDate);
	 if(!Utility.StringEquals(oldVal.WorkDate,m_peoplecoming.WorkDate))
	     values.put("WorkDate",m_peoplecoming.WorkDate);
	 if(!Utility.StringEquals(oldVal.LeaveDate,m_peoplecoming.LeaveDate))
	     values.put("LeaveDate",m_peoplecoming.LeaveDate);
	 if(!Utility.StringEquals(oldVal.Name,m_peoplecoming.Name))
	     values.put("Name",m_peoplecoming.Name);
	 if(!Utility.StringEquals(oldVal.ComeFrom,m_peoplecoming.ComeFrom))
	     values.put("ComeFrom",m_peoplecoming.ComeFrom);
	 if(!Utility.StringEquals(oldVal.Relation,m_peoplecoming.Relation))
	     values.put("Relation",m_peoplecoming.Relation);
	 if(oldVal.Status != m_peoplecoming.Status)
	     values.put("Status",m_peoplecoming.Status);
	 if(oldVal.SGFenE != m_peoplecoming.SGFenE)
	     values.put("SGFenE",m_peoplecoming.SGFenE);
	 if(!Utility.StringEquals(oldVal.Job,m_peoplecoming.Job))
	     values.put("Job",m_peoplecoming.Job);
	 if(!Utility.StringEquals(oldVal.HouseID,m_peoplecoming.HouseID))
	     values.put("HouseID",m_peoplecoming.HouseID);
	 if(!Utility.StringEquals(oldVal.Remark,m_peoplecoming.Remark))
	     values.put("Remark",m_peoplecoming.Remark);
	  ExecuteUpdate("PeopleComing",values,"ID='"+m_peoplecoming.ID+"'");
}
public void Delete(String id) throws Exception{
	String	sql = "select count(0) from PeopleWorking where ComingID = '" + id + "' order by dayCount";
	if( ExecuteScalar(sql).equals("0")){
		ExecuteDelete("PeopleComing","ID='"+ id +"'");
	}
	else throw new Exception("有工作记录，不能删除！");

}

}
