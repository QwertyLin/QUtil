package q.util.bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * 管理Bitmap缓存
 *
 */
public class QBitmapManager {
	
	private static QBitmapManager instance;
	
	private QBitmapManager(){}
	
	public static QBitmapManager getInstance(){
		if(instance == null){
			synchronized (QBitmapManager.class) {
				if(instance == null){
					instance = new QBitmapManager();
				}
			}
		}
		return instance;
	}

	private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	
	public Bitmap get(String key){
		if(cache.containsKey(key)){
			Bitmap bm = cache.get(key).get();
			if(bm != null && !bm.isRecycled()){
				return bm;
			}
		}
		return null;
	}
	
	public void put(String key, Bitmap bm){
		cache.put(key, new SoftReference<Bitmap>(bm)); 
	}
	
	/**
	 * 退出时调用clear释放图片缓存
	 */
	public void clear(){
		Bitmap bm;
		for(String key : cache.keySet()){
			bm = cache.get(key).get();
			if(bm != null && !bm.isRecycled()){
				bm.recycle();
			}
		}
		cache.clear();
	}
}
