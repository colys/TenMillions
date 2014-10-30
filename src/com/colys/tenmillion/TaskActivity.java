package com.colys.tenmillion;
import CustomViews.*;
import DataAccess.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*; 
import com.colys.tenmillion.Entity.*;
import java.util.*;
import android.view.ContextMenu.*;

public class TaskActivity extends WSActivity {
	
	LinkedList< Task> taskList;
	LinkedList< AskForLeave> askList;
	int queryCount =0;
	int selectedIndex=-1;

	private ListView listview;//正在查询的数量（调用webservices）
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Button btnAddTask =(Button) findViewById(R.id.task_add_button);
		Button btnAddAskFor =(Button) findViewById(R.id.ask_for_leave_add_button);
		btnAddTask.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent tent= new Intent();
				tent.setClass(TaskActivity.this,EditTaskActivity.class);
				startActivityForResult(tent,IntentTask); 
			}
		});
		btnAddAskFor.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent tent= new Intent();
				Bundle bundle=new Bundle(); 
				bundle.putInt("askID",0); 
				tent.putExtras(bundle);
				tent.setClass(TaskActivity.this,EditAskForLeaveActivity.class);
				startActivityForResult(tent,IntentTask); 
			}
		});
		listview = (ListView) findViewById(R.id.task_unfinish_listview);
		View emptyView = findViewById(R.id.month_people_coming_listview_empty_1);
        listview.setEmptyView(emptyView);
		listview.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener(){

				@Override
				public void onCreateContextMenu(ContextMenu p1, View p2, ContextMenu.ContextMenuInfo p3)
				{
					AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) p3;
					if (minfo.position == -1) return;
				    selectedIndex=minfo.position;
					p1.setHeaderTitle("您想做什么？");
					p1.add(0, 0, 0, "修改");
					p1.add(1, 1, 0, "删除");
				}

			
		});
		QueryTask();
		QueryAskForLeave();
	}
	 
	@Override
	protected int getLayout() {return R.layout.activity_task;}
	
	
	private void QueryTask(){
		if(Utility.UseLocal){
			String sql="select * from tasks where groupid='"+ getCurrentGroupId() +"' and  isfinish = 0 and GroupID = "+ getCurrentGroupId();
			EntityDBHelper<Task> helper =new EntityDBHelper<Task>(ws.GetSqlite(),Task.class);
			taskList = helper.QueryList(sql); 
			QueryAskForLeave();
		}else{
			queryCount++;
			ws.visitServices("Task_GetUnFinishJson",
					new String[]{"day","groupID"}
					,new String[]{
						Utility.GetNowString(),
						String.valueOf(getCurrentGroupId())
					},
					QueryTask);
		}
	}
	
	private void QueryAskForLeave(){
		if(Utility.UseLocal){
			String sql="select Member.Name MemberName,AskForLeave.* from AskForLeave join Member on Member.ID = AskForLeave.MemberID "+
					   "where Member.groupid='"+ getCurrentGroupId() +"' and isback = 0 and Member.Status > -1 ";
			EntityDBHelper<AskForLeave> helper =new EntityDBHelper<AskForLeave>(ws.GetSqlite(),AskForLeave.class);
			askList = helper.QueryList(sql);
			BindListView();
		}else{
			queryCount++;
			ws.visitServices("AskForLeave_QueryUnBackJson","groupID",String.valueOf(getCurrentGroupId()), QueryAskForLeave);
		}
		
	}
	
	
	public void onHandleMessage(Message msg){
		super.onHandleMessage(msg);	
		queryCount--;
		switch(msg.what){
		case QueryTask:			
			taskList= Task.ListFromJson(ws.queryResult);
			break;
		case QueryAskForLeave:			
			askList = AskForLeave.ListFromJson(ws.queryResult);			
			
			break;
		}
		if(queryCount ==0) BindListView();//两个都查好了，再绑定
	}

	final int  QueryAskForLeave = 1,QueryTask=2,IntentTask =3,IntentAskForLeave =4;
	

	private void BindListView(){
		if(taskList ==null || askList == null) return;
		
		String[]  listItem = new String[taskList.size()+ askList.size()];
        for(int i=0;i< taskList.size();i++)
        {  				    
        	Task mp = taskList.get(i);
        	if(mp.Text==null) mp.Text="";
        	listItem[i]= mp.Text ;
        }
        int askForStart = taskList.size();
        for(AskForLeave mp:askList)
        {  
        	String endStr = mp.BackDate ==null? "起":" 到 "+ mp.BackDate;
        	listItem[askForStart++]=mp.MemberName+" "+ mp.ApplyDate+endStr;
        }
        ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(TaskActivity.this,android.R.layout.simple_list_item_1,listItem );
        listview.setAdapter(listItemAdapter);
        
	}
	/*
	
	private void BindAskListView(){
		ListView listview1 = (ListView) getActivity().findViewById(R.id.task_ask_for_leave_listview);
		String[]  listItem1 = new String[askList.size()];
        for(int i=0;i< askList.size();i++)
        {  				    
        	AskForLeave mp = askList.get(i);
        	String endStr = mp.BackDate ==null? "起":"到"+ mp.BackDate;
        	listItem1[i]=mp.MemberName+" "+ mp.ApplyDate+endStr;
        }  				         
        ArrayAdapter<String> listItemAdapter1 =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listItem1 );
        listview1.setAdapter(listItemAdapter1);
        View emptyView1 = getActivity().findViewById(R.id.month_people_coming_listview_empty_2);
        listview1.setEmptyView(emptyView1);
        listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				AskForLeave ask = askList.get(arg2);
				Intent tent= new Intent();
				Bundle bundle=new Bundle(); 
				bundle.putInt("askID", ask.ID); 
				tent.putExtras(bundle);
				tent.setClass(getActivity(),EditAskForLeaveActivity.class);
				startActivityForResult(tent,IntentTask);
				return false;
			}
		});
	}
	*/
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		 if(resultCode ==  android.app.Activity.RESULT_OK){
			 switch(requestCode){
				 case IntentTask:
					 QueryTask();
					 break;
				 case IntentAskForLeave:
					 QueryAskForLeave();
					 break;
			 }
		 }
		 
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item){
		switch(item.getItemId()){
			case 0:
				//编辑
				if(selectedIndex < taskList.size()){
					Task task = taskList.get(selectedIndex);
					Intent tent= new Intent();
					Bundle bundle=new Bundle(); 
					bundle.putString("taskID", task.ID); 
					tent.putExtras(bundle);
					tent.setClass(TaskActivity.this,EditTaskActivity.class);
					startActivityForResult(tent,IntentTask);
				}else{
					AskForLeave ask = askList.get(selectedIndex -taskList.size() );
					Intent tent= new Intent();
					Bundle bundle=new Bundle(); 
					bundle.putString("askID", ask.ID); 
					tent.putExtras(bundle);
					tent.setClass(TaskActivity.this,EditAskForLeaveActivity.class);
					startActivityForResult(tent,IntentTask);
				}
				break;
				case 1:
				BasicAccess access=new BasicAccess(this);
					String id;
				try
				{
					queryCount=1;
						if(selectedIndex < taskList.size()){
						id= taskList.get(selectedIndex).ID;
						access.Visit(TaskAccess.class).Delete(id);
						QueryTask();
					}else{
						id=askList.get(selectedIndex -taskList.size() ).ID;
						access.Visit(AskForLeaveAccess.class).Delete(id);
						QueryAskForLeave();
					}
				}catch (Exception e)
				{
					ws.Toast(e.getMessage());
					queryCount=0;
				}
				access.Close(true);
					break;
		}
		return super.onContextItemSelected(item);  
	}
	
}

