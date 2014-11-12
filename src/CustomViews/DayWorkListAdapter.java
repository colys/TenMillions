package CustomViews;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.colys.tenmillion.R;
import com.colys.tenmillion.Entity.AskForLeave;
import com.colys.tenmillion.Entity.DayWorkDetail;
import com.colys.tenmillion.Entity.DayWorkHouse;
import com.colys.tenmillion.Entity.PeopleComing;
import com.colys.tenmillion.Entity.Task;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DayWorkListAdapter extends android.widget.BaseAdapter {

	LinkedList<DayWorkHouse> m_data;
	LinkedList< Task> m_taskList;
	LinkedList< AskForLeave> m_askList,m_willBackList,m_overDateList;
	String queryDateStr;
	int total=0;
	
	public DayWorkListAdapter(Context context,LinkedList<DayWorkHouse> houseWorkList,LinkedList< Task> taskList,LinkedList< AskForLeave> askList,LinkedList< AskForLeave> willBackList,LinkedList< AskForLeave> overDateList) {
		m_taskList=taskList;
		m_askList = askList;
		m_willBackList = willBackList;
		m_overDateList = overDateList;
		m_data= houseWorkList;
		total = houseWorkList.size()+2;
		if(houseWorkList.size() > 0)
		queryDateStr = houseWorkList.get(0).WorkDay;
	}
	
	
 @Override
    public View getView(int position, View convertView, ViewGroup parent)
	{
	 
		 ViewGroup viewgroup =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house_working, null);
		 
		 TextView txtGroup =(TextView) viewgroup.findViewById(R.id.item_house_working_housename);
		 TextView txtComing =(TextView) viewgroup.findViewById(R.id.item_house_working_people_coming_1);
		 TextView txtMember =(TextView) viewgroup.findViewById(R.id.item_house_working_house_member1);
		 int size = m_data.size();
		 if(position < size){
			txtGroup.setText(m_data.get(position).House.Name);       
			txtMember.setText(Html.fromHtml(FormatHouseMemberWork(m_data.get(position))));		
			txtComing.setText(Html.fromHtml(FormatPeopleComing(m_data.get(position))));
		 }else if (position == size){
			 txtGroup.setText("请假与归队");    
			 String qingJia="",guiDui="";
				for (AskForLeave afl : m_askList)
				{
					if (afl.ApplyDate.equals(queryDateStr))
					{
						qingJia += afl.MemberName + " ";
					}
					if (afl.IsBack && afl.BackDate.equals(queryDateStr))
					{
						guiDui += afl.MemberName + " ";
					}
				}
				String resultStr ="";
				if(!qingJia.isEmpty())  resultStr +="请假："+qingJia;
				
				if(!guiDui.isEmpty()){
					if(!resultStr.isEmpty())resultStr +="<br>"; 
					resultStr +="归队：" +guiDui;
				}
				 
				if(m_willBackList.size()>0){
					String willBack ="";
					for (AskForLeave ask : m_willBackList)
					{
						willBack += ask.MemberName + " ";
					}
					 
					
					if(!resultStr.isEmpty())resultStr +="<br>";  
					resultStr +="即将到期 ："+willBack;
				} 
				if(m_overDateList.size()>0){
					String overDay="";
					for (AskForLeave ask : m_overDateList)
					{
						overDay += ask.MemberName + " ";
					}				 
					
					if(!resultStr.isEmpty())resultStr +="<br>";  
					resultStr +="到期未续 ："+overDay;
				}
				txtMember.setText(Html.fromHtml(resultStr));
		 }else{
			 txtGroup.setText("待办事项");    
			 String str="";
				for (Task task : m_taskList)
				{
					str += "·" + task.Text +"<br>";
				}				
				txtMember.setText(Html.fromHtml(str));
		 }
		/*
		 ViewGroup view =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house_working, null);
		 TextView txtHouse =(TextView) view.findViewById(R.id.item_house_working_housename);
		 TextView txtMember =(TextView) view.findViewById(R.id.item_house_working_house_member1);
		 TextView txtComing =(TextView) view.findViewById(R.id.item_house_working_people_coming_1);
		 txtHouse.setText(map.get("houseName").toString());
		 txtMember.setText(Html.fromHtml(map.get("Member").toString()));
		 txtComing.setText(map.get("People").toString());*/
		 return viewgroup;
	}


@Override
public int getCount() {
	return total;
}


@Override
public Object getItem(int arg0) {
	if(arg0< m_data.size()) return m_data.get(arg0);
	else return null;
}


@Override
public long getItemId(int arg0) {
	return arg0;
}
public String FormatPeopleComing(DayWorkHouse dayWorkHouse)
{
	int peopleSize = dayWorkHouse.PeopleComingList == null ? 0: dayWorkHouse.PeopleComingList.size();
    String peopleStr = "";
    for (int i=0;i < peopleSize;)
	{
    	PeopleComing pc = dayWorkHouse.PeopleComingList.get(i);
    	peopleStr += FormatPeopleWorking(pc);
    	i++;
        if (i < peopleSize) peopleStr += "\r\n";
    }
    if(dayWorkHouse.Remark!=null)    peopleStr+= dayWorkHouse.Remark;
    return peopleStr;
}

public static String FormatPeopleWorking(PeopleComing pc )
{
	return "<font color='#1d598b'><B>"+pc.MemberName +pc.Relation+"：</B>"+ pc.DayResult+"</font><br>";
}
public static String FormatHouseMemberWork(DayWorkHouse DayWorkHouse)
{

	String workStr =null;
	for (DayWorkDetail daywork : DayWorkHouse.Works)
	{
		String strItem = FormatMemberWork(daywork);
		if(workStr==null) workStr=strItem;
        else workStr +=  "&nbsp;&nbsp;"+strItem;
	}
	
	return workStr ;
}

public static String FormatMemberWork(DayWorkDetail daywork)
{
	String strItem = daywork.MemberName ;
	if (daywork.ZhengBang > 0) strItem += daywork.ZhengBang + "Z";
    if (daywork.GenJin > 0) strItem += daywork.GenJin + "G";
    if (daywork.BaiFang > 0) strItem += daywork.BaiFang + "B";
    if (daywork.PeiXun > 0) strItem += daywork.PeiXun + "PX";
    if (daywork.PuDian > 0) strItem += daywork.PuDian + "P";
    if (daywork.DaiGZ > 0) strItem += daywork.DaiGZ + "D";
    if (daywork.Remark != null && daywork.Remark != "") strItem += daywork.Remark;
    if(daywork.IsHouseChanged) strItem="<font color='#5c6063'>"+ strItem +"</font>";
    return strItem;
}

}
