package q.manager;

import q.util.QLog;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class QWindow {
	
	private int width;//宽度分辨率
	private int height;//高度分辨率
	private int dpi;//密度DPI
	private float scale;//缩放倍数，以480x320为一倍
	private float scaleRes;//资源缩放倍数，以480x320为一倍
	private float scaleText;//字体缩放倍数，以480x320为一倍
	
	public QWindow(Context ctx){
		DisplayMetrics dm = new DisplayMetrics(); 
		((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		//
		width = dm.widthPixels;
		height = dm.heightPixels;
		if(width > height) {//高度大于宽度
			int temp = width;
			width = height;
			height = temp;
		}
		//
		dpi = dm.densityDpi;
		//
		scale = (float)(width / 320.0);//以480x320为一倍
		//	
		if (dpi < 130) {
			scaleRes = 0.75f;
		} else if (dpi < 200) {
			scaleRes = 1;
		} else if (dpi < 320) {
			scaleRes = 1.5f;
		} else {
			scaleRes = 2f;
		}
		//
		if (dpi == DisplayMetrics.DENSITY_LOW) {
			scaleText = 2f;
		} else if (dpi == DisplayMetrics.DENSITY_MEDIUM) {
			scaleText = 3f / 2f;
		} else {
			scaleText = 1f;
		}
		//
		QLog.log(toString());
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" width=" + width);
		sb.append(" height=" + height);
		sb.append(" dpi=" + dpi);
		sb.append(" scale=" + scale);
		sb.append(" scaleRes=" + scaleRes);
		sb.append(" scaleText=" + scaleText);
		return sb.toString();
	}
	
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getDpi() {
		return dpi;
	}

	public float getScale() {
		return scale;
	}

	public float getScaleRes() {
		return scaleRes;
	}

	public float getScaleText() {
		return scaleText;
	}
	
	

	
	
}
