package q;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;

public class QUtil4SystemService {
	
	//=============================== 相机 =================================================
	protected void Camera(){}
	
	/**
	 * 检查是否有相机
	 */
	public static final boolean Camera_checkHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        return false;
	    }
	}

	//=============================== 震动，Vibrator ==================================
	protected void Vibrator(){}
	
	/**
	 * 振动一次
	 * 权限<uses-permission android:name="android.permission.VIBRATE" /><!-- 使用振动。允许振动 -->
	 * @param milliseconds 振动持续时长
	 */
	public static final void Vibrator_once(Context context, long milliseconds){
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(milliseconds);
	}
	
	/**
	 * 振动多次
	 * 权限<uses-permission android:name="android.permission.VIBRATE" /><!-- 使用振动。允许振动 -->
	 * @param pattern 奇数索引表示等待时长，偶数索引表示震动时长，如 {2000, 1000, 3000, 1000}
	 */
	public static final void Vibrator_manyTimes(Context context, long[] pattern){
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern, -1);
	}
	
	//=================================== 定时器 ============================================
	protected void Alarm(){}
		
	/**
	 * 定时启动Activity
	 * @param atTime 时间点
	 * @param cls 待启动的Activity类
	 */
	public static final void Alarm_activity(Context ctx, long atTime, Class<?> cls){
		((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE))
			.set(AlarmManager.RTC_WAKEUP, atTime, 
					PendingIntent.getActivity(ctx, 0, new Intent(ctx, cls), 0));
	}
	
	/**
	 * 定时启动Service
	 * @param atTime 时间点
	 * @param cls 待启动的Service类
	 */
	public static final void Alarm_service(Context ctx, long atTime, Class<?> cls){
		((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE))
			.set(AlarmManager.RTC_WAKEUP, atTime, 
					PendingIntent.getService(ctx, 0, new Intent(ctx, cls), 0));
	}
	
	/**
	 * 定时发送Broadcast
	 * @param atTime 时间点
	 * @param cls 待启动的Broadcast类
	 */
	public static final void Alarm_broadcast(Context ctx, long atTime, Class<?> cls){
		((AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE))
			.set(AlarmManager.RTC_WAKEUP, atTime, 
					PendingIntent.getBroadcast(ctx, 0, new Intent(cls.getName()), 0));
	}
	
	//================================= 声音 ==========================================
	protected void Audio(){}
	
	/**
	 * 获得手机铃声模式
	 * public static final int AUDIO_MODE_SILENT = 0;
	 * public static final int AUDIO_MODE_VIBRATE = 1;
	 * public static final int AUDIO_MODE_RING = 2;
	 * public static final int AUDIO_MODE_RING_VIBRATE = 3;
	 * 
	 * @param ctx
	 * @return
	 */
	public static final String audioMode(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		switch (audio.getRingerMode()){
			case AudioManager.RINGER_MODE_SILENT:
				return "静音";
			case AudioManager.RINGER_MODE_VIBRATE:
				return "振动";
		}
		if (audio.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER)){
			return "铃声和振动";
		}
		return "铃声";
	}
	
	/**
	 * 获得铃声音量大小
	 * 
	 * @param ctx
	 * @return
	 */
	public static final int audioVolume(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		return audio.getStreamVolume(AudioManager.STREAM_RING);
	}
	
	/**
	 * 降低音量
	 * 
	 * @param ctx
	 */
	public static final void audioVolumeLower(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		audio.adjustVolume(AudioManager.ADJUST_LOWER, 0); 
	}
	
	/**
	 * 提高音量
	 * 
	 * @param ctx
	 */
	public static final void audioVolumeRaise(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		audio.adjustVolume(AudioManager.ADJUST_RAISE, 0); 
	}
	
	/**
	 * 铃声设为正常状态
	 * 
	 * @param ctx
	 */
	public static final void audioModeNormal(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
	
	/**
	 * 铃声设为静音状态
	 * 
	 * @param ctx
	 */
	public static final void audioModeSilent(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}
	
	/**
	 * 铃声设为振动状态
	 * 
	 * @param ctx
	 */
	public static final void audioModeVibrate(Context ctx){
		AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
	}	

}
