package DataAccess;
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.*;

import android.content.*;
import java.util.*;

public class TaskAccess extends BasicAccess
{
	private  TaskAccess(Context content){
		super(content);
	}
	
	
	public LinkedList<Task> GetByDate(String day, boolean inlcudeUnfinish, int groupID)
    { 
        String sql = "select * from Tasks where groupid="+groupID+" and ('"+day+"' between begindate and enddate ";
        if (inlcudeUnfinish) sql +=  "or ( isfinish = 0 and  begindate <'"+day+"' )";
        sql += ") order by begindate";
         return super.QueryEntityList(Task.class, sql);
    }
	
	public void Add(Task task) throws Exception{
		task.ID = CreateGUID();
		ContentValues values=new ContentValues();
		values.put("ID",task.ID);
		values.put("GroupID",task.GroupID);
		values.put("BeginDate",task.BeginDate);
		values.put("EndDate",task.EndDate);
		values.put("IsFinish",task.IsFinish);
		
		values.put("Text",task.Text);
		ExecuteInsert("Tasks",values);
	}

	public void Update(Task m_task) throws Exception{
		Task oldVal = this.QueryEntity(Task.class, "select * from Tasks where ID ='"+m_task.ID+"'");
		 ContentValues values=new ContentValues();
		 if(!Utility.StringEquals(oldVal.Text,m_task.Text))
		     values.put("Text",m_task.Text);
		 if(!Utility.StringEquals(oldVal.BeginDate,m_task.BeginDate))
		     values.put("BeginDate",m_task.BeginDate);
		 if(!Utility.StringEquals(oldVal.EndDate,m_task.EndDate))
		     values.put("EndDate",m_task.EndDate);
		 if(oldVal.IsFinish != m_task.IsFinish)
		     values.put("IsFinish",m_task.IsFinish);
		 if(oldVal.GroupID != m_task.GroupID)
		     values.put("GroupID",m_task.GroupID);
		  ExecuteUpdate("Tasks",values,"ID='"+m_task.ID+"'");
	}
	
	public void Delete(String id) throws Exception{
		ExecuteDelete("Tasks","id='"+ id +"'");
	}
		
}
