package CustomViews;
 
import java.util.Calendar; 

import android.app.Activity;
import android.app.DatePickerDialog; 
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;



public class DateClickListener implements android.view.View.OnClickListener {
	Activity activity;
	EditText happenView;
	DatePickerDialog.OnDateSetListener dateSetListener;
	
	public DateClickListener(Activity activity){
		this.activity = activity;
		
	}
	
	public void SetOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
		dateSetListener=listener; 
	}
	

	@Override
	public void onClick(View v) {
		happenView =(EditText) v;
		happenView.setInputType(android.text.InputType.TYPE_NULL);
		String val = happenView.getText().toString();
		int year,month,day;
		if(val.isEmpty()) {
			Calendar c = Calendar.getInstance();
			year =c.get(Calendar.YEAR);
			month =c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
		}else{
			
			String[] valArr = val.split("-");
			
			year = Integer.valueOf(valArr[0]);
			month =Integer.valueOf(valArr[1]) -1;			
			day =Integer.valueOf(valArr[2]);
		
		}
		
		
		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {  
			  
		       
			public void onDateSet(DatePicker view, int year, int monthOfYear,   int dayOfMonth) {  		
		    	   String dateStr =String.valueOf(year);
		    	   dateStr+="-";
		    	   if(monthOfYear < 9)
		    		   dateStr +="0"+ (monthOfYear+1);
		    	   else dateStr += String.valueOf(monthOfYear+1);
		    	   dateStr+="-";
		    	   if(dayOfMonth < 10)
		    		   dateStr +="0"+ (dayOfMonth);
		    	   else dateStr += String.valueOf(dayOfMonth);
		    	   if(happenView.getText().toString().equals(dateStr)) return;
		    	   happenView.setText(dateStr);
		    	   if(dateSetListener!=null) dateSetListener.onDateSet(view, year, monthOfYear, dayOfMonth);
		       }  
		  
		    };  
		   
		    DatePickerDialog   dialog = new DatePickerDialog(activity, 
		    		mDateSetListener, 
                   year, 
                    month, 
                    day); 
		    dialog.show();
		
	}
	
}
