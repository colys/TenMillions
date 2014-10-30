package com.colys.tenmillion; 
import java.util.LinkedList; 

import com.colys.tenmillion.Entity.House;
import com.colys.tenmillion.Entity.PeopleWorking; 
import CustomViews.WSActivity; 
import DataAccess.BasicAccess;
import DataAccess.HouseAccess;
import DataAccess.PeopleWorkingAccess;
import android.os.Bundle; 
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner; 

public class EditPeopleWorkingActivity extends WSActivity {
	private EditText txtDayCount;
	private EditText txtResult;
	private Spinner txtHouse; 
	protected LinkedList<House> houseList;
	private House selectedHouse;
	private PeopleWorking working;
	Button	saveBtn;
	BasicAccess m_Access ;
	private boolean changed=false;
	
	
	protected int getLayout()  {
		return R.layout.activity_people_working_edit;
	}
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 
		txtDayCount=(EditText) findViewById(R.id.people_working_item_day);
		txtResult=(EditText) findViewById(R.id.people_working_item_result);
		txtHouse=(Spinner) findViewById(R.id.people_working_item_house);
		m_Access =new BasicAccess(this);
		String json = this.getIntent().getExtras().getString("json");
		if(json.isEmpty()){
			working = new PeopleWorking();
			working.ComingID = this.getIntent().getExtras().getString("comingID");
			working.DayCount = this.getIntent().getExtras().getInt("day");
			working.HouseID= this.getIntent().getExtras().getString("houseID");
			txtDayCount.setText(String.valueOf(working.DayCount));
		}else{
			working = PeopleWorking.FromJson(json);			
			txtDayCount.setText(String.valueOf(working.DayCount));
			txtResult.setText(working.Result);
			
			
		}
		
		
		saveBtn	=(Button) findViewById(R.id.people_working_item_save_button);
		saveBtn.setOnClickListener(new	View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SaveWorking();
				
			}
		});
		saveBtn.setEnabled(false);
		QueryHouse();
		
	}
	
	final int QueryHouse =0,UpdateWorking =1,AddWorking =2;
	
	private void QueryHouse(){
		if(Utility.UseLocal){
			houseList = m_Access.Visit(HouseAccess.class).Query(false, getCurrentGroupId());
			String[] arr = new String[houseList.size()];
			for(int i=0;i<houseList.size();i++) arr[i]= houseList.get(i).Name;
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(EditPeopleWorkingActivity.this,android.R.layout.simple_dropdown_item_1line,arr);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			txtHouse.setAdapter(adapter);
			txtHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0,
						View arg1, int arg2, long arg3) {
					selectedHouse = houseList.get(arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					selectedHouse =null;
				}
				
			}
			);
			
			for(int i=0;i<houseList.size();i++ ){
	    		if(houseList.get(i).ID.equals(working.HouseID)){
	    			txtHouse.setSelection(i);
	    			break;
	    		}    		
	    	}
			saveBtn.setEnabled(true);
		}else{
			ws.visitServices("DayWork_QueryHouseJson",new String[]{ "showTemp","groupID"},new String[]{"false",String.valueOf(getCurrentGroupId())} ,QueryHouse);
		}
			
		
	}
	
	private void SaveWorking(){
		if(selectedHouse == null) {
			ws.Toast(R.string.please_choose_house_message);
			return;
		}
		working.HouseID = selectedHouse.ID;
		working.DayCount =Integer.valueOf(txtDayCount.getText().toString());
		working.Result = txtResult.getText().toString();
		//String json = working.ToJson();
		try {
		 if(working.ID == null)
			
				m_Access.Visit(PeopleWorkingAccess.class).Add(working);
			
		else 
			m_Access.Visit(PeopleWorkingAccess.class).Update(working);
			// ws.visitServices("Coming_UpdateWorkingJson", "workJson",json,UpdateWorking);
		} catch (Exception e) {
			ws.Toast(e.getMessage());
			return;
		}
		m_Access.Close(true);
		changed = true;
		setResult(RESULT_OK);
		finish();
	}
	
	
	public void onHandleMessage(Message msg){		
		switch(msg.what){
		case AddWorking:
		case UpdateWorking:
					changed = true;
					setResult(RESULT_OK);
					finish();
				break;
		case QueryHouse:
					houseList = House.ListFromJson(ws.queryResult);
					String[] arr = new String[houseList.size()];
					for(int i=0;i<houseList.size();i++) arr[i]= houseList.get(i).Name;
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(EditPeopleWorkingActivity.this,android.R.layout.simple_dropdown_item_1line,arr);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					txtHouse.setAdapter(adapter);
					txtHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							selectedHouse = houseList.get(arg2);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							selectedHouse =null;
						}
						
					}
					);
					
					for(int i=0;i<houseList.size();i++ ){
			    		if(houseList.get(i).ID.equals(working.HouseID)){
			    			txtHouse.setSelection(i);
			    			break;
			    		}    		
			    	}
					saveBtn.setEnabled(true);
					break;
		}
	}
	
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        	if(changed){	        	
					/*String passString = working.ToJson();
					intent.putExtra("json", passString);*/
	        		setResult(RESULT_OK);
	        	}
	        	else
	        		setResult(RESULT_CANCELED);
	        }
	        return super.onKeyDown(keyCode, event);
	    }

}
