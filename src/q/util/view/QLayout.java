package q.util.view;

import q.util.R;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class QLayout {
	
	public static class Loading extends FrameLayout {
		
		private TextView tv;

		public Loading(Context ctx) {
			super(ctx);
			View v = ((Activity)ctx).getLayoutInflater().inflate(R.layout.layout_loading, null);
			//
	        LinearInterpolator li = new LinearInterpolator();//匀速效果
	        //
	        RotateAnimation animOut = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	        animOut.setDuration(500);
			animOut.setRepeatCount(Animation.INFINITE);
			animOut.setInterpolator(li);
			v.findViewById(R.id.layout_loading_out).startAnimation(animOut);
			//
			RotateAnimation animIn = new RotateAnimation(359, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	        animIn.setDuration(500);
			animIn.setRepeatCount(Animation.INFINITE);
			animIn.setInterpolator(li);
			v.findViewById(R.id.layout_loading_in).startAnimation(animIn);
			//
			tv = (TextView)v.findViewById(R.id.layout_loading_text);
			tv.setText("连接中");
			//
			this.addView(v);
		}
		
		public void setText(String text){
			tv.setText(text);
		}
	}

}
