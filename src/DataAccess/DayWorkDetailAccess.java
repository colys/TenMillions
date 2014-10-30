package DataAccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.DayWorkDetail;
import com.colys.tenmillion.Entity.House;
import com.colys.tenmillion.Entity.DayWorkHouse; 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.*;


public class DayWorkDetailAccess extends BasicAccess
{
	public  DayWorkDetailAccess(Context content)
	{
		super(content);

	}
//	
//	@SuppressLint("SimpleDateFormat")
//	public LinkedList<DayWorkHouse> QueryByDay(Calendar queryDay ){
//		LinkedList<DayWorkHouse> lst =new LinkedList<DayWorkHouse>();
//		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
//		String queryDateStr = sdf.format(queryDay.getTime());
//		queryDay.add(Calendar.DATE, 1);
//		String nextDayStr = sdf.format(queryDay.getTime());
//		queryDay.add(Calendar.DATE, -2);
//		String yesterDayStr = sdf.format(queryDay.getTime());
//		String sql="select * from House Order by OwnDate";
//		EntityDBHelper<House> helper =new EntityDBHelper<House>(super.m_dbhelper, House.class);
//		LinkedList<House> houseList = helper.QueryList(sql);
//		for (House house :houseList)
//		{
//			DayWorkHouse item = new DayWorkHouse();
//			item.House = house;
//			sql = "select Member.Name MemberName,DayWorkDetail.* from DayWorkDetail join Member on Member.ID = DayWorkDetail.MemberID " +
//				"where houseid = '" + house.ID + "' and WorkDay ='" + queryDateStr + "'";
//			EntityDBHelper<DayWorkDetail> dayWorkHelper =new EntityDBHelper<DayWorkDetail>(super.m_dbhelper, DayWorkDetail.class);
//			item.Works = dayWorkHelper.QueryList(sql);
//			if (!house.ID.equals(House.Empty_House_Guid ))
//			{
//				sql = "select Member.Name MemberName, PeopleComing.*,PeopleWorking.DayCount,PeopleWorking.Result as DayResult from PeopleComing " +
//                    "join PeopleWorking on PeopleWorking.ComingID = PeopleComing.ID and date(ArriveDate ,'+'||(PeopleWorking.DayCount-1)||' day') = '" + queryDateStr + "'" +
//                    "join Member on Member.ID = PeopleComing.MemberID " +
//                    "where PeopleComing.Status > -1 " +
//					"and PeopleComing.ArriveDate < '" + nextDayStr + "'  and ( PeopleComing.leaveDate is null or PeopleComing.leaveDate > '" + yesterDayStr + "') " +
//					"and PeopleWorking.HouseID= '" + house.ID + "' " +
//					"order by arriveDate";
//				EntityDBHelper<PeopleComing> comingHelper =new EntityDBHelper<PeopleComing>(super.m_dbhelper, PeopleComing.class);
//				item.PeopleComingList = comingHelper.QueryList(sql);
//			}
//			lst.add(item);
//		}	
//		return lst;
//	}




	public LinkedList<DayWorkHouse> QueryHouseWorks(String date, int groupID) throws Exception
	{		 

		LinkedList<DayWorkHouse> lst = FillHouseWorks(date, groupID);

		return lst;
	}

	public LinkedList<DayWorkHouse> InitDayHouseWorks(String date, int groupid) throws Exception
	{  
		LinkedList<DayWorkHouse> hdwList = null; 
		String cc=  super.ExecuteScalar("select count(0) from DayWorkHouse where GroupID ="+ groupid +" and workDay ='" + date + "' and exists(select 1 from DayWorkDetail where DayWorkDetail.WDHID= DayWorkHouse.ID)");
		if (cc == null ||  Integer.valueOf(cc) == 0)
		{
			if (!Exist3DayAgoAndFill(date,groupid))
			{
				FillWorkFromMember(date,groupid);
			}
			baseAccess.Visit(PeopleComingAccess.class).FillEmptyDayResult(date,groupid);
			   //delte empty house record
            String sql = "select  id from DayWorkHouse where GroupID ="+ groupid +" and workday ='"+ date +"' and not exists (select 1 from DayWorkDetail where WDHID =DayWorkHouse.ID)";
            Cursor cur = Query(sql);
            while (cur.moveToNext())
			{    			            	  
  				String id =  cur.getString(0);
  				super.ExecuteDelete("DayWorkHouse", "ID='" + id + "'"); 
            }
            cur.close();
		}
		hdwList = FillHouseWorks(date, groupid); 

		return hdwList;
	}

