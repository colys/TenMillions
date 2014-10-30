package DataAccess;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.Member;
import com.colys.tenmillion.Entity.TrainReport;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import java.util.*;


public class MemberAccess extends BasicAccess{
	private  MemberAccess(Context content){
super(content);
}

public void UpdateFenE(String id, int absFenE) throws Exception
{ 
    ContentValues values=new ContentValues();
    String oldFenE = super.ExecuteScalar("select xvalue from member where id ='"+ id +"'");
    if(oldFenE ==null) oldFenE ="0";
    int newFenE =Integer.valueOf(oldFenE) + absFenE ;
    values.put("XValue", String.valueOf(newFenE));
    ExecuteUpdate("Member",values,"ID='"+id+"'"); 
}

public void UpdateStatus(String  id, int status) throws Exception
{
	ContentValues values=new ContentValues();
	values.put("status", status);
	ExecuteUpdate("Member",values,"ID='"+id+"'");  
}



public void Add(Member m_member) throws Exception{
	if(m_member.GroupID == 0) throw new Exception("groupID is null of member");
	m_member.ID = CreateGUID();
	ContentValues values=new ContentValues();
	values.put("ID",m_member.ID);
	values.put("Name",m_member.Name);
	values.put("ReferenceID",m_member.ReferenceID);
	values.put("Status",m_member.Status);
	values.put("XValue",m_member.XValue);
	values.put("PinYinJ",m_member.PinYinJ);
	values.put("JoinDate",m_member.JoinDate);
	values.put("JiangBan",m_member.JiangBan);
	values.put("GroupID",m_member.GroupID);
	values.put("SuZhiKe",m_member.SuZhiKe);
	values.put("WenHuaKe",m_member.WenHuaKe);
	ExecuteInsert("Member",values);
	//init trainreport
	ContentValues values2=new ContentValues();
	values2.put("MemberID",m_member.ID);
	ExecuteInsert("TrainReport",values2);
}
public void Update(Member m_member) throws Exception{		
	Member oldVal = this.QueryEntity(Member.class, "select * from Member where ID ='"+m_member.ID+"'");
	 ContentValues values=new ContentValues();
	 if(!Utility.StringEquals(oldVal.Name,m_member.Name))
	     values.put("Name",m_member.Name);
	 if(!Utility.StringEquals(oldVal.ReferenceID,m_member.ReferenceID))
	     values.put("ReferenceID",m_member.ReferenceID);
	 if(oldVal.Status != m_member.Status)
	     values.put("Status",m_member.Status);
	 if(oldVal.XValue != m_member.XValue)
	     values.put("XValue",m_member.XValue);
	 if(!Utility.StringEquals(oldVal.PinYinJ,m_member.PinYinJ))
	     values.put("PinYinJ",m_member.PinYinJ);
	 if(!Utility.StringEquals(oldVal.JoinDate,m_member.JoinDate))
	     values.put("JoinDate",m_member.JoinDate);
	 if(!Utility.StringEquals(oldVal.JiangBan,m_member.JiangBan))
	     values.put("JiangBan",m_member.JiangBan);
	 if(oldVal.GroupID != m_member.GroupID)
	     values.put("GroupID",m_member.GroupID);
	 if(oldVal.SuZhiKe != m_member.SuZhiKe)
	     values.put("SuZhiKe",m_member.SuZhiKe);
	 if(oldVal.WenHuaKe != m_member.WenHuaKe)
	     values.put("WenHuaKe",m_member.WenHuaKe);
	  ExecuteUpdate("Member",values,"ID='"+m_member.ID+"'");
}

	public void SwitchGroup(String id,int groupID) throws Exception{
		ContentValues values=new ContentValues();
		values.put("groupid", String.valueOf(groupID));
		ExecuteUpdate("Member",values,"ID='"+id+"'"); 
		String	sql = "select ID from member where ReferenceID = '" + id + "'";
		Cursor cur= baseAccess.Query(sql);
		List<String> lst = new ArrayList<String>();
		while(cur.moveToNext()){
			lst.add( cur.getString(0));
		}
		cur.close();
		for(String childid:lst){
			SwitchGroup(childid,groupID);
		}
	}

public void Delete(String id) throws Exception{
	
	String	sql = "select count(0) from member where ReferenceID = '" + id + "'";
	if(! ExecuteScalar(sql).equals("0")){
		throw new Exception("下面有业务员，不能删除");
	}
	ExecuteDelete("TrainReport","MemberID='"+ id +"'");
	ExecuteDelete("DayWorkDetail","MemberID='"+ id +"'");
	ExecuteDelete("Member","ID='"+ id +"'");
}


}
