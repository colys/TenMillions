package DataAccess;

import java.util.LinkedList;

import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.Member; 
import com.colys.tenmillion.Entity.TrainItem;
import com.colys.tenmillion.Entity.TrainPlan;
import com.colys.tenmillion.Entity.TrainRecord;

import android.content.ContentValues;
import android.content.Context;


public class TrainPlanAccess extends BasicAccess{
public  TrainPlanAccess(Context content){
super(content);
}


public TrainPlan Get(String id) throws Exception{
	TrainPlan plan= this.QueryEntity(TrainPlan.class, "select * from TrainPlan  where ID ='"+id+"'");
	plan.SetRecord( baseAccess.Visit(TrainRecordAccess.class).QueryByPlan(plan.ID));
	return plan;
}

public LinkedList<TrainPlan> QueryMonth(int year,int month,int groupID){
	String monthOneDay,monthMaxDay ;
	if(month < 9){
		monthOneDay= year+"-0"+month+"-00";
		monthMaxDay= year+"-0"+month+"-32";
	}
	else {
		monthOneDay= year+"-"+month+"-00";
		monthMaxDay= year+"-"+month+"-32";
	}
	LinkedList<TrainPlan> lst = this.QueryEntityList(TrainPlan.class, "select * from TrainPlan where groupID ='"+groupID+"' and  ApplyDate between '"+monthOneDay+"' and '"+monthMaxDay+"'");
	for(TrainPlan plan : lst){
		plan.SetRecord(baseAccess.Visit(TrainRecordAccess.class).QueryByPlan(plan.ID));
	}
	return lst;
}

public boolean ExistsUnFinish() throws Exception{
	String sql ="select count(0) from TrainRecords where status=0";
	int count = Integer.valueOf(super.ExecuteScalar(sql));
	return count >0;
}

public LinkedList<Member> QueryUnTrainMembers(int trainItem,int groupid){
	return this.QueryEntityList(Member.class, "select Name,ID from Member where groupid ='"+groupid+"' and status > -1 and joinDate > '2014-01-01' and not exists(select 1 from  TrainRecords where MemberID = Member.ID and ItemID ='"+ trainItem +"')");
}
//public LinkedList<Member> QueryReadyTrainMembers(int trainItem){
//	return this.QueryEntityList(Member.class, "select Name,ID,PinYinJ from Member  where groupid ='"+getCurrentGroupId()+"' and status > -1  and exists(select 1 from  TrainReport where MemberID = Member.ID and Item"+ trainItem +" =1)");
//}



public void Add(TrainPlan m_trainplan) throws Exception {
	m_trainplan.ID = CreateGUID();
	ContentValues values=new ContentValues();
	if(m_trainplan.ID != null)    values.put("ID",m_trainplan.ID);
	if(m_trainplan.Name != null)    values.put("Name",m_trainplan.Name);
	if(m_trainplan.ApplyDate != null)    values.put("ApplyDate",m_trainplan.ApplyDate);
	values.put("IsFinish",m_trainplan.IsFinish);
	values.put("GroupID",m_trainplan.GroupID);
	ExecuteInsert("TrainPlan",values);
}

public void Update(TrainPlan m_trainplan) throws Exception {
	TrainPlan oldVal = this.QueryEntity(TrainPlan.class, "select * from TrainPlan where ID ='"+m_trainplan.ID+"'");
	ContentValues values=new ContentValues();
	if(!Utility.StringEquals(oldVal.Name,m_trainplan.Name))
	values.put("Name",m_trainplan.Name);
	if(!Utility.StringEquals(oldVal.ApplyDate,m_trainplan.ApplyDate))
	values.put("ApplyDate",m_trainplan.ApplyDate);
	if(oldVal.IsFinish != m_trainplan.IsFinish)
	values.put("IsFinish",m_trainplan.IsFinish);
	ExecuteUpdate("TrainPlan",values,"ID='"+m_trainplan.ID+"'");
}

public void Delete(String id) throws Exception {
	TrainPlan plan = this.Get(id);	
	
	for(TrainItem item :  plan.GetTrainItems() ){
		for(TrainRecord record :  item.Records){
			baseAccess.Visit(TrainRecordAccess.class).Delete(record.ID);
		}
		
	}
	ExecuteDelete("TrainPlan","ID='"+ id +"'");
}

}
