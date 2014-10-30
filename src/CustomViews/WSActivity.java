package CustomViews;
 
import com.colys.tenmillion.MyApplication;
import com.colys.tenmillion.R; 

import android.app.Activity;
import android.os.Bundle; 
import android.os.Message;
import android.widget.TextView; 

public class WSActivity extends Activity {
	
	MyApplication mMyApplication=null;
	
	public MyApplication getApp(){
		if(mMyApplication == null) mMyApplication = (MyApplication) getApplication();
		return mMyApplication;
	}
	
	public int getCurrentGroupId(){
		return getApp().getCurrentGroupID();
	}
	
	protected WSView ws =new WSView();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ws.setActivity(this);
		try {
			this.setContentView(getLayout());
		} catch (Exception e) {
			this.setContentView(R.layout.empty_list_view);
			TextView tv = (TextView) findViewById(R.id.empty_list_view_label);
			tv.setText(e.getMessage());
			e.printStackTrace();
		}
		ws.CreateHandle(new android.os.Handler.Callback(){
			@Override
			public boolean handleMessage(Message msg) {
				onHandleMessage(msg);
				return false;
			}
		
		});
		ws.WsErrorCallback = new android.os.Handler.Callback(){
			@Override
			public boolean handleMessage(Message msg) {
				 
				onHandleErrorMessage(msg);
				return false;
			}
		
		};
	}
	
	protected int getLayout() throws Exception {
		throw new Exception("Layout is not defind");
	}
	
	
	
	
	
	//ws error return
	public void onHandleErrorMessage(Message msg){
		
	}
		
	 
	public void onHandleMessage(Message msg){		
//		switch(msg.what){
//		case Utility.DOWN_START:			
//			break;
//		case Utility.DOWN_POSITION:
//			LoadingDialog.UpdateMessage(Utility.GetPercent(msg.arg1,msg.arg2));
//		break;
//		case Utility.DOWN_COMPLETE:
//			LoadingDialog.Close();
//			//����Ϊ����ģʽ
//			 MainActivity.SetAsOffline(true); 
//			break;
//		case Utility.Down_ERROR:
//			LoadingDialog.Close();
//			Toast toast = Toast.makeText(this, "����ʧ��", Toast.LENGTH_SHORT); 
//			toast.show();
//			break;
//		}

	}
	
	
}



