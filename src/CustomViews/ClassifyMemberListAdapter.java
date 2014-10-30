package CustomViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.colys.tenmillion.R;
import com.colys.tenmillion.Utility;
import com.colys.tenmillion.Entity.HomeEntity;
import com.colys.tenmillion.Entity.Member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.TextView;

public class ClassifyMemberListAdapter extends android.widget.BaseAdapter
{

	List<String> resultList =new ArrayList<String>();
	List<String> resultListNew=new ArrayList<String>();
	List<String> resultListDazr=new ArrayList<String>();
	List<String> resultListBan1=new ArrayList<String>();
	List<String> resultListBan24=new ArrayList<String>();
	List<String> resultListBan3=new ArrayList<String>();
	List<String> resultListBan5=new ArrayList<String>();
	List<String> resultListNoBan=new ArrayList<String>();
	List<String> resultListNoSuZhiKe=new ArrayList<String>();
	List<String> resultListNoWenHuaKe=new ArrayList<String>();
	int[] GroupPostionArr =new int[10];
	int threeMonthAgo;
	//int lineSize = 5;//һ�м���
	int memberCount =0;
	HomeEntity hEntity = new HomeEntity();
	private String todayStr;
	@SuppressLint("SimpleDateFormat")
	public ClassifyMemberListAdapter(Context context,
									 LinkedList<Member> members)
	{



		int monthAgo = 3;
		Calendar nowCalendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		todayStr = sdf.format(nowCalendar.getTime());
		Calendar ThreeMonthAgoCalendar = Calendar.getInstance();
		ThreeMonthAgoCalendar.add(Calendar.MONTH, -monthAgo);
		
		nowCalendar.add(Calendar.DATE, 1);
		
		String threeMonthAgoStr = sdf.format(ThreeMonthAgoCalendar.getTime());

		threeMonthAgo = Integer.valueOf(threeMonthAgoStr.replace("-", ""));
		FindFromList(members);
		GroupPostionArr[0] = 0;
		resultList.add("在位情况");
		resultList.add(String.format("在位：%s人 , 请假：%s人 , 虚掉：%s人 , 总人数：%s人", hEntity.NormalMember, hEntity.AskForLeave, hEntity.NotBack, memberCount));
		GroupPostionArr[1] = resultList.size();
		resultList.add(monthAgo + "个月以内的新兵");
		ParseLineList(resultListNew);
		GroupPostionArr[2] = resultList.size();
		resultList.add("大主任");
		ParseLineList(resultListDazr);	
		GroupPostionArr[3] = resultList.size();   
		resultList.add("讲一班人员");
		ParseLineList(resultListBan1);
		GroupPostionArr[4] = resultList.size();
		resultList.add("讲2/4班人员");
		ParseLineList(resultListBan24);
		GroupPostionArr[5] = resultList.size();
		resultList.add("讲三班人员");
		ParseLineList(resultListBan3);
		GroupPostionArr[6] = resultList.size();
		resultList.add("讲五班人员");
		ParseLineList(resultListBan5);
		GroupPostionArr[7] = resultList.size();
		resultList.add(monthAgo + "个月未推正班人员");
		ParseLineList(resultListNoBan);
		GroupPostionArr[8] = resultList.size();
		resultList.add("15天内未开素质课");
		ParseLineList(resultListNoSuZhiKe);
		GroupPostionArr[9] = resultList.size();
		resultList.add("上经理未开文化课");
		ParseLineList(resultListNoWenHuaKe);
		

	}

	private void ParseLineList(List<String> lst)
	{
		int count =0 ;
		String str="";
		for (String m :lst)
		{			 
			count++;
			str += m + "  ";
//			if (count % lineSize == 0)
//			{
//				resultList.add(str);
//				str = "";
//			}
		}
		if (!str.isEmpty()) resultList.add(str);
	}


	private void FindFromList(LinkedList<Member> list)
	{

		for (Member m :list)
		{
			memberCount++;
			switch (m.Status)
			{
				case 0:
					hEntity.NormalMember ++;
					break;
				case 1:
					hEntity.AskForLeave++;
					break;
				case -1:
					hEntity.NotBack++;
					break;
			}
		
			if (m.TotalFenE > 39 && m.TotalFenE < 65) resultListDazr.add(m.Name + " " + m.TotalFenE + "份");
			if (m.Status == 0)
			{	
				int dateInt = Integer.valueOf(m.JoinDate.replace("-", ""));
				if (dateInt > threeMonthAgo)
				{
					resultListNew.add(m.Name + " " + m.XValue + "份");
				} else{
					if(m.JiangBan==null || m.JiangBan.isEmpty()){
						resultListNoBan.add(m.Name);
					}
				}
				if ("1".equals(m.JiangBan)) resultListBan1.add(m.Name);
				if ("2/4".equals(m.JiangBan)) resultListBan24.add(m.Name);
				if ("3".equals(m.JiangBan)) resultListBan3.add(m.Name);
				if ("5".equals(m.JiangBan)) resultListBan5.add(m.Name); 
				if(m.SuZhiKe ==0 && Utility.getDateDays(todayStr,m.JoinDate) > 15) resultListNoSuZhiKe.add(m.Name);				
				if(m.WenHuaKe ==0 &&  m.TotalFenE > 64 ) resultListNoWenHuaKe.add(m.Name);
			}
			if (m.CounterMan != null && m.CounterMan.size() > 0)
			{
				FindFromList(m.CounterMan);
			}
		}
	}



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
	{
    	boolean isGroupTitle =false;
    	for (int i:GroupPostionArr)
		{
    		if (i == position)
			{
    			isGroupTitle = true;
    			break;
    		}
    	}
    	if (isGroupTitle)
		{
    		//group
    		ViewGroup viewgroup =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_people_coming_group, null);
            TextView txtGroup =(TextView) viewgroup.findViewById(R.id.item_month_coming_group_name);
            txtGroup.setText(getItem(position).toString()); 
            return viewgroup;
    	}
		else
		{

    		ViewGroup view =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_textview_item, null);
            TextView txtGroup =(TextView) view.findViewById(R.id.item_text_view);
            txtGroup.setText(getItem(position).toString()); 
            if (position == 1)
			{
            	txtGroup.setTextColor(view.getResources().getColor(android.R.color.holo_red_light));
            }
            return view;
    	} 

    }


	@Override
	public int getCount()
	{		
		return resultList.size();
	}


	@Override
	public Object getItem(int arg0)
	{
		return resultList.get(arg0);
		
		
	}


	@Override
	public long getItemId(int arg0)
	{
		return arg0;
	}

}

