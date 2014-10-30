package com.colys.tenmillion; 
import CustomViews.*;
import DataAccess.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import com.colys.tenmillion.Entity.*;
import java.util.*; 

public class MonthPlanActivity extends WSActivity
{

	private EditText txtFenE;

	private EditText txtPiShu;

	private MemberSelectDialog memberSelectDialog;

	private LinkedList<Member> memberlst;

	private static final int QueryMembers = 0, QueryPlans =1,UpdatePlan =2;

	private EditText txtRemark;

	public MonthPlanActivity()
	{

	}

	int year,month;

	int selectIndex;


	@Override
	public int getLayout()
	{
		return R.layout.activity_month_plans;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		listview = (ListView) findViewById(R.id.month_plan_listview);
		Button btnAdd = (Button) findViewById(R.id.month_plan_add_button);
		btnAdd.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					selectIndex = -1;
					selectedPlan = new MonthPlan();
					Calendar cal = Calendar.getInstance();
					selectedPlan.Year = cal.get(Calendar.YEAR);
					selectedPlan.Month = cal.get(Calendar.MONTH) + 1;
					memberSelectDialog.Show();
					isAdd = true;
				}


			});
		runQueryMembers();
		runQueryPlan();
	}
	public void runQueryMembers()
	{
		if (Utility.UseLocal)
		{
			String sql ="select * from Member where status > -1";
			EntityDBHelper<Member> helper =new EntityDBHelper<Member>(ws.GetSqlite(), Member.class);
			memberlst = helper.QueryList(sql);
			BindMemberDialog();			
		}
		else
		{
			ws.visitServices("Member_GetAllMemberJson", "tree", "false", QueryMembers);			
		}
	}


	public void runQueryPlan()
	{

		if (Utility.UseLocal)
		{
			String sql="select MonthPlan.*,Name from MonthPlan join Member on Member.ID = MonthPlan.MemberID where groupid='"+getCurrentGroupId()+"' and year =" + year + " and month = " + month;
			EntityDBHelper<MonthPlan> helper =new EntityDBHelper<MonthPlan>(ws.GetSqlite(), MonthPlan.class);
			lst = helper.QueryList(sql);
			BindListView();
		}
		else
		{
			ws.visitServices("Plan_QueryPlansJson", new String[]{"year","month","showAllMember","groupID"}
							 , new String[]{String.valueOf(year),String.valueOf(month),"false",String.valueOf(getCurrentGroupId())}
							 , QueryPlans);
		}
	}

	private void BindMemberDialog()
	{
		if (memberSelectDialog == null)
		{

			memberSelectDialog = new MemberSelectDialog(this,					
				memberlst,
				R.string.please_choose_member,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						selectedPlan.MemberID = memberSelectDialog.SelectedMember.ID;
						selectedPlan.Name = memberSelectDialog.SelectedMember.Name;
						ShowEditDialog();
						isAdd = false;
					}
				});
		}

	}

	@Override
    public void onConfigurationChanged(Configuration newConfig)
	{		
	    super.onConfigurationChanged(newConfig);
    }
	ListView listview ;
	LinkedList< MonthPlan> lst ;
	boolean isAdd ;

	private void BindListView()
	{ 

		String[]  listItem = new String[lst.size()+1];
		int totalFenE = 0;
        for (int i=0;i < lst.size();i++)
        {  				    
        	MonthPlan mp = lst.get(i);
        	if (mp.Remark == null) mp.Remark = "";
        	if (mp.Name == null) mp.Name = "unknow";
			String str = mp.Name + " " ;
			if(mp.FenE > 0) str += mp.FenE + "份 " ;
			if(mp.PiShu > 0) str+= mp.PiShu + "批 " ;
			if(mp.Remark!=null && !mp.Remark.isEmpty()) str+= mp.Remark;  
			listItem[i] = str;
			totalFenE+= mp.FenE;
        }		   
		listItem[listItem.length -1] ="total:"+ totalFenE;
        ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItem);
        listview.setAdapter(listItemAdapter);
        View emptyView = findViewById(R.id.month_plan_coming_listview_empty);
        listview.setEmptyView(emptyView);
		listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
				{ 
					AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
					if(minfo.position < lst.size() ){
					selectIndex = minfo.position;
					selectedPlan = lst.get(minfo.position);
					menu.add(0, 0, 0, "编辑");
					menu.add(1, 1, 1, "删除");
					}
				}
			});
	}
	MonthPlan selectedPlan;
	

	private void ShowEditDialog()
	{
		
			txtFenE = new EditText(this);
			txtPiShu = new EditText(this);
			txtRemark = new EditText(this);
			txtFenE.setHint("fen e");
			txtPiShu.setHint("pi shu");
			txtRemark.setHint("remark");
			txtFenE.setInputType(InputType.TYPE_CLASS_NUMBER);
			txtPiShu.setInputType(InputType.TYPE_CLASS_NUMBER);
			txtRemark.setText(selectedPlan.Remark);
			if (selectedPlan.FenE > 0) txtFenE.setText(String.valueOf(selectedPlan.FenE));
			if (selectedPlan.PiShu > 0) txtPiShu.setText(String.valueOf(selectedPlan.PiShu));
			LinearLayout layout = new LinearLayout(this);
			layout.addView(txtFenE);
			layout.addView(txtPiShu);
			layout.addView(txtRemark);
			layout.setOrientation(LinearLayout.VERTICAL);
			new AlertDialog.Builder(this)
				.setTitle("make plan , input math values")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(layout)
				.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{		
						String tempVal ;
						selectedPlan.Remark = txtRemark.getText().toString();
						tempVal =txtFenE.getText().toString();
						if(tempVal.isEmpty()) tempVal ="0";
						selectedPlan.FenE = Integer.valueOf(tempVal);
						tempVal =txtPiShu.getText().toString();
						if(tempVal.isEmpty()) tempVal ="0";
						selectedPlan.PiShu = Integer.valueOf(tempVal);
						//String json = "[" + selectedPlan.ToJson() + "]";
						//Log.i("tip", json);
						BasicAccess access =new BasicAccess(MonthPlanActivity.this);
						try {
							access.Visit(MonthPlanAccess.class).CompletePlans(year, month, selectedPlan);
						} catch (Exception e) {
							ws.Toast(e.getMessage());
							return;
						}
						if (selectIndex == -1)
						{ 
							lst.add(0, selectedPlan);
						}
						else lst.set(selectIndex, selectedPlan);
						BindListView();
//						ws.visitServices("Plan_CompletePlansJson",
//										 new String[]{"year","month","planJson"},
//										 new String[]{
//											 String.valueOf(selectedPlan.Year),
//											 String.valueOf(selectedPlan.Month),
//											 json
//										 },
//										 UpdatePlan);
						// Plan_CompletePlansJson(int year, int month, string planJson)

					}
				})
				.setNegativeButton(R.string.cancel_button_text, null)
				.show();
	}

	public void onHandleMessage(Message msg)
	{
		super.onHandleMessage(msg);	
		switch (msg.what)
		{
			case QueryMembers:
				memberlst = Member.ListFromJson(ws.queryResult);
				BindMemberDialog();					
				break;
			case QueryPlans:
				lst = MonthPlan.ListFromJson(ws.queryResult);
				BindListView();
				break;
			case UpdatePlan:
				if (selectIndex == -1)
				{ 
					lst.add(0, selectedPlan);
				}
				else lst.set(selectIndex, selectedPlan);
				BindListView();
				break;
		}		

	}

	public boolean onContextItemSelected(MenuItem item)
	{  
		switch (item.getItemId())
		{
			case 0:		
				ShowEditDialog(); 

				/*
				 Intent tent= new Intent();
				 Bundle bundle=new Bundle(); 
				 bundle.putInt("year",selectedPlan.Year); 
				 bundle.putInt("month",selectedPlan.Month); 
				 bundle.putInt("memberID",selectedPlan.MemberID); 
				 tent.putExtras(bundle);
				 tent.setClass(this,EditShenGouActivity.class);
				 startActivityForResult(tent,0); 
				 */
				break;
			case 1:
				 //delete
				 new AlertDialog.Builder(this)      
				 .setTitle(R.string.dialog_tip_title)    
				 .setMessage("确定要删除吗？")    
				 .setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {						
					 @Override
					 public void onClick(DialogInterface dialog, int which) {
						 BasicAccess access=new BasicAccess(MonthPlanActivity.this);
						 try {
							access.Visit(MonthPlanAccess.class).Delete(selectedPlan.Year,selectedPlan.Month,selectedPlan.MemberID);
							lst.remove(selectedPlan);
						} catch (Exception e) {
							ws.Toast(e.getMessage());
						}
						 access.Close(true);						 
						 BindListView();
						 //ws.visitServices("ShenGou_Delete" ,"ID",String.valueOf(selectedShenGou.ID), Delete);
					 }
				 } )  
				 .setNegativeButton(R.string.cancel_button_text, null) 
				 .show(); 
				 
				break;
		}
		return super.onContextItemSelected(item);
	}

}
