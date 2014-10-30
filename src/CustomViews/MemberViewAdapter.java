package CustomViews;

import java.util.LinkedList;
 
import com.colys.tenmillion.Entity.Member;

import android.content.Context; 
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class MemberViewAdapter extends BaseAdapter implements Filterable{
	
	Context context;
	
	MemberFilter mfilter;
	
	public LinkedList<Member> memberList;
	
	public MemberViewAdapter(Context context,LinkedList<Member> list){
		this.context= context;
		for(Member m :list){
			m.NineGG = "";
			if(m.PinYinJ == null){m.PinYinJ="";continue;}
			if(m.PinYinJ.isEmpty()) continue;			
			for(char c :m.PinYinJ.toUpperCase().toCharArray()){
				int val =(c-65)/3 + 1;
				if(val> 8) val =8;
				m.NineGG+= String.valueOf(val);
			}
		}
		this.memberList = list;
		mfilter = new MemberFilter(list);
	}

	@Override
	public int getCount() { 
		return memberList.size();
	}

	@Override
	public Object getItem(int arg0) { 
		return memberList.get(arg0).Name;
	}

	@Override
	public long getItemId(int arg0) { 
		return (long)arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) { 
        TextView txtMember = new TextView(context);
        txtMember.setText(memberList.get(arg0).Name);
        txtMember.setHeight(48);
		return txtMember;
	}

	@Override
	public Filter getFilter() {  
		return mfilter;
	}

}
