package q.os;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public final class PowerUtil {
	
	/**
	 * 激活屏幕电源。
	 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
	 * @param ctx
	 * @return
	 */
	public static final WakeLock wake(Context ctx){
		PowerManager pm = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "pm");
        wakeLock.acquire();
        return wakeLock;
	}
	
	/**
	 * 释放屏幕电源。
	 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
	 * @param ctx
	 * @param wakeLock
	 */
	public static final void release(Context ctx, WakeLock wakeLock){
		if(wakeLock != null && wakeLock.isHeld()){
			wakeLock.release();
		}
	}

}
