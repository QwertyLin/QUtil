package q.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import q.util.QCodeUtil;
import q.util.QLog;
import q.util.QStreamUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * 带缓存与线程池的http请求
 *
 */
public class QHttpManager {
	
	private static QHttpManager instance;
	
	private QHttpManager(){}
	
	public static QHttpManager getInstance(Context ctx){
		if(instance == null){
			synchronized (QHttpManager.class) {
				if(instance == null){
					instance = new QHttpManager();
					instance.init(ctx);
				}
			}
		}
		return instance;
	}
	
	private ExecutorService threadPool;//线程池
	private String cacheDir; //必须以"/"结尾
	
	private void init(Context ctx){
		cacheDir = ctx.getCacheDir().getPath() + File.separator;
		threadPool = Executors.newFixedThreadPool(5);//5个线程
		QLog.kv(this, "init", "cacheDir", cacheDir);
	}
	
	private boolean checkCache(String cacheFile, long cacheExpire){
		//缓存，如果读取本地缓存，则不开线程请求网络
		File file = new File(cacheFile);
		if(cacheExpire != 0 && file.exists() && cacheExpire > new Date().getTime() - file.lastModified()){
			QLog.kv(this, "checkCache", "cache available", true);
			return true;
    	}
		QLog.kv(this, "checkCache", "cache available", false);
		return false;
	}
    
    /**
     * @param url
     * @param cacheTime 缓存时间，单位毫秒
     * @param checkExist 是否比较本地缓存文件大小
     * @param callback
     */
    public void get(final String url, final Callback callback){    	
    	
    	final String cacheFile = (callback.getCacheFile() == null) ? cacheDir + QCodeUtil.md5(url) : callback.getCacheFile();
    	QLog.kv(this, "get", "remote", url);
    	QLog.kv(this, "get", "local", cacheFile);
    	//
    	if(checkCache(cacheFile, callback.getCacheTime())){
    		callback.success(cacheFile, url);
    		return;
    	}
    	//
    	threadPool.submit(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(2000);//TODO
				try {
					QHttpUtil.getFile(url, cacheFile, callback.checkExist());
					callback.success(cacheFile, url);
				} catch (IOException e) {
					e.printStackTrace();
					callback.error(e);
				}
			}
		});
    }
	
	/**
	 * 删除缓存
	 */
	public final void deleteCache(String url){
		File file = new File(cacheDir + QCodeUtil.md5(url));
		file.delete();
		QLog.log("ope：删除缓存：" + file.getAbsolutePath());
	}
	
	public String getFilePath(String url){
		return cacheDir + QCodeUtil.md5(url);
	}
	
	private static abstract class Callback {
		
		public enum TYPE {
			FILE, TEXT, BITMAP;
		}
		
		class Holder {
			String url;
			Object obj;
		}
		
		public Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					Holder h = (Holder)msg.obj;
					switch(getReturnType()){
					case FILE:
					case TEXT:
						onCompleted((String)h.obj, h.url);
						break;
					}
					break;
				case 1:
					onError((IOException)msg.obj);
					break;
				}
			};
		};	
		
		protected void success(String file, String url){
			Holder h = new Holder();
			h.url = url;
			switch(getReturnType()){
			case FILE:
				h.obj = file;
				break;
			case TEXT:
				try {
					h.obj = QStreamUtil.toStr(new FileInputStream(file));
				} catch (IOException e) {
					e.printStackTrace();
					error(e);
				}
				break;
			}
			Message msg = handler.obtainMessage();
			msg.what = 0;
			msg.obj = h;
			handler.sendMessage(msg);
		}
		
		void error(IOException e){
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.obj = e;
			handler.sendMessage(msg);
		}
		
		public abstract TYPE getReturnType();
		/**
		 * 缓存时间，单位毫秒
		 */
		protected abstract long getCacheTime();
		/**
		 * 缓存文件路径
		 * @return null表使用默认的手机缓存目录
		 */
		protected abstract String getCacheFile();
		/**
		 * 是否检测比较文件大小
		 */
		protected abstract boolean checkExist();
		public abstract void onCompleted(String str, String url);
		public abstract void onCompleted(Bitmap bm, String url);
		public abstract void onError(IOException e);
	}
	
	public static abstract class CallbackFile extends Callback {
		public abstract void onCompleted(String file, String url);
		@Override
		public TYPE getReturnType() {return TYPE.FILE;}
		@Override
		public void onCompleted(Bitmap bm, String url) {return;}
	}
	
	public static abstract class CallbackText extends Callback {
		public abstract void onCompleted(String text, String url);
		@Override
		public TYPE getReturnType() {return TYPE.TEXT;}
		@Override
		public void onCompleted(Bitmap bm, String url) {return;}
	}
	
	public static abstract class CallbackBitmap extends Callback {
		@Override
		public TYPE getReturnType() {return TYPE.BITMAP;}
		@Override
		public void onCompleted(String str, String url) {return;}
	}
    
}
