package CustomViews;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

public class MyTouchListener implements View.OnLongClickListener {

//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//
//		if(event.getAction()== MotionEvent.ACTION_DOWN){
//			//ClipData data=new ClipData("id");
//			View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
//			v.startDrag(null, shadowBuilder, v, 0);
//			//v.setVisibility(View.INVISIBLE);
//			return true;
//		}
//		else	return false;
//	}

	@Override
	public boolean onLongClick(View v) {
		View.DragShadowBuilder shadowBuilder=new View.DragShadowBuilder(v);
		v.startDrag(null, shadowBuilder, v, 0);
		return true;
	}

}
