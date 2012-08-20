package q.util;

import android.util.Log;

public final class QLog {
	
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
	
	public static final void error(Object obj, String msg){
		Log.d("QQ", obj.getClass().getSimpleName() + " " + msg);
	}
}
