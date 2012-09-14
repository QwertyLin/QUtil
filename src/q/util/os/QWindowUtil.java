package q.util.os;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class QWindowUtil {
	
	/**
	 * 设置为无标题栏，必须在setContentView之前调用
	 */
	public static final void setNoTitle(Activity act){
		act.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	}
	
	/**
	 * 设置为全屏模式
	 */
	public static final void setFullScreen(Activity act){
		act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/**
	 * 设置屏幕保持唤醒状态
	 */
	public static final void setScreenKeepOn(Activity act){
		act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

}
