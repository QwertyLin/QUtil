package q.util;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.util.Log;

public final class QLog {
	
	private static boolean available = true;
	
	public static void error(Context ctx, String error){
		log(ctx, error);
		MobclickAgent.reportError(ctx, error);
	}
	
	public static void event(){
		//MobclickAgent.onEvent
	}
	
	public static final void kv(String clazz, String method, String key, String value){
		if(available){
			Log.d(clazz + ":" + (method == null ? "" : method), key + " *** " + value);
		}
	}
	
	public static final void kv(Object clazz, String method, String key, String value){
		if(available){
			kv(clazz.getClass().getSimpleName(), method, key, value);
		}
	}
	
	public static final void kv(Object tag, String method, String key, boolean value){
		if(available){
			kv(tag, method, key, String.valueOf(value));
		}
	}
	
	public static final void kv(Object tag, String method, String key, int value){
		if(available){
			kv(tag, method, key, String.valueOf(value));
		}
	}
	
	public static final void kv(Object tag, String method, String key, float value){
		if(available){
			kv(tag, method, key, String.valueOf(value));
		}
	}
	
	public static final void log(String tag, String msg){
		if(available){
			Log.d(tag, msg);
		}
	}
	
	public static final void log(Object tag, String msg){
		if(available){
			Log.d(tag.getClass().getSimpleName(), msg);
		}
	}
	
	@Deprecated
	public static final void log(String msg){
		if(available){
			Log.d("Q", msg);
		}
	}
	
	@Deprecated
	public static final void log(boolean msg){
		if(available){
			log(String.valueOf(msg));
		}
	}
	
	@Deprecated
	public static final void log(int msg){
		if(available){
			log(String.valueOf(msg));
		}
	}
	
	@Deprecated
	public static final void log(StringBuffer sb){
		if(available){
			log(sb.toString());
		}
	}
}
