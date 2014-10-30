
package DataAccess; 
import com.colys.tenmillion.Entity.MonthPlan;
import android.content.ContentValues;
import android.content.Context;

  
public class MonthPlanAccess extends BasicAccess{
	private  MonthPlanAccess(Context content){
	    super(content);
	}
	
	public void Add(MonthPlan m_monthplan) throws Exception{ 
	    ContentValues values=new ContentValues();
	    values.put("Year",m_monthplan.Year);
	    values.put("Month",m_monthplan.Month);
	    values.put("MemberID",m_monthplan.MemberID);
	    values.put("PiShu",m_monthplan.PiShu);
	    values.put("FenE",m_monthplan.FenE);
	    values.put("Remark",m_monthplan.Remark); 
	    ExecuteInsert("MonthPlan",values);
	}


	public int CompletePlans(int year, int month, MonthPlan plan) throws Exception
	{
	
	    int i = 0;
	  
	         ContentValues values=new ContentValues();
	      
	        values.put("PiShu",plan.PiShu);
	        values.put("FenE",plan.FenE);
	        values.put("Remark",plan.Remark); 
	       int count =  ExecuteUpdate("MonthPlan",values,"year="+year+" and month = "+ month +" and MemberID='"+ plan.MemberID+"'" );
	     
	        if ( count == 0)
	        {
	            values.put("Year",plan.Year);
	            values.put("Month",plan.Month);
	            values.put("MemberID",plan.MemberID);
	            ExecuteInsert("MonthPlan",values);
	        }
	        i++;
	  
	
	    return i;
	}

	public void Delete(int year, int month, String memberID) throws Exception {
		ExecuteDelete("MonthPlan", "year="+year+" and month = "+ month +" and MemberID='"+ memberID+"'");
		
	}
}