	public String GetLastDate(int groupId) throws Exception
	{
		return ExecuteScalar("select max(workday) from DayWorkHouse where groupid="+groupId);
	}

	public void ReCalcOnline(String date,int groupid) throws Exception
	{
		DayWorkHouse emptyDWH = GetOrCreateDayWorkHouse(House.Empty_House_Guid,date,groupid);

		Calendar workDate = Utility.ConvertToCalendar(date);
		workDate.add(Calendar.DATE, 1);
		String nextDayStr = Utility.CalendarToString(workDate);
		//workDate.add(Calendar.DATE, -2);
		//String prevDayStr = Utility.CalendarToString(workDate);
		//请假的
		Cursor cur = null;	
			String sql = "select DayWorkDetail.ID from DayWorkDetail join DayWorkHouse on DayWorkHouse.ID = DayWorkDetail.WDHID where GroupID='" + groupid + "' and workday='" + date + "' and exists ( " +
    	  		"select 1 from AskForLeave where  memberid = DayWorkDetail.memberid and ApplyDate < '" + nextDayStr + "' and isback = 0 " +
				"  )";
			cur = Query(sql);
			while (cur.moveToNext())
			{
				String id =cur.getString(0);
				ExecuteDelete("DayWorkDetail", "ID='" + id + "'");
			}
			cur.close();
			//虚掉的
			if(Utility.getDateDays(Calendar.getInstance(), workDate) < 3){
				sql = "select  DayWorkDetail.ID from DayWorkDetail join DayWorkHouse on DayWorkHouse.ID = DayWorkDetail.WDHID join Member on Member.ID = DayWorkDetail.MemberID where workday='" + date + "'  and DayWorkHouse.GroupID='" + groupid + "' and status < 0 " ;
				cur = Query(sql);
				while (cur.moveToNext())
				{
					String id =cur.getString(0);
					ExecuteDelete("DayWorkDetail", "ID='" + id + "'");
				}
				cur.close();
			
			//归队或者没有初始化的
			sql = " select  id from member where GroupID='" + groupid + "' and status =0   and joindate < '" + date + "'" +
					" and not exists (select 1 from  DayWorkDetail join DayWorkHouse on DayWorkHouse.ID = DayWorkDetail.WDHID where workday = '" + date + "'  and memberid = member.id) " +
				" union " +
				"select  memberid from AskForLeave where ApplyDate > '" + date + "' and BackDate <'" + nextDayStr + "'  and not exists(select 1 from DayWorkDetail join DayWorkHouse on DayWorkHouse.ID = DayWorkDetail.WDHID where DayWorkDetail.memberid = AskForLeave.memberid and workDay = '" + date + "')";
			cur = Query(sql);
			while (cur.moveToNext())
			{    			            	  
				ContentValues values=new ContentValues();				
				values.put("WDHID", emptyDWH.ID);
				values.put("MemberID", cur.getString(0)); 
				values.put("ID", super.CreateGUID()); 
				ExecuteInsert("DayWorkDetail", values); 
			}
			cur.close(); 
			}
			//走工作的记录
			sql = "select PeopleComing.id,julianday('" + date + "')- julianday(ArriveDate) +1 dayCount  from PeopleComing join Member on Member.ID = PeopleComing.MemberID " +
           		"  where Member.GroupID =" + groupid + " and PeopleComing.status =1 and ArriveDate < '" + nextDayStr + "' and WorkDate is not null and  not exists(select 1 from PeopleWorking where PeopleWorking.ComingID = PeopleComing.ID and date(ArriveDate ,'+'||(DayCount-1)||' day') = '" + date + "' )";
			cur = Query(sql);
			while (cur.moveToNext())
			{    			            	  
				ContentValues values=new ContentValues();
				values.put("ComingID", cur.getString(0));
				values.put("dayCount", cur.getString(1));
				values.put("WDHID", emptyDWH.ID); 
				values.put("result", ""); 
				values.put("ID", super.CreateGUID()); 
				ExecuteInsert("PeopleWorking", values);
			}
			cur.close();
			//delete copy
			sql = "select  min(DayWorkDetail.id) id from DayWorkDetail where exists (select 1 from DayWorkHouse where DayWorkHouse.ID = DayWorkDetail.WDHID and workday='" + date + "') group by memberid having count(0) > 1";
            cur = Query(sql);
            while (cur.moveToNext())
			{    			            	  
  				String id =  cur.getString(0);
  				super.ExecuteDelete("DayWorkDetail", "ID='" + id + "'"); 
            }
            cur.close();
            //delte empty house record
            sql = "select  id from DayWorkHouse where GroupID ="+ groupid +" and workday ='"+ date +"' and not exists (select 1 from DayWorkDetail where WDHID =DayWorkHouse.ID)";
            cur = Query(sql);
            while (cur.moveToNext())
			{    			            	  
  				String id =  cur.getString(0);
  				super.ExecuteDelete("DayWorkHouse", "ID='" + id + "'"); 
            }
            cur.close();
	}

