package DataAccess;

import java.util.LinkedList;

import com.colys.tenmillion.Utility; 
import com.colys.tenmillion.Entity.TrainItem;
import com.colys.tenmillion.Entity.TrainRecord;

import android.content.ContentValues;
import android.content.Context;

public class TrainRecordAccess extends BasicAccess{
    public  TrainRecordAccess(Context content){
    super(content);
}
    

public TrainRecord Get(int id) throws Exception{
	return this.QueryEntity(TrainRecord.class, "select * from TrainRecords  where ID ='"+id+"'");
}

    public LinkedList<TrainRecord> QueryByPlan(String planID){
    	return this.QueryEntityList(TrainRecord.class, "select TrainItems.Name ItemName,Member.Name MemberName, TrainRecords.* from TrainRecords join TrainItems on TrainItems.ID = TrainRecords.ItemID join Member on Member.ID = TrainRecords.MemberID where planid='"+ planID +"'");
    }

public void Add(TrainRecord m_trainrecord) throws Exception {
    m_trainrecord.ID = CreateGUID();
    ContentValues values=new ContentValues();
    values.put("ItemID",m_trainrecord.ItemID);
    if(m_trainrecord.MemberID != null)    values.put("MemberID",m_trainrecord.MemberID);
    values.put("Status",m_trainrecord.Status);
    if(m_trainrecord.PlanID != null)    values.put("PlanID",m_trainrecord.PlanID);
    values.put("ID",m_trainrecord.ID);
    ExecuteInsert("TrainRecords",values);
}
public void Update(TrainRecord m_trainrecord) throws Exception{
	 TrainRecord oldVal = this.QueryEntity(TrainRecord.class, "select * from TrainRecords where ID ='"+m_trainrecord.ID+"'");
	 ContentValues values=new ContentValues();
	 if(oldVal.ItemID != m_trainrecord.ItemID)
	     values.put("ItemID",m_trainrecord.ItemID);
	 if(!Utility.StringEquals(oldVal.MemberID,m_trainrecord.MemberID))
	     values.put("MemberID",m_trainrecord.MemberID);
	 if(oldVal.Status != m_trainrecord.Status){
	     values.put("Status",m_trainrecord.Status);
	     ContentValues reportValues=new ContentValues();
	     if(m_trainrecord.Status ==1){
	    	 //标记为已经培训
	    	 reportValues.put("item"+m_trainrecord.ItemID  , 1);
	    	 ExecuteUpdate("TrainReport",reportValues,"MemberID='"+m_trainrecord.MemberID+"'");
	     }else{
	    	 if(oldVal.Status ==1 ){
	    		 reportValues.put("item"+m_trainrecord.ItemID  , 0);
		    	 ExecuteUpdate("TrainReport",reportValues,"MemberID='"+m_trainrecord.MemberID+"'");
	    	 }
	     }
	 }
	 if(!Utility.StringEquals(oldVal.PlanID,m_trainrecord.PlanID))
	     values.put("PlanID",m_trainrecord.PlanID);
	  ExecuteUpdate("TrainRecords",values,"ID='"+m_trainrecord.ID+"'");
}


public void Delete(String id) throws Exception {
      ExecuteDelete("TrainRecords","ID='"+ id +"'");
}

}
