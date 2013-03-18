package q.util;

import java.io.File;

import q.os.PowerUtil;
import q.os.WindowUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainA extends Activity {
	
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		if(true){
			return;
		}
		
		layout = new LinearLayout(this);
		setContentView(layout);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(this);
		tv.setText("2222222");
		layout.addView(tv);
		
		//powerUtil();
		new Thread(){
			public void run() {
				SystemClock.sleep(200);
				
			};
		}.start();
		
		//finish();
	}
	
	private void powerUtil(){
		final WakeLock wake = PowerUtil.wake(this);
		PowerUtil.release(MainA.this, wake);
	}
	
	private void windowUtil(){
		System.out.println(WindowUtil.getWidth(this) + " " + WindowUtil.getHeight(this) + " " + WindowUtil.getDensity(this) + " " + WindowUtil.getDensityDpi(this));
		System.out.println(WindowUtil.dip2px(this, 100));
		System.out.println(WindowUtil.getStatusBarHeight(this));
	}
	

}
