package CustomViews;
import com.colys.tenmillion.R; 
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyDragListener implements View.OnDragListener  {
	
	IDragCallback callback;
	
	public MyDragListener(IDragCallback back){
		callback = back;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		int action = event.getAction();
		switch(action){
		case DragEvent.ACTION_DRAG_STARTED:			
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			v.setBackgroundColor(v.getResources().getColor(R.color.drag_enter));
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			v.setBackgroundColor(v.getResources().getColor(R.color.drag_exited));
			break;
		case DragEvent.ACTION_DROP:
			//ClipData data = event.getClipData();
			View view =(View) event.getLocalState();
			ViewGroup owner=(ViewGroup) view.getParent();
			if((LinearLayout)owner != v){
				owner.removeView(view);
				if(v.getClass()==LinearLayout.class  ){
					LinearLayout container =(LinearLayout) v;
					container.addView(view);
					callback.DragOK(view,v);
					
					
					return true;
				}else{
					
				}
			}
			//view.setVisibility(View.VISIBLE);			
			break;
		}
		return true;
	}

}
