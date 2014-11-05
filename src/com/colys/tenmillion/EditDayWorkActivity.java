package com.colys.tenmillion;

import java.util.LinkedList;

import com.colys.tenmillion.Entity.DayWorkDetail;
import com.colys.tenmillion.Entity.DayWorkHouse;
import com.colys.tenmillion.Entity.House;
import com.colys.tenmillion.Entity.Member;
import com.colys.tenmillion.Entity.PeopleComing;
import com.colys.tenmillion.Entity.PeopleWorking;
import com.colys.tenmillion.Entity.Task;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button; 
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView; 
import CustomViews.DayWorkListAdapter;
import CustomViews.IPopMemberWorkCallback; 
import CustomViews.MemberFilter;
import CustomViews.MemberViewAdapter; 
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.DayWorkDetailAccess;
import DataAccess.DayWorkHouseAccess;
import DataAccess.HouseAccess;
import DataAccess.PeopleComingAccess;
import DataAccess.PeopleWorkingAccess;

public class EditDayWorkActivity extends WSActivity{
	
	int temp;
	private ListView txtMember;
	private LinkedList<Member> memberlst;
	private ViewGroup membersLayout;
	LinkedList<House> houseList;
	EditText txtRemark,txtQuickText;
	BasicAccess m_Access;
	private Spinner txtHouse;
	DayWorkHouse selectDayWorkHouse;
	private LinkedList<DayWorkHouse> houseWorkList;	
	private String queryDateStr,queryHouse;
	PopMemberDayWorkActivity memberWorkDialog;
	private LinearLayout workingListLayout;
	final int OpenEditPeopleWorking = 5,ChoosePeopleComing =6;
	private ImageButton delView;
	boolean hasChange = false;
	private MemberViewAdapter memberAdapter;
	private MemberFilter memberFilter;
	
