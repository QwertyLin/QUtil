package q.util.os;

import q.util.QLog;
import q.util.Q;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class QWindowManager {
	
	private static QWindowManager nInstance;
	
	public static QWindowManager getInstance(Context ctx){
		if(nInstance == null){
			synchronized (QWindowManager.class) {
				if(nInstance == null){
					nInstance = new QWindowManager(ctx);
				}
			}
		}
		return nInstance;
	}
	
	private QWindowManager(Context ctx){
		DisplayMetrics dm = new DisplayMetrics(); 
		((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		//
		nWidth = dm.widthPixels;
		nHeight = dm.heightPixels;
		if(nWidth > nHeight) {//高度大于宽度
			int temp = nWidth;
			nWidth = nHeight;
			nHeight = temp;
		}
		Q.log.kv(this, "init", "width", nWidth);
		Q.log.kv(this, "init", "height", nHeight);
		//
		nDpi = dm.densityDpi;
		Q.log.kv(this, "init", "dpi", nDpi);
		//
		nScale = (float)(nWidth / 320.0);//以480x320为一倍
		Q.log.kv(this, "init", "scale", nScale);
		//	
		if (nDpi < 130) {
			nScaleRes = 0.75f;
		} else if (nDpi < 200) {
			nScaleRes = 1;
		} else if (nDpi < 320) {
			nScaleRes = 1.5f;
		} else {
			nScaleRes = 2f;
		}
		QLog.kv(this, "init", "scaleRes", nScaleRes);
		//
		if (nDpi == DisplayMetrics.DENSITY_LOW) {
			nScaleText = 2f;
		} else if (nDpi == DisplayMetrics.DENSITY_MEDIUM) {
			nScaleText = 3f / 2f;
		} else {
			nScaleText = 1f;
		}
		QLog.kv(this, "init", "scaleText", nScaleText);
	}
	
	private int nWidth;//宽度分辨率
	private int nHeight;//高度分辨率
	private int nDpi;//密度DPI
	private float nScale;//缩放倍数，以480x320为一倍
	private float nScaleRes;//资源缩放倍数，以480x320为一倍
	private float nScaleText;//字体缩放倍数，以480x320为一倍

	public int getWidth() {
		return nWidth;
	}

	public int getHeight() {
		return nHeight;
	}

	public int getDpi() {
		return nDpi;
	}

	public float getScale() {
		return nScale;
	}

	public float getScaleRes() {
		return nScaleRes;
	}

	public float getScaleText() {
		return nScaleText;
	}
	
	

	
	
}
