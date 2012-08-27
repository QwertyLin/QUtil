package q.util;

import java.util.ArrayList;

import com.umeng.analytics.MobclickAgent;

import q.manager.QWindow;
import android.app.Activity;
import android.app.Application;

public abstract class QAppSp extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.onError(this);
	}

	//窗口 管理器
	
	private QWindow qWindow;

	public QWindow getQWindow() {
		if(qWindow == null){
			qWindow = new QWindow(this); 
		}
		return qWindow;
	}
	
	//文件 管理器
	
	private QFile qFile;

	public QFile getQFile() {
		if(qFile == null){
			qFile = new QFile(this);
		}
		return qFile;
	}
	
	//Activity 管理器
	
	private QActivityCache qActivityCache;
	
	public QActivityCache getQActivityCache() {
		if(qActivityCache == null){
			qActivityCache = new QActivityCache();
		}
		return qActivityCache;
	}
	
	public void removeQActivityCache(){
		qActivityCache = null;
	}

	public static class QActivityCache {

		private ArrayList<Activity> cache = new ArrayList<Activity>();
		
		public void put(Activity act){
			cache.add(act);
		}
		
		public Activity get(int index){
			return cache.get(index);
		}
		
		public void clear(){
			for(Activity act : cache){
				if(act != null){
					act.finish();
				}
			}
		}
	}
}
