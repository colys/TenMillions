package com.colys.tenmillion;
import java.util.LinkedList;
import com.colys.tenmillion.Entity.House;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import CustomViews.WSActivity;
import DataAccess.BasicAccess;
import DataAccess.HouseAccess;

public class HouseActivity extends WSActivity{
	
	Spinner spnGroups;
	ListView lvHouse;
	Button btnAdd;
	BasicAccess m_access;
	House selectedHouse;
	LinkedList<House> lstHouse;
	private ArrayAdapter<String> adapter;
	private EditText txtName;
	int selectGroupId;
	
	@Override
	protected int getLayout() {return R.layout.activity_house;}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		btnAdd =(Button) findViewById(R.id.house_add_button);
		lvHouse =(ListView) findViewById(R.id.listView_house_list);
		spnGroups =(Spinner) findViewById(R.id.spinner_groups);
		txtName = new EditText(HouseActivity.this);
		txtName.setInputType(InputType.TYPE_CLASS_TEXT);
		
		m_access = new BasicAccess(getApplicationContext());		
		String[] arr = new String[getApp().getCurrentUser().Groups.length];
		for(int i=0;i< arr.length;i++){ arr[i] =  getApp().getCurrentUser().Groups[i].Name;}
		adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arr);  
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
	    spnGroups.setAdapter(adapter);
	    spnGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectGroupId = getApp().getCurrentUser().Groups[arg2].ID;				 
				 BindListView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
	    spnGroups.setSelection(0);
	    btnAdd.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				txtName.setText("");
				new AlertDialog.Builder(HouseActivity.this)
					.setTitle("房子在哪？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(txtName)
					.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							selectedHouse = null;
							String name = txtName.getText().toString();							
							try {
								House house =new House();
								house.Name = name;
								house.GroupID = selectGroupId;
								house.OwnDate = Utility.GetNowString();
								m_access.Visit(HouseAccess.class).Add(house);
							} catch (Exception e) {
								ws.Toast(e.getMessage());
								m_access.Close(false);
								return;
							}
							m_access.Close(true);
							BindListView();
							((ViewGroup) txtName.getParent()).removeView(txtName);
							return;
						}
					})
					.setNegativeButton(R.string.cancel_button_text, null)
					.show();
			}
		});
	    
		lvHouse.setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener(){

			@Override
			public void onCreateContextMenu(ContextMenu p1, View p2, ContextMenu.ContextMenuInfo p3)
			{
				AdapterView.AdapterContextMenuInfo minfo = (AdapterView.AdapterContextMenuInfo) p3;
				if (minfo.position == -1) return;
				selectedHouse = lstHouse.get(minfo.position);
				p1.setHeaderTitle("您想对\""+selectedHouse.Name +"\"什么？");
				p1.add(0, 0, 0, "重命名");
				p1.add(1, 1, 0, "删除");
			}

		
		});
	}
	
	private void BindListView(){
		lstHouse = m_access.Visit(HouseAccess.class).Query(false,selectGroupId);
		String[]  listItem = new String[lstHouse.size()];
		 for(int i=0;i<listItem.length;i++){
			 listItem[i] =  lstHouse.get(i).Name + " "+ ( lstHouse.get(i).OwnDate==null?"":lstHouse.get(i).OwnDate);
		 }
		 ArrayAdapter<String> listItemAdapter =new ArrayAdapter<String>(HouseActivity.this,android.R.layout.simple_list_item_1,listItem );
		 lvHouse.setAdapter(listItemAdapter);
	}
		
		@Override  
	    public boolean onContextItemSelected(MenuItem item){
			switch(item.getItemId()){
				case 0:
				{
					
					txtName.setText(selectedHouse.Name);					
					new AlertDialog.Builder(HouseActivity.this)
						.setTitle("房子在哪？")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(txtName)
						.setPositiveButton(R.string.ok_button_text, new DialogInterface.OnClickListener() {					
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								String name = txtName.getText().toString();
								((ViewGroup) txtName.getParent()).removeView(txtName);
								if(selectedHouse.Name.equals(name)) return;
								try {
									selectedHouse.Name = name;
									m_access.Visit(HouseAccess.class).Update(selectedHouse);
								} catch (Exception e) {
									ws.Toast(e.getMessage());
									m_access.Close(false);
									return;
								}
								m_access.Close(true);
								BindListView();
								return;
							}
						})
						.setNegativeButton(R.string.cancel_button_text, null)
						.show();
					break;
				}
				case 1:{
					try {
						m_access.Visit(HouseAccess.class).Delete(selectedHouse.ID);
					} catch (Exception e) {
						ws.Toast(e.getMessage());
						m_access.Close(false);
						return false;
					}
					m_access.Close(true);
					BindListView();
					break;
				}
		
			}
			return true;
	}
	
	
}
