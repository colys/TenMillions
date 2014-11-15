package CustomViews; 
import com.colys.tenmillion.MainActivity;
import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.R; 
import DataAccess.BasicAccess;
import android.app.Activity; 
import android.os.Bundle;
import android.os.Handler; 
import android.os.Message;
import android.support.v4.app.Fragment; 
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;  



public abstract class TabViewFragment extends Fragment {
	
	public boolean isFirstShow = true;
	
	public WSView ws;
	
	public TabViewFragment(){
		
	}
	
	public void Init(WSView ws){
		this.ws=ws;
		handler =ws.handler;		
	}
	
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){	
		super.onCreateView(inflater, container, savedInstanceState);
		boolean isError = true;	
		View rootView;
		int layout = getLayout();
		if(layout > -1) {
			isFirstShow = true;	
			rootView = inflater.inflate(layout,	container, false);
			if(rootView != null){				
				m_Access =new BasicAccess(rootView.getContext());				
				isError = false;
				onCreateView(rootView);
				return rootView;
			}
		} 
		if(isError)
		{ 
			 
			rootView = inflater.inflate(R.layout.empty_list_view,	container, false); 
			TextView tv = (TextView) rootView.findViewById(R.id.empty_list_view_label);
			tv.setText("layout not defind or root is null");
			isFirstShow = false;	
			return rootView;
		}	
		else return null;
	}
	 @Override  
	 public void onActivityCreated(Bundle savedInstanceState) {  
	        super.onActivityCreated(savedInstanceState);  
	        
	 }	

	 
	public void onCreateView(View rootView) {
		
	}
	 
	MyApplication mMyApplication=null;
	
	public MyApplication getApp(){
		if(mMyApplication == null) mMyApplication = (MyApplication) getActivity().getApplication();
		return mMyApplication;
	}
	
	public int getCurrentGroupId(){
		return getApp().getCurrentGroupID();
	}
		
	
		
	protected abstract int getLayout();
		
	
	Handler handler ;
		
	public Handler GetMessageHandler(){ return handler;}
	
	public void FirstShow(){
		isFirstShow = false;
	}
	
	BasicAccess m_Access ;
	
	public void onContextMenuClosed(Menu menu){
		 
	 }
}
