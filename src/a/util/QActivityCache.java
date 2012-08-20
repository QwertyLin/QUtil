package a.util;

import java.util.ArrayList;

import android.app.Activity;

public class QActivityCache {

	private ArrayList<Activity> cache = new ArrayList<Activity>();
	
	public void put(Activity act){
		cache.add(act);
	}
	
	public void clear(){
		for(Activity act : cache){
			if(act != null){
				act.finish();
			}
		}
	}
}
