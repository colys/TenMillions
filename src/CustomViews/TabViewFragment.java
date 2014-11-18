package CustomViews; 
import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.R; 
import DataAccess.BasicAccess;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler; 
import android.support.v4.app.Fragment; 
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.*;
import android.support.v4.app.*;  



public abstract class TabViewFragment extends Fragment {
	
	public boolean isFirstShow = true;
	
	public WSView ws;
	
	protected Activity mActivity;
	
	protected BasicAccess m_Access ;
	
	public TabViewFragment(){
		
	}
	
	public void Init(WSView ws,BasicAccess access){
		this.ws=ws;
		this.m_Access = access;
		handler =ws.handler;		
	}
	
	public void onCreateView(View rootView) {

	}

	@Override
	public void onStart()
	{
		//ws.Toast("on start " +this.getClass().getName());
		super.onStart();
		FirstShow();
	}

	@Override
	public void onStop()
	{
		//ws.Toast("on stop " +this.getClass().getName());
		super.onStop();
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
				isError = false;
				((OnFragmentListener)mActivity).onFragmentCreated(this);
				onCreateView(rootView);
				//ws.Toast("oncreate view "+this.getClass().getName());
				/*
				
			
				FirstShow();
				*/
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
		// ws.Toast("onActivity create "+this.getClass().getName());
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

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mActivity=activity;
	
	}
	
	
	public interface OnFragmentListener{
		public void onFragmentCreated (TabViewFragment f);
		
	    public void onFragmentStart(TabViewFragment f);
	
	}


}
