package q.util.os;

import q.util.QLog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class QWindowManager {
	
	private static QWindowManager instance;
	
	private QWindowManager(){}
	
	public static QWindowManager getInstance(Context ctx){
		if(instance == null){
			synchronized (QWindowManager.class) {
				if(instance == null){
					instance = new QWindowManager();
					instance.init(ctx);
				}
			}
		}
		return instance;
	}
	
	private int width;//宽度分辨率
	private int height;//高度分辨率
	private int dpi;//密度DPI
	private float scale;//缩放倍数，以480x320为一倍
	private float scaleRes;//资源缩放倍数，以480x320为一倍
	private float scaleText;//字体缩放倍数，以480x320为一倍
	
	
	public void init(Context ctx){
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
		QLog.kv(this, "init", "width", width);
		QLog.kv(this, "init", "height", height);
		//
		dpi = dm.densityDpi;
		QLog.kv(this, "init", "dpi", dpi);
		//
		scale = (float)(width / 320.0);//以480x320为一倍
		QLog.kv(this, "init", "scale", scale);
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
		QLog.kv(this, "init", "scaleRes", scaleRes);
		//
		if (dpi == DisplayMetrics.DENSITY_LOW) {
			scaleText = 2f;
		} else if (dpi == DisplayMetrics.DENSITY_MEDIUM) {
			scaleText = 3f / 2f;
		} else {
			scaleText = 1f;
		}
		QLog.kv(this, "init", "scaleText", scaleText);
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
