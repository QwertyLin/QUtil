package q.util.a;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * Bitmap 缓存
 *
 */
public class QBitmapCache {

	private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	
	public Bitmap get(String key){
		Bitmap bm = null;
		if(cache.containsKey(key)){
			bm = cache.get(key).get();
		}
		if(bm != null && !bm.isRecycled()){
			return bm;
		}
		return null;
	}
	
	public void put(String key, Bitmap bm){
		cache.put(key, new SoftReference<Bitmap>(bm)); 
	}
}
