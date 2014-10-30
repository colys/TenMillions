package com.colys.tenmillion; 
import com.colys.tenmillion.Entity.Task;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText; 
import CustomViews.DateClickListener;
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.EntityDBHelper;
import DataAccess.TaskAccess;

public class EditTaskActivity extends WSActivity {

	private EditText txtText,txtBegin,txtEnd;
	private CheckBox chkIsfinish;
	private String taskId;
	private Task task;
	
	protected int getLayout()  {
		return R.layout.activity_edit_task;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		txtText = (EditText) findViewById(R.id.task_edit_content);	
		txtBegin = (EditText) findViewById(R.id.task_edit_begin_date);	
		txtEnd = (EditText) findViewById(R.id.task_edit_end_date);	
		chkIsfinish= (CheckBox) findViewById(R.id.task_edit_isfinish);	
		txtBegin.setOnClickListener(new DateClickListener(this));
		txtEnd.setOnClickListener(new DateClickListener(this));
		if(this.getIntent().getExtras()!=null)
			taskId = this.getIntent().getExtras().getString("taskID");
		Button btnSave = (Button) findViewById(R.id.task_edit_save);	
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(task == null) task =new Task();
				task.Text = txtText.getText().toString();
				task.BeginDate = txtBegin.getText().toString();
				task.EndDate= txtEnd.getText().toString();				
				task.IsFinish =chkIsfinish.isChecked();
				if(task.Text.isEmpty()){
					ws.Toast(R.string.please_input_task_content);
					return;
				}
				if(task.BeginDate.isEmpty() || task.EndDate.isEmpty()){
					ws.Toast(R.string.please_choose_task_date_message);
					return;
				}
				BasicAccess access =new BasicAccess(EditTaskActivity.this);
				try {
				if(task.ID == null){
					task.GroupID = getCurrentGroupId();	
					access.Visit(TaskAccess.class).Add(task);
					//ws.visitServices("Task_AddNewJson","taskJson", task.ToJson(), AddTask);
				}
				else
					access.Visit(TaskAccess.class).Update(task);
					//ws.visitServices("Task_UpdateJson","taskJson", task.ToJson(), UpdateTask);
				} catch (Exception e) {
					ws.Toast(e.getMessage());
					access.Close(true);
					return;
				}
				access.Close(true);				
				setResult(RESULT_OK);
				finish();
			}
		});
		if(taskId !=null){
			QueryTask();
		}
	}
	
	private final int QueryTask =0,AddTask =1,UpdateTask =2;
	
	
	private void QueryTask(){
		if(Utility.UseLocal){
			String sql ="select * from Tasks  where ID = '"+ taskId+"'";
			EntityDBHelper<Task> helper =new EntityDBHelper<Task>(ws.GetSqlite(),Task.class);
			task = helper.QuerySingle(sql);
			BindTask();
		}else{ 
			ws.visitServices("Task_GetJson","ID", String.valueOf(taskId), QueryTask);
		}
	}
	
	private void BindTask(){
		txtText.setText(task.Text);
		txtBegin.setText(task.BeginDate);
		txtEnd.setText(task.EndDate);
		chkIsfinish.setChecked(task.IsFinish);
	}
	
	
	

	public void onHandleMessage(Message msg){
		super.onHandleMessage(msg);	
		switch(msg.what){
			case QueryTask:
				task = Task.FromJson(ws.queryResult);
				BindTask();
				break;
			case AddTask:
			case UpdateTask:
				setResult(RESULT_OK);
				finish();
				break;		
		}
	}
}
