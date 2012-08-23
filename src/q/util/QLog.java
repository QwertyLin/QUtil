package q.util;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.util.Log;

public final class QLog {
	
	public static void error(Context ctx, String error){
		log(error);
		MobclickAgent.reportError(ctx, error);
	}
	
	public static void event(){
		//MobclickAgent.onEvent
	}
	
	public static final void log(String msg){
		Log.d("Q", msg);
	}
	
	public static final void log(boolean msg){
		Log.d("Q", String.valueOf(msg));
	}
	
	public static final void log(int msg){
		Log.d("Q", String.valueOf(msg));
	}
	
	public static final void log(StringBuffer sb){
		Log.d("Q", sb.toString());
	}
}
