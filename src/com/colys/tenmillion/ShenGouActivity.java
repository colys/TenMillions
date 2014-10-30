package com.colys.tenmillion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.DefaultAccess; 
import DataAccess.ShenGouRecordsAccess;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView; 
import android.widget.TextView;

import com.colys.tenmillion.Entity.ShenGouRecords;
import CustomViews.*;
import android.util.*;

public class ShenGouActivity extends WSActivity {
	String startStr,endStr;
	EditText txtStart,txtEnd;
	TextView txtSummary;
	ShenGouRecords selectedShenGou;
	BasicAccess access ;

	@Override
	public int getLayout(){
		return R.layout.activity_shen_gou;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		access = new BasicAccess(ShenGouActivity.this);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		endStr = sdf.format(c.getTime());
		c.add(Calendar.DATE, -1* c.get(Calendar.DATE) +1);
		startStr = sdf.format(c.getTime());
		listview = (ListView) findViewById(R.id.shen_gou_listview);
		txtStart = (EditText) findViewById(R.id.shen_gou_search_begin_date);
		txtEnd = (EditText) findViewById(R.id.shen_gou_search_end_date);
		txtSummary =(TextView) findViewById(R.id.shen_gou_summary_text);
		Button btnSearch =(Button) findViewById(R.id.shen_gou_search_button);
		Button btnNew = (Button) findViewById(R.id.shen_gou_add_button);
		btnSearch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {				 
					runQuery();
				}
			});
		btnNew.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) { 
					Intent tent= new Intent();
					Bundle bundle=new Bundle(); 
					tent.putExtras(bundle);
					tent.setClass(ShenGouActivity.this,EditShenGouActivity.class);
					startActivityForResult(tent,1); 
				}
			});

		listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
					AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

					selectedShenGou =lst.get( minfo.position);
					menu.add(0, 0, 0, "修改");
					menu.add(1, 1, 1, "删除");
				}
			});
		txtStart.setOnClickListener(new DateClickListener(this));
		txtEnd.setOnClickListener(new DateClickListener(this));
		txtStart.setText(startStr);
		txtEnd.setText(endStr);
		runQuery();
	}


	@Override
    public void onConfigurationChanged(Configuration newConfig) {		
	    super.onConfigurationChanged(newConfig);
    }

	ListView listview ;
	LinkedList<ShenGouRecords> lst ;
	final int Query =1,Delete =2;
	public void onHandleMessage(Message msg){
		super.onHandleMessage(msg);	
		switch(msg.what){
			case Query:
				lst = ShenGouRecords.ListFromJson(ws.queryResult);
				BindListView();
				break;
			case Delete:
				ws.Toast("删除成功！");
				runQuery();
				break;
		}

	}

	private void BindListView(){ 

		String[]  listItem = new String[lst.size()];
		int total = 0;
        for(int i=0;i< lst.size();i++)
        {  	
        	ShenGouRecords mp = lst.get(i);
        	total += mp.SGFenE;
			Log.i("tip",mp.ToJson());
        	String str = mp.MemberName+" "+ mp.ApplyDate +" ";
        	if(mp.IsPatch) str+="补";
        	else str+="申购";
        	str+= mp.SGFenE +"份";
        	listItem[i]= str;
			//Log.i("tip",str);
        }
        txtSummary.setText("总共"+ total +"份");
		
        ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItem );
        listview.setAdapter(listItemAdapter);
        View emptyView = findViewById(R.id.shen_gou_listview_empty);
        listview.setEmptyView(emptyView);
	}


	public void runQuery(){
		startStr = txtStart.getText().toString();
		endStr = txtEnd.getText().toString();
		if(Utility.UseLocal){
			
			String sql="select Member.Name as MemberName,ShenGouRecords.* from ShenGouRecords "+
				"join Member on Member.ID = ShenGouRecords.MemberID " +
				"where  Member.groupid='"+ getCurrentGroupId() +"' and  ApplyDate between  '"+startStr+"' and '"+ endStr +"'";
		
			 lst = access.Visit(DefaultAccess.class).QueryEntityList(ShenGouRecords.class, sql);
			BindListView();
			access.Close(true);
		}else{
			ws.visitServices("ShenGou_QueryJson", new String[]{"start","end","groupID"}
							 ,new String[]{startStr,endStr,String.valueOf(getCurrentGroupId())}
							 , Query);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {  
		switch(item.getItemId()){
			case 0:				
				Intent tent= new Intent();
				Bundle bundle=new Bundle(); 
				bundle.putString("shenGouID",selectedShenGou.ID); 
				tent.putExtras(bundle);
				tent.setClass(this,EditShenGouActivity.class);
				startActivityForResult(tent,0); 
				break;
			case 1:
				//delete
				new AlertDialog.Builder(this)      
					.setTitle(R.string.dialog_tip_title)    
					.setMessage("确定要删除申购记录吗，此操作将会影响Member的份额！")    
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							access.OpenTransConnect();
							try {
								access.Visit(ShenGouRecordsAccess.class).Delete(selectedShenGou);
								runQuery();
							} catch (Exception e) {
								ws.Toast(e.getMessage());
								access.Close(false);
								return;
							}
							access.Close(true);
							//ws.visitServices("ShenGou_Delete" ,"ID",String.valueOf(selectedShenGou.ID), Delete);
						}
					} )  
					.setNegativeButton(R.string.cancel_button_text, null) 
					.show(); 
				break;
		}
		return super.onContextItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode ==  android.app.Activity.RESULT_OK){
			runQuery();
		}

	}
}
