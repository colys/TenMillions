package com.colys.tenmillion;

import CustomViews.*;
import android.os.*;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

public class ExecuteSqlActivity extends WSActivity
{
	protected int getLayout() {
		return R.layout.activity_execute_sql;
	}
	EditText txtSql,txtPwd;
	WebView  txtResult;
	Button btnExecute;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		txtSql = (EditText) findViewById(R.id.execute_sql_text);
		txtPwd = (EditText) findViewById(R.id.execute_sql_querypwd);
		txtResult = (WebView) findViewById(R.id.execute_sql_display);
		btnExecute = (Button) findViewById(R.id.execute_sql_button);
		btnExecute.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String sql = txtSql.getText().toString();
					String pwd = txtPwd.getText().toString().trim();
					ws.visitServices("ExecuteSql",new String[]{"sql","pwd"},new String[]{sql,pwd},0);
				}

			
		});
	}
	public void onHandleMessage(Message msg){		
		super.onHandleMessage(msg);
		txtResult.loadData(ws.queryResult,"text/html; charset=UTF-8", null);
	}
	
}
