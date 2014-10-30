package CustomViews;

import java.util.LinkedList;

import com.colys.tenmillion.Entity.Member;

import android.annotation.SuppressLint;
import android.widget.Filter;

public class MemberFilter extends Filter{
	
	LinkedList<Member> memberList,allMember;
	
	@SuppressWarnings("unchecked")
	public MemberFilter(LinkedList<Member> list){
		memberList= list;
		allMember = (LinkedList<Member>) list.clone();
	}
	
	
	@SuppressLint("DefaultLocale")
	public boolean FilterMember(String key){
		String lower = key.toUpperCase();
		memberList.clear();
		if(lower.isEmpty() ) return false;		
		for(int i=0;i< allMember.size();i++){
			Member m = allMember.get(i);
			if(m.PinYinJ ==null) m.PinYinJ="";
			if(m.PinYinJ.toUpperCase().indexOf(lower) > -1 || m.Name.indexOf(lower) > -1 ||m.NineGG.indexOf(lower)> -1 ){
				memberList.add(m);
			}
		}
		return true;
	}
	@SuppressLint("DefaultLocale")
	@Override
	protected FilterResults performFiltering(CharSequence arg0) { 
		FilterResults result = new FilterResults();
		if(!FilterMember(arg0.toString())) return result;
		result.count = memberList.size();
		result.values =memberList;
		return result;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		// TODO Auto-generated method stub
		 
	}
	 

}