	private LinkedList<DayWorkHouse> FillHouseWorks(String date, int groupID) throws Exception
	{    	  

		LinkedList<DayWorkHouse> hdwList = new LinkedList<DayWorkHouse>();
		String sql = "select DayWorkHouse.*,House.Name HouseName from DayWorkHouse join House on House.ID =DayWorkHouse.HouseID  where  DayWorkHouse.groupid = "+ groupID +" and workday ='"+date+"' order by OwnDate";
		LinkedList<DayWorkHouse> houseList = QueryEntityList(DayWorkHouse.class, sql);
	
		for (DayWorkHouse item:houseList)
		{  
			QueryHouseInfo(item,groupID);
			hdwList.add(item);
		}
		return hdwList;
	}
	
	public void QueryHouseInfo(DayWorkHouse item,int groupid) throws Exception{
		
		item.Works = QueryDetail(item);
		if (item.HouseID != House.Empty_House_Guid)
		{
			item.PeopleComingList = baseAccess.Visit(PeopleComingAccess.class).GetWorking(item.HouseID, item.WorkDay,groupid);
		}
	}

	public DayWorkHouse GetOrCreateDayWorkHouse(String houseID,String date,int groupid) throws Exception{
		String sql ="select * from DayWorkHouse where HouseID ='"+houseID+"' and groupid="+groupid+" and WorkDay ='"+date+"'";
		DayWorkHouse item = QueryEntity(DayWorkHouse.class, sql);
		if(item==null){
			Log.i("tip","GetOrCreateDayWorkHouse create new ");
			item=new DayWorkHouse();
			item.HouseID = houseID;
			item.GroupID = groupid;
			item.WorkDay = date;
			baseAccess.Visit(DayWorkHouseAccess.class).Add(item);
		}
		else 
			Log.i("tip","GetOrCreateDayWorkHouse found :"+item.ID);
		return item;
	}

	public DayWorkHouse[] ChangeHouse(String memberId, String oldHourse, String newHourse, String date,int groupid) throws Exception
	{
		
		DayWorkHouse newDWH = GetOrCreateDayWorkHouse(newHourse,date,groupid);
		String sql ="select DayWorkDetail.ID from DayWorkDetail join DayWorkHouse on DayWorkDetail.WDHID = DayWorkHouse.ID where DayWorkDetail.MemberID ='"+memberId+"' and  HouseID ='"+oldHourse+"' and WorkDay ='"+date+"'";
		String oldDetailID = ExecuteScalar(sql);
		ContentValues values=new ContentValues();
		values.put("WDHID", newDWH.ID);
		ExecuteUpdate("DayWorkDetail", values, "ID ='" + oldDetailID + "'");
		Log.i("tip",oldDetailID+" change wdhid to "+newDWH.ID);
		DayWorkHouse[] arr = new DayWorkHouse[2];
		String[] houseIDArr = new String[] { newHourse, oldHourse };
		for (int i =0;i < houseIDArr.length ;i++)
		{			
			arr[i] = new DayWorkHouse();
			arr[i].HouseID = houseIDArr[i];
			arr[i].House = baseAccess.Visit(HouseAccess.class).Get(arr[i].HouseID);
			arr[i].Works = Query(arr[i].HouseID, date,groupid);
			arr[i].PeopleComingList = baseAccess.Visit(PeopleComingAccess.class).GetWorking(arr[i].HouseID, date,groupid);
		}

		return arr;
	}

