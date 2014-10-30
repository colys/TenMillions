package DataAccess;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.ShenGouRecords;

import android.content.ContentValues;
import android.content.Context;


public class ShenGouRecordsAccess extends BasicAccess{
	private  ShenGouRecordsAccess(Context content){
    super(content);
}

public void Add(ShenGouRecords m_shengourecords) throws Exception{
    m_shengourecords.ID = CreateGUID();
    ContentValues values=new ContentValues();
    values.put("ID",m_shengourecords.ID);
    values.put("MemberID",m_shengourecords.MemberID);
    values.put("ApplyDate",m_shengourecords.ApplyDate);
    values.put("SGFenE",m_shengourecords.SGFenE);
    values.put("IsPatch",m_shengourecords.IsPatch);
    ExecuteInsert("ShenGouRecords",values);
    baseAccess.Visit(MemberAccess.class).UpdateFenE(m_shengourecords.MemberID,  m_shengourecords.SGFenE); 
}
public void Update(ShenGouRecords m_shengourecords) throws Exception{		
	 ShenGouRecords oldVal = this.QueryEntity(ShenGouRecords.class, "select * from ShenGouRecords where ID ='"+m_shengourecords.ID+"'");
	 ContentValues values=new ContentValues();
	 if(!Utility.StringEquals(oldVal.MemberID,m_shengourecords.MemberID))
	     values.put("MemberID",m_shengourecords.MemberID);
	 if(!Utility.StringEquals(oldVal.ApplyDate,m_shengourecords.ApplyDate))
	     values.put("ApplyDate",m_shengourecords.ApplyDate);
	 if(oldVal.SGFenE != m_shengourecords.SGFenE)
	     values.put("SGFenE",m_shengourecords.SGFenE);
	 if(oldVal.IsPatch != m_shengourecords.IsPatch)
	     values.put("IsPatch",m_shengourecords.IsPatch);
	  ExecuteUpdate("ShenGouRecords",values,"ID='"+m_shengourecords.ID+"'");
	if(oldVal.SGFenE != m_shengourecords.SGFenE){
		baseAccess.Visit(MemberAccess.class).UpdateFenE(m_shengourecords.MemberID,  m_shengourecords.SGFenE - oldVal.SGFenE ); 
	}
}

public void Delete(ShenGouRecords m_shengourecords) throws Exception{
	  
	  ExecuteDelete("ShenGouRecords","ID='"+ m_shengourecords.ID +"'");
	  baseAccess.Visit(MemberAccess.class).UpdateFenE(m_shengourecords.MemberID,  - m_shengourecords.SGFenE); 
      
}

}
