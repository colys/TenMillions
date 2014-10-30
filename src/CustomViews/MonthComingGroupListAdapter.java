package CustomViews; 
import java.util.LinkedList; 

import com.colys.tenmillion.R; 
import com.colys.tenmillion.Entity.PeopleComing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.TextView;
import com.colys.tenmillion.*;
import java.util.*;
import android.util.*;



public class MonthComingGroupListAdapter extends android.widget.BaseAdapter {
	
	private LinkedList<PeopleComing> comingList = null;
Calendar calToday;
//int stayNum = 0,goBackNum =0,sgNum=0,returnSum=0,returnSGSum=0,beforeSGSum=0;
	SummaryItem[] summaryArr = new SummaryItem[]{new SummaryItem(),new SummaryItem(),new SummaryItem()};
	
	public MonthComingGroupListAdapter(Context context, LinkedList<PeopleComing> list,int year,int month) {  
		this.comingList = new LinkedList<PeopleComing>();
        /* tagArray.add("online");
         tagArray.add("Week1");//1-7
         tagArray.add("Week2");//8-14
         tagArray.add("Week3");//15-21
         tagArray.add("Week4");//22-30    
		 */         
		 calToday= Calendar.getInstance();
		PeopleComing pcOnlineGroup =new PeopleComing();
		pcOnlineGroup.Name = "正在走工作";
		this.comingList.add(pcOnlineGroup);
		for(PeopleComing pc : list){
			if(pc.Status ==1){
				comingList.add(pc);
			}
		}
		PeopleComing willComeGroup =new PeopleComing();
		willComeGroup.Name = "将要来";
		this.comingList.add(willComeGroup);         
		for(PeopleComing pc : list){        	  
			if(pc.Status ==0){
				comingList.add(pc);
			}
		}         

		String monthOneDay,monthMaxDay ;
		if(month < 10){
			monthOneDay= year+"-0"+month+"-00";
			monthMaxDay= year+"-0"+month+"-32";
		}
		else {
			monthOneDay= year+"-"+month+"-00";
			monthMaxDay= year+"-"+month+"-32";
		}
		LinkedList<PeopleComing> beforeList=new LinkedList<PeopleComing>();
		LinkedList<PeopleComing> week1List=new LinkedList<PeopleComing>();
		LinkedList<PeopleComing> week2List=new LinkedList<PeopleComing>();
		LinkedList<PeopleComing> week3List=new LinkedList<PeopleComing>();
		LinkedList<PeopleComing> week4List=new LinkedList<PeopleComing>(); 
		for( PeopleComing pc : list){
			if( pc.Status > 0){
				int index ;
				if(pc.Remark!=null && pc.Remark.indexOf(PeopleComingActivity.return_string_in_remark)==0){
					index = 1;					
				}else{
					index =pc.ArriveDate.compareTo(monthOneDay ) < 0?2:0;
					if(index==2) beforeList.add(pc);
				}
				summaryArr[index].Count++;
				switch(pc.Status){
					case 2:
						summaryArr[index].ShenGou++;
					case 1:
					case 3:
						summaryArr[index].Stay++;
						break;
					case 4:
						summaryArr[index].GoAway++;
						break;						
				}
			}
			
			if( pc.Status > 0 && pc.ArriveDate!=null && pc.ArriveDate.compareTo(monthOneDay) >0 && pc.ArriveDate.compareTo(monthMaxDay) < 0 ){
				int day = Integer.valueOf(pc.ArriveDate.substring(pc.ArriveDate.length() -2));
				if(day > 0 && day < 8) week1List.add(pc);
				else if (day > 7 && day < 15) week2List.add(pc);
				else if (day > 14 && day < 22) week3List.add(pc);
				else if (day > 21) week4List.add(pc);
				//统计本月
				
				
			}
		}
		
		PeopleComing beforeGroup =new PeopleComing();
		beforeGroup.Name = "月初之前";
		this.comingList.add(beforeGroup);
		for(PeopleComing pc:beforeList) comingList.add(pc);
		
		PeopleComing Week1Group =new PeopleComing();
		Week1Group.Name = "第一周";
		this.comingList.add(Week1Group);
		for(PeopleComing pc:week1List) comingList.add(pc);

		PeopleComing Week2Group =new PeopleComing();
		Week2Group.Name = "第二周";
		this.comingList.add(Week2Group);
		for(PeopleComing pc:week2List) comingList.add(pc);

		PeopleComing Week3Group =new PeopleComing();
		Week3Group.Name = "第三周";
		this.comingList.add(Week3Group);
		for(PeopleComing pc:week3List) comingList.add(pc);

		PeopleComing Week4Group =new PeopleComing();
		Week4Group.Name = "第四周";
		this.comingList.add(Week4Group);
		for(PeopleComing pc:week4List) comingList.add(pc);
		//int total = week1List.size()+week2List.size() + week3List.size() + week4List.size()-returnSum;
		String strSummary = "本月：来"+ summaryArr[0].Count +"批，留"+ summaryArr[0].Stay+ "批，申"+summaryArr[0].ShenGou+"批，走"+summaryArr[0].GoAway+"批\n" +
				"回头：来"+ summaryArr[1].Count +"批，留"+ summaryArr[1].Stay+ "批，申"+summaryArr[1].ShenGou+"批，走"+summaryArr[1].GoAway+"批\n" +
				"之前：来"+ summaryArr[2].Count +"批，留"+ summaryArr[2].Stay+ "批，申"+summaryArr[2].ShenGou+"批，走"+summaryArr[2].GoAway+"批\n" ;
		PeopleComing summaryGroup =new PeopleComing();
		summaryGroup.Name = "本月汇总";
		this.comingList.add(summaryGroup);
		PeopleComing summaryDetail =new PeopleComing();
		summaryDetail.Name = strSummary;
		summaryDetail.ID ="summaryDetail";
		this.comingList.add(summaryDetail);

	}

//禁止标签项的响应事件
	@Override
	public boolean isEnabled(int position) {
		if(comingList.get(position).ID == null){
			return false;
		}
		return super.isEnabled(position);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {    	 

		ViewGroup viewgroup;
		PeopleComing pc = comingList.get(position);
		if(pc.ID == null){
			viewgroup =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_people_coming_group, null);
			TextView txtGroup =(TextView) viewgroup.findViewById(R.id.item_month_coming_group_name);
			txtGroup.setText(pc.Name);             

		}else{                    
			viewgroup =(ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_people_coming, null);
			TextView txtMember = (TextView) viewgroup.findViewById(R.id.item_month_people_coming_member);
			if(pc.MemberID ==null){
				txtMember.setText(pc.Name);
			}else{
				txtMember.setText(String.format("%s号.%s",Integer.valueOf( pc.ArriveDate.substring(8)),  pc.MemberName));
				TextView txtrelation = (TextView) viewgroup.findViewById(R.id.item_month_people_coming_relation_day);
				String titleInfo = "";
				if(pc.Relation!=null) titleInfo +=" "+ pc.Relation;
				if(pc.ComeFrom!=null) titleInfo +=" "+ pc.ComeFrom;
				if(pc.Job!=null) titleInfo +=" "+ pc.Job;
				txtrelation.setText(titleInfo);
				TextView txtResult = (TextView) viewgroup.findViewById(R.id.item_month_people_coming_result);
				if(pc.Status ==0|| pc.DayCount==0)
					txtResult.setText(pc.Remark);
				else{
					Calendar cal =Utility.ConvertToCalendar(pc.ArriveDate);
					cal.add(Calendar.DATE,pc.DayCount-1);
					int days=	Utility.getDateDays(calToday,cal);
					Log.i("tip","days is "+days);
					if( days==0){
						txtResult.setText("[今]"+pc.DayResult);
					}else if(days==1){
						txtResult.setText("[昨]"+pc.DayResult);
					}else if(days==2){
						txtResult.setText("[前]"+pc.DayResult);
					}else
						txtResult.setText(pc.DayResult);
				}
					
			}

		}
		return viewgroup;
	}

	@Override
	public int getCount() { 
		return this.comingList.size();
	}

	@Override
	public Object getItem(int arg0) {

		return this.comingList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return  arg0;
	}
	
	class SummaryItem{
		public int Count;
		public int Stay;
		public int ShenGou;
		public int GoAway;
		
	}

}