	private LinkedList<DayWorkDetail> Query(String houseID, String date,int groupid) throws Exception
	{
		//昨天
		Calendar workDate = Utility.ConvertToCalendar(date);
		workDate.add(Calendar.DATE, -1);
	  	String yestDayStr = Utility.CalendarToString(workDate);
		String sql = "select DayWorkHouse.HouseID, Member.Name MemberName,DayWorkDetail.*,( select houseid from DayWorkHouse yestdayHouse " +
				"join DayWorkDetail yestdayWork on yestdayHouse.ID =yestdayWork.WDHID " +
				"where yestdayWork.memberid=DayWorkDetail.memberid and yestdayHouse.WorkDay = '"+yestDayStr+"' ) yestdayHouse " +
			"from DayWorkHouse join DayWorkDetail on DayWorkHouse.ID =DayWorkDetail. WDHID " +
			"join Member on Member.ID = DayWorkDetail.MemberID " +
			"where DayWorkHouse.GroupID ='" + groupid + "' and DayWorkHouse.houseid = '" + houseID + "' and DayWorkHouse.WorkDay ='" + date + "'";
		return QueryEntityList(DayWorkDetail.class, sql);
	}

	private LinkedList<DayWorkDetail> QueryDetail(DayWorkHouse item) throws Exception
	{
		//昨天
		Calendar workDate = Utility.ConvertToCalendar(item.WorkDay);
		workDate.add(Calendar.DATE, -1);
	  	String yestDayStr = Utility.CalendarToString(workDate);
		String sql = "select '"+ item.HouseID +"' HouseID, Member.Name MemberName,DayWorkDetail.*,( select houseid from DayWorkHouse yestdayHouse " +
				"join DayWorkDetail yestdayWork on yestdayHouse.ID =yestdayWork.WDHID " +
				"where yestdayWork.memberid=DayWorkDetail.memberid and yestdayHouse.WorkDay = '"+yestDayStr+"' ) yestdayHouse " +
			"from DayWorkDetail join Member on Member.ID = DayWorkDetail.MemberID " +
			"where DayWorkDetail.WDHID ='" + item.ID + "'";
		return QueryEntityList(DayWorkDetail.class, sql);
	}


	public boolean Exist3DayAgoAndFill(String date,int groupid) throws Exception
	{
		Calendar workDate = Utility.ConvertToCalendar(date);
		workDate.add(Calendar.DATE, -4);
		String prev3DayStr = Utility.CalendarToString(workDate);
		String sql = "select max(WorkDay) from DayWorkHouse  where GroupID='" + groupid + "' and  WorkDay between '" + prev3DayStr + "' and  '" + date + "' and exists(select 1 from DayWorkDetail where DayWorkDetail.WDHID =DayWorkHouse.ID)";
		String val = ExecuteScalar(sql);
		if (val != null && !val.isEmpty())
		{
			Log.i("tip","fill from "+val);
			//fill house
			sql = "select DayWorkHouse.ID,DayWorkHouse.HouseID from DayWorkHouse join House on House.ID =DayWorkHouse.HouseID  where WorkDay='"+ val +"' and DayWorkHouse.groupid="+groupid+" and House.GroupID in (0,"+ groupid+")";
			//ArrayList<String> houseList = new ArrayList<String>();
			LinkedList<DayWorkHouse> prevDWHList = new LinkedList<DayWorkHouse>();
			LinkedList<DayWorkHouse> newDWHList = new LinkedList<DayWorkHouse>();
			//ArrayList<String> houseIDList = new ArrayList<String>();
			prevDWHList = baseAccess.QueryEntityList(DayWorkHouse.class, sql);			
			if(prevDWHList.size()==0) {
				Log.i("tip","no found date");
				return false;
			}			
			for(DayWorkHouse prevDWH : prevDWHList){
				//house
				DayWorkHouse todayDWH= GetOrCreateDayWorkHouse(prevDWH.HouseID, date,groupid);
				//file detail
				sql = " select MemberID ,DaiGZ ,Remark from DayWorkDetail join Member on Member.ID=DayWorkDetail.MemberID where Member.GroupID="+ groupid+" and WDHID='"+prevDWH.ID+"'";	
				Cursor cur = Query(sql);
				while (cur.moveToNext())
				{
					ContentValues values =new ContentValues();
					values.put("ID", CreateGUID());
					values.put("WDHID", todayDWH.ID);
					//values.put("WorkDay", date);					
					values.put("MemberID", cur.getString(0));
					String dgz= cur.getString(1);
					values.put("DaiGZ", dgz);
					if(dgz!=null && dgz.isEmpty()) values.put("Remark", cur.getString(2));
					ExecuteInsert("DayWorkDetail", values);
				}
				cur.close();
			}
			return true;
		}
		else return false;
	}

