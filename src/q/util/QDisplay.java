package q.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class QDisplay {
	
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
	
	private static int displayWidth;
	/**
	 * 宽度分辨率
	 */
	public static final int getWidth(Context ctx){
		if(displayWidth == 0){
			init(ctx);
		}
		return displayWidth;
	}
	
	private static int displayHeight;
	/**
	 * 高度分辨率
	 */
	public static final int getHeight(Context ctx){
		if(displayHeight == 0){
			init(ctx);
		}
		return displayHeight;
	}
	
	private static int displayDpi;
	/**
	 * 密度DPI
	 */
	public static final int getDpi(Context ctx){
		if(displayDpi == 0){
			init(ctx);
		}
		return displayDpi;
	}
	
	private static float displayScale;
	/**
	 * 缩放倍数，以480x320为一倍
	 */
	public static final float getScale(Context ctx){
		if(displayScale == 0){
			init(ctx);
		}
		return displayScale;
	}
	
	private static float displayScaleRes;
	/**
	 * 资源缩放倍数，以480x320为一倍
	 */
	public static final float getScaleRes(Context ctx){
		if(displayScaleRes == 0){
			init(ctx);
		}
		return displayScaleRes;
	}
	
	private static float displayScaleText;
	/**
	 * 字体缩放倍数，以480x320为一倍
	 */
	public static final float getScaleText(Context ctx){
		if(displayScaleText == 0){
			init(ctx);
		}
		return displayScaleText;
	}
	
	private static final void init(Context ctx){
		DisplayMetrics dm = new DisplayMetrics(); 
		((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		//
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;
		if(displayWidth > displayHeight) {//高度大于宽度
			int temp = displayWidth;
			displayWidth = displayHeight;
			displayHeight = temp;
		}
		//
		displayDpi = dm.densityDpi;
		//
		displayScale = (float)(displayWidth / 320.0);//以480x320为一倍
		//	
		if (displayDpi < 130) {
			displayScaleRes = 0.75f;
		} else if (displayDpi < 200) {
			displayScaleRes = 1;
		} else if (displayDpi < 320) {
			displayScaleRes = 1.5f;
		} else {
			displayScaleRes = 2f;
		}
		//
		if (displayDpi == DisplayMetrics.DENSITY_LOW) {
			displayScaleText = 2f;
		} else if (displayDpi == DisplayMetrics.DENSITY_MEDIUM) {
			displayScaleText = 3f / 2f;
		} else {
			displayScaleText = 1f;
		}
	}
	
}
