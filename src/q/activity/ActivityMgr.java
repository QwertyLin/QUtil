package q.activity;

import java.util.ArrayList;

import android.app.Activity;

/**
 * Activity管理，可一次finish多个Activity。
 * 
 * 尽量继承本类，如QActivityCache4XXX，使功能更单一。
 */
public class ActivityMgr {
	
	private static ActivityMgr nInstance;
	
	private ActivityMgr(){}
	
	public static ActivityMgr getInstance(){
		if(nInstance == null){
			synchronized (ActivityMgr.class) {
				if(nInstance == null){
					nInstance = new ActivityMgr();
				}
			}
		}
		return nInstance;
	}
	
	private ArrayList<Activity> nCache = new ArrayList<Activity>();
	
	public void add(Activity act){
		nCache.add(act);
	}
	
	public Activity get(int index){
		return nCache.get(index);
	}
	
	public void finishAll(){
		for(Activity act : nCache){
			if(act != null){
				act.finish();
			}
		}
	}
	
	/**
	 * 因为cache保存了对Activity的引用，当确定不需要退出多个Activity时，应及时调用recycle(), 以便gc。
	 */
	public void recycle(){
		nCache.clear();
	}
}
