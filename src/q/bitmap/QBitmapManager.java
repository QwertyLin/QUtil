package q.bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * 管理Bitmap缓存
 *
 */
public class QBitmapManager {
	
	private static QBitmapManager nInstance;
	
	private QBitmapManager(){}
	
	public static QBitmapManager getInstance(){
		if(nInstance == null){
			synchronized (QBitmapManager.class) {
				if(nInstance == null){
					nInstance = new QBitmapManager();
				}
			}
		}
		return nInstance;
	}

	private HashMap<String, SoftReference<Bitmap>> nCache = new HashMap<String, SoftReference<Bitmap>>();
	
	public Bitmap get(String key){
		if(nCache.containsKey(key)){
			Bitmap bm = nCache.get(key).get();
			if(bm != null && !bm.isRecycled()){
				return bm;
			}
		}
		return null;
	}
	
	public void put(String key, Bitmap bm){
		nCache.put(key, new SoftReference<Bitmap>(bm)); 
	}
	
	/**
	 * 退出时调用clear释放图片缓存
	 */
	public void clear(){
		Bitmap bm;
		for(String key : nCache.keySet()){
			bm = nCache.get(key).get();
			if(bm != null && !bm.isRecycled()){
				bm.recycle();
			}
		}
		nCache.clear();
	}
}