	public void FillWorkFromMember(String date,int groupid) throws Exception
	{
		Log.i("tip","fill from member");
		ContentValues values=new ContentValues();
		String DWHID= CreateGUID();
		values.put("ID",DWHID );
		values.put("GroupID", groupid);
		values.put("WorkDay", date);
		values.put("HouseID", House.Empty_House_Guid);
		ExecuteInsert("DayWorkHouse", values);
		//������λ�� union ������ٵĺͽ����ӵ�
		String sql = " select  id from Member where GroupID ='" + groupid + "' and status =0 " +
			" union " +
			"select memberid from AskForLeave join Member on Member.ID = AskForLeave.MemberID  where GroupID ='" + groupid + "' and ( (isback =0 and ApplyDate > '" + date + "') or (isback =1 and BackDate = '" + date + "'))";

		Cursor cur = Query(sql);
		while (cur.moveToNext())
		{
			values.clear();
			values.put("WDHID", DWHID);
			values.put("MemberID", cur.getString(0));  
			values.put("ID", CreateGUID());
			ExecuteInsert("DayWorkDetail", values);
		}
		cur.close(); 
	}
	
	public DayWorkDetail Get(String workDay,String memberid) throws Exception
	{
		String sql ="select DayWorkDetail.*,Member.Name membername from DayWorkDetail join Member on Member.ID = DayWorkDetail.MemberID join DayWorkHouse on DayWorkHouse.ID =DayWorkDetail.WDHID where workDay='"+ workDay +"' and MemberID ='"+ memberid +"' ";
		return QueryEntity(DayWorkDetail.class, sql);
	}

	public void Add(DayWorkDetail m_DayWorkDetail) throws Exception
	{
		m_DayWorkDetail.ID = CreateGUID();
		ContentValues values=new ContentValues();
		values.put("WDHID", m_DayWorkDetail.WDHID);
		values.put("MemberID", m_DayWorkDetail.MemberID);
		values.put("ZhengBang", m_DayWorkDetail.ZhengBang);
		values.put("BaiFang", m_DayWorkDetail.BaiFang);
		values.put("GenJin", m_DayWorkDetail.GenJin);
		values.put("PuDian", m_DayWorkDetail.PuDian);
		values.put("PeiXun", m_DayWorkDetail.PeiXun);
		values.put("Remark", m_DayWorkDetail.Remark);
		values.put("ID", m_DayWorkDetail.ID);
		values.put("DaiGZ", m_DayWorkDetail.DaiGZ);
		ExecuteInsert("DayWorkDetail", values);
	}

	public void Update(DayWorkDetail m_DayWorkDetail) throws Exception
	{		
		DayWorkDetail oldVal = this.QueryEntity(DayWorkDetail.class, "select * from DayWorkDetail where ID ='" + m_DayWorkDetail.ID + "'");
		ContentValues values=new ContentValues();
		
		
		if (!Utility.StringEquals(oldVal.MemberID, m_DayWorkDetail.MemberID))
			values.put("MemberID", m_DayWorkDetail.MemberID);
		if (!Utility.StringEquals(oldVal.WDHID, m_DayWorkDetail.WDHID))
			values.put("WDHID", m_DayWorkDetail.WDHID);
		if (oldVal.ZhengBang != m_DayWorkDetail.ZhengBang)
			values.put("ZhengBang", m_DayWorkDetail.ZhengBang);
		if (oldVal.BaiFang != m_DayWorkDetail.BaiFang)
			values.put("BaiFang", m_DayWorkDetail.BaiFang);
		if (oldVal.GenJin != m_DayWorkDetail.GenJin)
			values.put("GenJin", m_DayWorkDetail.GenJin);
		if (oldVal.PuDian != m_DayWorkDetail.PuDian)
			values.put("PuDian", m_DayWorkDetail.PuDian);
		if (oldVal.PeiXun != m_DayWorkDetail.PeiXun)
			values.put("PeiXun", m_DayWorkDetail.PeiXun);
		if (!Utility.StringEquals(oldVal.Remark, m_DayWorkDetail.Remark))
			values.put("Remark", m_DayWorkDetail.Remark);
		if (oldVal.DaiGZ != m_DayWorkDetail.DaiGZ)
			values.put("DaiGZ", m_DayWorkDetail.DaiGZ);
		ExecuteUpdate("DayWorkDetail", values, "ID='" + m_DayWorkDetail.ID + "'");
	}

	public void Delete(String id) throws Exception
	{
		ExecuteDelete("DayWorkDetail", "ID='" + id + "'");
	}

}
