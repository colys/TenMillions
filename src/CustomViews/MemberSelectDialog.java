package CustomViews;

import java.util.LinkedList;

import com.colys.tenmillion.R;
import com.colys.tenmillion.Entity.Member;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface; 
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView; 
import android.widget.AutoCompleteTextView; 

public class MemberSelectDialog {
 Context context;
LinkedList<Member> memberList;
 Dialog loadingDialog;
 DialogInterface.OnClickListener onClickListener;
 int titleRID;
 public Member SelectedMember;
 
 AutoCompleteTextView textView;
	
	public MemberSelectDialog(Context context,LinkedList<Member> list,DialogInterface.OnClickListener listener){
		this.context = context;
		this.memberList= list;
		onClickListener = listener;
		titleRID = R.string.please_choose_member;
	}
	
	public MemberSelectDialog(Context context,LinkedList<Member> list,int title,DialogInterface.OnClickListener listener){
		this.context = context;
		this.memberList= list;
		onClickListener = listener;
		titleRID = title;
	}
	
	public  void Show() {  
		 if(loadingDialog ==null){
			 LayoutInflater inflater = LayoutInflater.from(context);  
		        View v = inflater.inflate(R.layout.simple_autocomplete_item, null);// µ√µΩº”‘ÿview  
		        textView=(AutoCompleteTextView)  v.findViewById(R.id.autoCompleteTextView1);
		        String[] arr = new String[memberList.size()];
		        for(int i=0;i<arr.length;i++){
		        	Member m =memberList.get(i);
		        	arr[i]=m.PinYinJ +"-"+m.Name;
		        }
		        MemberViewAdapter av = new MemberViewAdapter(context, memberList);
		        textView.setAdapter(av);
		        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						SelectedMember = ((MemberViewAdapter)arg0.getAdapter()).memberList.get(arg2); 
					}
				});
		        
		         loadingDialog = new AlertDialog.Builder(context)      
                .setTitle(titleRID)    
                .setView(v)
                .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onClickListener.onClick(dialog, which);
						
					}
				})
                .setNegativeButton(R.string.cancel_button_text, null)
                .show();  
		        
		 }
		 else{  
			 
			 textView.setText("");
			 loadingDialog.show();
		 
		 }
	}
	
	  public  void Close(){
		     if(loadingDialog!=null){
		    	 loadingDialog.dismiss();
		     }
	    }
}
