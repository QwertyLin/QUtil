package q.util;

import java.util.ArrayList;

import com.umeng.analytics.MobclickAgent;

import q.manager.QWindow;
import android.app.Activity;
import android.app.Application;

public class QApp extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.onError(this);
		qWindow = new QWindow(this); 
		qFile = new QFile(this);
	}

	//窗口 管理器
	
	private QWindow qWindow;

	public QWindow getQWindow() {
		return qWindow;
	}
	
	//文件 管理器
	
	private QFile qFile;

	public QFile getQFile() {
		return qFile;
	}
	
	//Activity 管理器
	
	private QActivityCache qActivityCache;
	
	public void initQActivityCache(){
		qActivityCache = new QActivityCache();
	}
	
	public void removeQActivityCache(){
		qActivityCache = null;
	}
	
	public QActivityCache getQActivityCache() {
		return qActivityCache;
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
