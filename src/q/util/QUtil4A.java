package q.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public final class QUtil4A {
	
	//=================================== 窗口 =========================================
	protected void Window(){}
	
	/**
	 * 设置为横屏
	 */
	public static final void Window_setOrientationLandscape(Activity act){
		act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
	}
	
	//================================= 操作系统，OS ===================================================
	protected void Os(){}
	
	/**
	 * 获得手机型号
	 */
	public static final String Os_getModel () {
		Build bd = new Build();
		return bd.MODEL;
	}
	
	//======================================= TextView =========================================
	protected void TextView(){}
	
	/**
	 * TextView字体加粗
	 */
	public static void TextView_setFontBold(TextView v){
		v.setTypeface(null, Typeface.BOLD);
	}
	
	//======================================= EditText ========================================
	protected void EditText(){}
	
	/**
	 * 监听EditText的输入
	 */
	public static void EditText_addTextChangedListener(android.widget.EditText et){
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				System.out.println("count:"+count+" str:"+s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	
	//======================================= ScrollView ======================================
	protected void ScrollView(){}
	
}
