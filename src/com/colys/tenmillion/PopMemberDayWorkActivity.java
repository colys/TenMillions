package com.colys.tenmillion; 
import com.colys.tenmillion.Entity.DayWorkDetail; 

import CustomViews.IPopMemberWorkCallback;
import CustomViews.TabViewFragment;
import DataAccess.DayWorkDetailAccess; 
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;  
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow; 
import android.widget.Spinner;
import android.widget.Toast;
import android.view.*;

public class PopMemberDayWorkActivity  {
	
	PopupWindow pop; 
	DayWorkDetail DayWorkDetail;
	Spinner txtZhengBan,txtGengJin,txtBaiFang,txtPeiXun,txtPuDian,txtDaiGZ;
	EditText txtRemark;
	View view;
	String updateWorkReturn;
	View happenView; 
	String queryHouseReturn; 
	Activity activity;
	boolean spinnerChange = false ;
	private IPopMemberWorkCallback m_callback;
	public boolean IsShow(){
		if(pop==null) return false;
		return pop.isShowing();
	}
	 
	public PopMemberDayWorkActivity(Activity a,IPopMemberWorkCallback callback){
		activity = a;
		m_callback = callback;
		LayoutInflater inflater = LayoutInflater.from(activity); 
        view = inflater.inflate(R.layout.activity_memeber_daywork, null);
        pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
        pop.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.pop_dialog_bg)); 
        pop.setOutsideTouchable(true); 
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener(){

			

			@Override
			public void onDismiss() {
				if(DayWorkDetail == null) return;
				String remark= txtRemark.getText().toString();
				if(DayWorkDetail.Remark == null ) DayWorkDetail.Remark ="";
				Message message =new Message();
				message.what = Utility.PopWin_Daywork_Edit;
				if(spinnerChange || (!remark.equals(DayWorkDetail.Remark)) ){
					DayWorkDetail.Remark = remark; 
					message.obj = DayWorkDetail;
					
					m_callback.Callback(happenView,DayWorkDetail);
					
					//web services
//					 new Thread(){
//						 @Override
//						 public void run(){	
//							 try{					 
//								 
//								 List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//								 params.add(new BasicNameValuePair("dayWorkJson",DayWorkDetail.ToJson()));
//								 updateWorkReturn = WSHelper.GetResponse("DayWork_SaveWorkJson",params);										 
//								 
//							 }
//							 catch(ServerException e){					 
//								 updateWorkReturn="err:"+e.getMessage();
//							 }
//							 handler.sendEmptyMessage(0);
//						 }
//					}.start();	
				}else{
					message.obj = null;
				}
				//tabViewFragment.ws.handler.sendMessage(message);
			}
        	
        });
	    txtZhengBan= (Spinner) view.findViewById(R.id.day_work_pop_zhengban);
	    txtGengJin= (Spinner) view.findViewById(R.id.day_work_pop_gengjin);
	    txtBaiFang= (Spinner) view.findViewById(R.id.day_work_pop_baifang);
	    txtPeiXun= (Spinner) view.findViewById(R.id.day_work_pop_peixun);
	    txtPuDian= (Spinner) view.findViewById(R.id.day_work_pop_pudian);
	    txtDaiGZ= (Spinner) view.findViewById(R.id.day_work_pop_daigz);
	    txtRemark= (EditText) view.findViewById(R.id.day_work_pop_remark); 	    
	    
	    //QueryHouse();
	}
	
	/*private void QueryHouse(){
		//get house
		 new Thread(){
			 @Override
			 public void run(){	
				 
				try{					 
					 
					 List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					 params.add(new BasicNameValuePair("showTemp","false"));
					 queryHouseReturn = WSHelper.GetResponse("DayWork_QueryHouseJson",params);										 
					 
				 }
				 catch(ServerException e){					 
					 queryHouseReturn="err:"+e.getMessage();
				 }
				 handler.sendEmptyMessage(0);
			 }
		}.start();	
	}*/
	ArrayAdapter<String> adapter;
	private void BindSpinner(){
		String[] arr = new String[]{"没有","1个","2个","3个","4个"};
	    adapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item,arr);  
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
         Spinner[] spinnerArr= new Spinner[]{txtZhengBan,txtGengJin,txtBaiFang,txtPeiXun,txtPuDian,txtDaiGZ};
         for(Spinner sp : spinnerArr){
        	 sp.setAdapter(adapter);
        	 sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					int value = arg2;
					
					switch(arg0.getId()){
						case R.id.day_work_pop_zhengban:
							if(DayWorkDetail.ZhengBang == value) return;
							DayWorkDetail.ZhengBang = value;							
							break;
						case R.id.day_work_pop_gengjin:
							if(DayWorkDetail.GenJin == value) return;
							DayWorkDetail.GenJin = value;
							break;
						case R.id.day_work_pop_baifang:
							if(DayWorkDetail.BaiFang == value) return;
							DayWorkDetail.BaiFang = value;
							break;
						case R.id.day_work_pop_peixun:
							if(DayWorkDetail.PeiXun == value) return;
							DayWorkDetail.PeiXun = value;
							break;
						case R.id.day_work_pop_pudian:
							if(DayWorkDetail.PuDian == value) return;
							DayWorkDetail.PuDian = value;
							break;
						case R.id.day_work_pop_daigz:
							if(DayWorkDetail.DaiGZ == value) return;
							DayWorkDetail.DaiGZ = value;
							break; 
					}		
					spinnerChange = true;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {					
					
				}
			});
         }
	}
	
	public void setValue(DayWorkDetail dws){
		DayWorkDetail = dws;
		if(DayWorkDetail ==null){
			Toast toast = Toast.makeText(activity , "setValue error: daywork is null", Toast.LENGTH_SHORT); 
			toast.show();
			Log.e("invoide error", "daywork is null"); 
			return;
		}
		txtZhengBan.setSelection(DayWorkDetail.ZhengBang);
		txtGengJin.setSelection(DayWorkDetail.GenJin);
		txtBaiFang.setSelection(DayWorkDetail.BaiFang);
		txtPeiXun.setSelection(DayWorkDetail.PeiXun);
		txtPuDian.setSelection(DayWorkDetail.PuDian);
		txtDaiGZ.setSelection(DayWorkDetail.DaiGZ);
		if(DayWorkDetail.Remark ==null) DayWorkDetail.Remark ="";
		 txtRemark.setText(DayWorkDetail.Remark);
		 
	}
	
	public DayWorkDetail getValue(){
		
		return DayWorkDetail;
	}
	
	
	public void Show(View v){	
		happenView =v;
        if(pop.isShowing()) {
            pop.dismiss(); 

        } else { 
            View parentLayout = ((ViewGroup) activity.findViewById(android.R.id.content) ).getChildAt(0);        
        	pop.showAtLocation(parentLayout,Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
        	if(adapter ==null)  BindSpinner();
        } 
	}
	
	/*
	private Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(updateWorkReturn!=null){
				if(updateWorkReturn.indexOf("err:")==0){
					 Toast toast = Toast.makeText(activity ,updateWorkReturn, Toast.LENGTH_LONG); 
					 toast.show();
				}else{
					updateWorkReturn = null;
					m_callback.Callback(happenView,DayWorkDetail);
				}
			}
			if(queryHouseReturn!=null){
				if(queryHouseReturn.indexOf("err:")==0){
					 Toast toast = Toast.makeText(activity ,queryHouseReturn, Toast.LENGTH_LONG); 
					 toast.show();
				}else{
					houseList = House.ListFromJson(queryHouseReturn);
					String[] arr = new String[houseList.size()];
					for(int i=0;i<houseList.size();i++) arr[i]= houseList.get(i).Name;
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(activity,android.R.layout.simple_dropdown_item_1line,arr);
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
					txtHouse.setVisibility(View.VISIBLE);  
					queryHouseReturn =null;
				}
			}
		}
	};
*/
}
