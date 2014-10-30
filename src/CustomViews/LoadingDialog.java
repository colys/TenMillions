package CustomViews;

import com.colys.tenmillion.R; 

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView; 

public class LoadingDialog {
	
	static Context context;
	
	static Dialog loadingDialog;
	
	static TextView tipTextView;
	
	static ImageView spaceshipImage;

	static Animation hyperspaceJumpAnimation;
	
	static int callOpenCount = 0;
	public static void UpdateMessage(String str){
		if(tipTextView!=null){
			tipTextView.setText(str);
		}
	}
	
	public static void Show(Context context) {  
		Show(context,null);
	}
	/** 
     * �õ��Զ����progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    @SuppressWarnings("deprecation")
	public static void Show(Context context,String msg) {  
        if(context == null) return;
    	callOpenCount ++;
    	if(loadingDialog!=null && loadingDialog.isShowing()){    		
    		return;
    	} 
    	if(msg==null) msg="loading...";
    	LoadingDialog.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.loading_dialog, null);// �õ�����view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// ���ز���  
        // main.xml�е�ImageView  
        spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);// ��ʾ����  
        // ���ض���  
        hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.loading_animation);  
        // ʹ��ImageView��ʾ����  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        
  
        loadingDialog = new Dialog(context, R.style.loading_dialog);// �����Զ�����ʽdialog  
  
        loadingDialog.setCancelable(false);// �������á����ؼ�ȡ��  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));// ���ò���  
        tipTextView.setText(msg);// ���ü�����Ϣ
        loadingDialog.show();
    }  
    
    public static void Close(){    	 
    	if(callOpenCount ==0){
    		callOpenCount = 1;
//    		Toast toast = Toast.makeText(context, "多余的lodingDialog.close", Toast.LENGTH_SHORT); 
//			toast.show();
    	}
	     if(loadingDialog!=null && callOpenCount == 1){
	    	 
	    	 if(loadingDialog.getContext()!=null && loadingDialog.isShowing()) loadingDialog.dismiss();
	     }
    	callOpenCount--;
    }
}
