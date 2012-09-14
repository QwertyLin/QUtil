package q.util.activity;

import java.util.ArrayList;

import android.app.Activity;

/**
 * Activity管理，可一次finish多个Activity。
 * 
 * 尽量继承本类，如QActivityCache4XXX，使功能更单一。
 */
public class QActivityManager {
	
	private static QActivityManager instance;
	
	private QActivityManager(){}
	
	public static QActivityManager getInstance(){
		if(instance == null){
			synchronized (QActivityManager.class) {
				if(instance == null){
					instance = new QActivityManager();
				}
			}
		}
		return instance;
	}
	
	private ArrayList<Activity> cache = new ArrayList<Activity>();
	
	public void add(Activity act){
		cache.add(act);
	}
	
	public Activity get(int index){
		return cache.get(index);
	}
	
	public void finishAll(){
		for(Activity act : cache){
			if(act != null){
				act.finish();
			}
		}
	}
	
	/**
	 * 因为cache保存了对Activity的引用，当确定不需要退出多个Activity时，应及时调用recycle(), 以便gc。
	 */
	public void recycle(){
		cache.clear();
	}
}
