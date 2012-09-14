package q.util.os;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

public class QNotificationUtil {

	/**
	 * 显示通知
	 * <br/>需要震动权限<uses-permission android:name="android.permission.VIBRATE" />
	 * @param id ID号，用于识别通知
	 * @param title 标题
	 * @param text 内容
	 * @param intent 跳转
	 */
	public static final void show(Context context, int id, int iconResId, CharSequence title, CharSequence text, PendingIntent intent){
		Notification notif = new Notification(iconResId, text, System.currentTimeMillis());
		notif.setLatestEventInfo(context, title, text, intent);
		notif.defaults = Notification.DEFAULT_ALL;
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify( id, notif);
	}
	
	/**
	 * 隐藏通知
	 * @param id ID号，用于识别通知
	 */
	public static final void cancel(Context context, int id){
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
	}
}