	protected int getLayout()  {
		return R.layout.activity_edit_daywork;
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(this.getIntent().getExtras()==null) return;
		queryDateStr = this.getIntent().getExtras().getString("workDay");
		queryHouse = this.getIntent().getExtras().getString("houseID");
		workingListLayout = (LinearLayout) findViewById(R.id.edit_daywork_people_working_list);
		
		membersLayout = (LinearLayout) findViewById(R.id.edit_daywork_details);
		txtRemark = (EditText) findViewById(R.id.edit_daywork_house_remark);
		m_Access =new BasicAccess(EditDayWorkActivity.this);	
		memberlst =	ws.QueryLocalMember(false,true,m_Access);
		txtRemark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(!arg1){
					SaveRemark();
				}
			}
		});
		TextView tvAddComing = (TextView) findViewById(R.id.edit_daywork_people_working_add_coming);
		tvAddComing.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent= new Intent();
				intent.setClass(EditDayWorkActivity.this, ChoosePeopleComingActivity.class);
				intent.putExtra("workdate", queryDateStr);				
				intent.putExtra("house", selectDayWorkHouse.HouseID);
				startActivityForResult(intent, ChoosePeopleComing);
			}
		});		
		txtQuickText = (EditText) findViewById(R.id.edit_daywork_quick_text);
		txtMember = (ListView) findViewById(R.id.edit_daywork_quick_add_member);	
		txtMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Member m = memberAdapter.memberList.get(arg2);
				for(DayWorkDetail i :selectDayWorkHouse.Works){
					if(i.MemberID.equals(m.ID)){
						ws.Toast("already exists!");
						return;
					}
				}
				DayWorkDetail d;
				try {
					String dwhid= selectDayWorkHouse.ID;
					d = m_Access.Visit(DayWorkDetailAccess.class).Get(queryDateStr, m.ID);
					if(d == null){
						d=new DayWorkDetail();
						d.MemberID= m.ID;
						d.MemberName = m.Name;
						d.WDHID = dwhid;
						m_Access.Visit(DayWorkDetailAccess.class).Add(d);						
					}else{
						d.WDHID = dwhid;
						m_Access.Visit(DayWorkDetailAccess.class).Update(d);
						//delete from old dayworkhouse worklist
						for(DayWorkHouse dwh : houseWorkList){
							for(DayWorkDetail detail : dwh.Works){
								if(detail.ID.equals(d.ID)){
									dwh.Works.remove(detail);
									break;
								}
							}
						}
					}
					//add to dayworkhouselist
					selectDayWorkHouse.Works.add(d);
					hasChange = true;
				} catch (Exception e) {
					ws.Toast(e.getMessage());
					e.printStackTrace();
					m_Access.Close(false);
					return;
				}
				m_Access.Close(true);
				AddMemberButton(d);
				txtQuickText.setText("1");
				OnBackspaceKey();
				
			}
		});
		memberAdapter=new MemberViewAdapter(this.getApplicationContext(),memberlst);	
		memberFilter = ((MemberFilter) memberAdapter.getFilter());
		
		Button btnKey1= (Button) findViewById(R.id.edit_daywork_quick_button1);
		Button btnKey2= (Button) findViewById(R.id.edit_daywork_quick_button2);
		Button btnKey3= (Button) findViewById(R.id.edit_daywork_quick_button3);
		Button btnKey4= (Button) findViewById(R.id.edit_daywork_quick_button4);
		Button btnKey5= (Button) findViewById(R.id.edit_daywork_quick_button5);
		Button btnKey6= (Button) findViewById(R.id.edit_daywork_quick_button6);
		Button btnKey7= (Button) findViewById(R.id.edit_daywork_quick_button7);
		Button btnKey8= (Button) findViewById(R.id.edit_daywork_quick_button8);
		Button btnKey9= (Button) findViewById(R.id.edit_daywork_quick_button9);
		
		Button[] btnArray =new Button[]{btnKey1,btnKey2,btnKey3,btnKey4,btnKey5,btnKey6,btnKey7,btnKey8};
		for(temp=0;temp< btnArray.length  ; temp++){
			btnArray[temp].setTag(String.valueOf(temp+1) );
			btnArray[temp].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {	
					String str = txtQuickText.getText().toString()+arg0.getTag().toString() ;
					txtQuickText.setText( str );
					memberFilter.FilterMember(str);		
					ReBindListView();
				}
			});
		}
		
		btnKey9.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				OnBackspaceKey();
			}
		});
		
		delView = (ImageButton) findViewById(R.id.edit_daywork_delete);
		delView.setBackgroundColor(this.getResources().getColor(R.color.drag_exited)); 
		delView.setOnDragListener(new View.OnDragListener(){

			@Override
			public boolean onDrag(View v, DragEvent event) {
				int action = event.getAction();
				View view =(View) event.getLocalState();
				switch(action){
				case DragEvent.ACTION_DRAG_ENDED:
					view.setVisibility(0);
					delView.setVisibility(4);
					break;
				case DragEvent.ACTION_DRAG_ENTERED:
					v.setBackgroundColor(v.getResources().getColor(R.color.drag_enter));
					break;
				case DragEvent.ACTION_DRAG_EXITED:
					v.setBackgroundColor(v.getResources().getColor(R.color.drag_exited)); 
					break;
				case DragEvent.ACTION_DROP:
					//ClipData data = event.getClipData();
										
					ViewGroup owner=(ViewGroup) view.getParent();
					if((LinearLayout)owner != v){
						DayWorkDetail item = (DayWorkDetail) view.getTag();
						owner.removeView(view);						
						try {
							DayWorkHouse emptyDWH = m_Access.Visit(DayWorkDetailAccess.class).GetOrCreateDayWorkHouse(House.Empty_House_Guid, queryDateStr,getApp().getCurrentGroupID());
							item.WDHID = emptyDWH.ID;
							m_Access.Visit(DayWorkDetailAccess.class).Update(item);
							selectDayWorkHouse.Works.remove(item);
							int isExists = -1;
							for(int i=0;i< houseWorkList.size();i++ ){
							 if(houseWorkList.get(i) .HouseID.equals(House.Empty_House_Guid)) {
								 isExists = i;
								 break;
							 }
							}
							if(isExists ==-1){
								m_Access.Visit(DayWorkDetailAccess.class).QueryHouseInfo(emptyDWH,getApp().getCurrentGroupID());
								houseWorkList.add(emptyDWH);								
							}else{
								houseWorkList.get(isExists).Works.add(item);
							}
							hasChange = true;
						} catch (Exception e) {
							ws.Toast(e.getMessage());
							e.printStackTrace();
						}
						m_Access.Close(true);
					}
					delView.setVisibility(View.INVISIBLE);			
					break;
				}
				return true;
			}
			
		});
		
		try {
			houseWorkList = m_Access.Visit(DayWorkDetailAccess.class).QueryHouseWorks(queryDateStr,getCurrentGroupId());
		
		
			houseList=m_Access.Visit(HouseAccess.class).Query(true,getCurrentGroupId());
			BindHouse();
			memberWorkDialog = new PopMemberDayWorkActivity(this,new IPopMemberWorkCallback() {
				
				@Override
				public void Callback(View v, DayWorkDetail dw) {
					//v.setTag(dw);
					try { 
						m_Access.Visit(DayWorkDetailAccess.class).Update(dw);	
						hasChange = true;
					} catch (Exception e) {
						ws.Toast(e.getMessage());
					}
					m_Access.Close(true);
					((Button)v).setText(Html.fromHtml(DayWorkListAdapter.FormatMemberWork(dw)));			
					 
				}
			});
		
		} catch (Exception e) {
			ws.Toast(e.getMessage());
			e.printStackTrace();
		}
		
		m_Access.Close(true);
		
	}
	
	public void SaveRemark(){
		if(selectDayWorkHouse == null) return;
		String remark = txtRemark.getText().toString();					 
		if(!remark.equals(selectDayWorkHouse.Remark)){
			selectDayWorkHouse.Remark = remark;
			try {
				m_Access.Visit(DayWorkHouseAccess.class).Update(selectDayWorkHouse);
				hasChange = true;
			} catch (Exception e) {
				ws.Toast(e.getMessage());
				e.printStackTrace();
			}
			m_Access.Close(true);
		}
	}
	
	private void ReBindListView(){
		LinkedList<String> listItem = new LinkedList<String>();
        for(int i=0;i< memberAdapter.memberList.size();i++)
        {  				    
        	listItem.add(memberAdapter.memberList.get(i).Name);
        }
		ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItem );
		txtMember.setAdapter(listItemAdapter);
                
	}
	
	private void OnBackspaceKey(){
		String str = txtQuickText.getText().toString();
		if(str.isEmpty()) return;
		str = str.substring(0,str.length() -1);
		txtQuickText.setText(str);
		memberFilter.FilterMember(str);	
		ReBindListView();
	}
	
	private void BindHouse(){
		String[] arr = new String[houseList.size()];
		for (int i=0;i < houseList.size();i++) arr[i] = houseList.get(i).Name;
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		txtHouse= (Spinner) findViewById(R.id.edit_daywork_house);
		txtHouse.setAdapter(adapter);
		txtHouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			
			
			@Override
			public void onItemSelected(AdapterView<?> arg0,
									   View arg1, int arg2, long arg3)
			{
				SaveRemark();
				String houseID = houseList.get(arg2).ID;
				selectDayWorkHouse= null;
				for(DayWorkHouse item:houseWorkList){
					if(item.HouseID.equals(houseID)) selectDayWorkHouse = item;
				}
				if(selectDayWorkHouse ==null){
					//not init house
					try
					{
						m_Access.OpenTransConnect();
						selectDayWorkHouse= m_Access.Visit(DayWorkDetailAccess.class).GetOrCreateDayWorkHouse(houseID, queryDateStr,getApp().getCurrentGroupID());
						m_Access.Visit(DayWorkDetailAccess.class).QueryHouseInfo(selectDayWorkHouse,getApp().getCurrentGroupID());
						houseWorkList.add(selectDayWorkHouse);
					}
					catch (Exception e)
					{
						m_Access.Close(false);
						ws.Toast(" selectDayWorkHouse is null and init error:"+e.getMessage());
						return;
					}
					m_Access.Close(true);
					
				}
				
				//load people working
				LoadPeopleComingInfo();
				//load details
				if(selectDayWorkHouse.Remark==null) selectDayWorkHouse.Remark="";
				txtRemark.setText(selectDayWorkHouse.Remark);
				membersLayout.removeAllViews();
				if(selectDayWorkHouse.Works!=null){
					for(DayWorkDetail d:selectDayWorkHouse.Works){	
						AddMemberButton(d);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				selectDayWorkHouse = null;
			}

		}
	);
		if(queryHouse!=null){
			for(int i =0;i< houseList.size();i++){
				if(houseList.get(i).ID.equals(queryHouse)){
					txtHouse.setSelection(i);
				}
			}
		}
	}
	
	private void LoadPeopleComingInfo(){
		workingListLayout.removeAllViews();
		if(selectDayWorkHouse.PeopleComingList!=null){
			for(PeopleComing pc : selectDayWorkHouse.PeopleComingList){
				TextView view = new TextView(getApplicationContext());
				view.setText(Html.fromHtml(DayWorkListAdapter.FormatPeopleWorking(pc)));
				view.setTag(pc);
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						PeopleComing pc = (PeopleComing) arg0.getTag();
						Intent tent= new Intent();
						Bundle bundle=new Bundle();
						bundle.putString("comingID", pc.ID);
						tent.putExtras(bundle);
						tent.setClass(EditDayWorkActivity.this, PeopleWorkingActivity.class);
						startActivityForResult(tent,OpenEditPeopleWorking);
					}
				});
				workingListLayout.addView(view);
			}
		}
	}
	
	private void AddMemberButton(DayWorkDetail item){
		Button btn = new Button(getApplicationContext());
		btn.setText(Html.fromHtml(DayWorkListAdapter.FormatMemberWork(item)));
		btn.setTag(item);
		btn.setBackgroundResource(R.drawable.buttonstyle);
		btn.setMaxWidth(500);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DayWorkDetail item = (DayWorkDetail) arg0.getTag();
				memberWorkDialog.Show(arg0);
				memberWorkDialog.setValue(item);
			}
		});
		btn.setOnLongClickListener(new View.OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				String btnViewText =  ((Button) v).getText().toString();
				ClipData.Item item= new ClipData.Item(v.getTag().toString());
				String[] mineTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN, ClipDescription.MIMETYPE_TEXT_INTENT, ClipDescription.MIMETYPE_TEXT_URILIST};
				ClipData dragData= new ClipData( btnViewText,mineTypes,item);
				android.view.View.DragShadowBuilder  myShadow= new android.view.View.DragShadowBuilder(v);
				 v.startDrag(dragData,  // the data to be dragged
	                        myShadow,  // the drag shadow builder
	                        v,      // no need to use local data
	                        0          // flags (not currently used, set to 0)
	            );
				v.setVisibility(4);
				delView.setVisibility(0);
				return false;
			}
			
		});

		membersLayout.addView(btn);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == android.app.Activity.RESULT_OK)
		{
			if(requestCode == OpenEditPeopleWorking){ 
				selectDayWorkHouse.PeopleComingList = this.m_Access.Visit(PeopleComingAccess.class).GetWorking(selectDayWorkHouse.HouseID, queryDateStr,getApp().getCurrentGroupID());
				LoadPeopleComingInfo();
				m_Access.Close(true);
			} else if(requestCode == ChoosePeopleComing){ 
				String comingid = data.getExtras().get("comingid").toString();				
				for(PeopleComing pc : selectDayWorkHouse.PeopleComingList){
					if(pc.ID.equals(comingid)){
						ws.Toast("已经存在!");
						return;
					}
				}
				try {
					PeopleComing pComing = m_Access.Visit(PeopleComingAccess.class).Get(comingid);
					 if(pComing.Status ==0){
						 pComing.HouseID = selectDayWorkHouse.HouseID;
						 pComing.Status = 1;
						 pComing.ArriveDate = queryDateStr;						 
						 
							m_Access.Visit(PeopleComingAccess.class).Update(pComing);
							PeopleWorking pw = new PeopleWorking();
							pw.ComingID = pComing.ID;
							pw.DayCount = 1;
							pw.HouseID = pComing.HouseID;
							m_Access.Visit(PeopleWorkingAccess.class).Add(pw);
							//if(pComing.Workings ==null) pComing.Workings = new LinkedList<PeopleWorking>();
							//pComing.Workings.add(pw);							
						
					 }else{
						 //修改房子
						 pComing = null;
						 for(DayWorkHouse dwh : houseWorkList){
							 for( PeopleComing pc :  dwh.PeopleComingList){
								 if(pc.ID.equals(comingid)){
									 pComing = pc;
									 dwh.PeopleComingList.remove(pc);
									 m_Access.Visit(PeopleWorkingAccess.class).UpdateHouseByDate(pc.ID, queryDateStr, selectDayWorkHouse.HouseID);
									 break;
								 }
							 }
						 }
						 if(pComing==null){ 
							 ws.Toast("error:can't find coming in loaded list");
							 return;
						 }
					}
					 selectDayWorkHouse.PeopleComingList.add(pComing);
					 LoadPeopleComingInfo();
					 
				} catch (Exception e) {
					ws.Toast(e.getMessage());
				}
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			SaveRemark();
			if(hasChange) setResult(RESULT_OK);
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
}
