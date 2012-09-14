package q.util;

import com.umeng.analytics.MobclickAgent;

import android.app.Application;

public class QAppSp extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.onError(this);
	}
	

}
