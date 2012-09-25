package q.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import q.util.QLog;
import q.util.QUtil;
import q.util.code.QCodeUtil;
import q.util.stream.QStreamUtil;
import q.util.thread.QThreadManager;
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
	
	private static QHttpManager nInstance;
	
	private QHttpManager(){}
	
	public static QHttpManager getInstance(Context ctx){
		if(nInstance == null){
			synchronized (QHttpManager.class) {
				if(nInstance == null){
					nInstance = new QHttpManager();
					nInstance.init(ctx);
				}
			}
		}
		return nInstance;
	}
	
	private String nCacheDir; //必须以"/"结尾
	
	private void init(Context ctx){
		nCacheDir = ctx.getCacheDir().getPath() + File.separator;
		QLog.kv(this, "init", "cacheDir", nCacheDir);
	}
	
	private boolean checkCache(File cacheFile, long cacheExpire){
		//缓存，如果读取本地缓存，则不开线程请求网络
		if(cacheExpire != 0 && cacheFile.exists() && cacheExpire > new Date().getTime() - cacheFile.lastModified()){
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
    	
    	final File cacheFile = (callback.getCacheFile() == null) ? new File(nCacheDir + QUtil.code.md5(url)) : new File(callback.getCacheFile());
    	QLog.kv(this, "get", "remote", url);
    	QLog.kv(this, "get", "local", cacheFile.getPath());
    	//
    	if(checkCache(cacheFile, callback.getCacheTime())){
    		callback.success(cacheFile, url);
    		return;
    	}
    	//
    	QUtil.threadM().execute(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(2000);//TODO
				try {
					QUtil.http.getFile(url, cacheFile, callback.checkExist());
					callback.success(cacheFile, url);
				} catch (IOException e) {
					e.printStackTrace();
					callback.error();
				}
			}
		});
    }
	
	/**
	 * 删除缓存
	 */
	public final void deleteCache(String url){
		File file = new File(nCacheDir + QUtil.code.md5(url));
		file.delete();
		QLog.log("ope：删除缓存：" + file.getAbsolutePath());
	}
	
	public String getFilePath(String url){
		return nCacheDir + QUtil.code.md5(url);
	}
	
	private static abstract class Callback {
		
		protected enum TYPE {
			FILE, TEXT, BITMAP;
		}
		
		private class Holder {
			String url;
			Object obj;
		}
		
		private Handler handler = new Handler(){
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
					onError();
					break;
				}
			};
		};	
		
		protected void success(File file, String url){
			Holder h = new Holder();
			h.url = url;
			switch(getReturnType()){
			case FILE:
				if(verify(file)){
					h.obj = file.getPath();
				}else{
					file.delete();
					error();
				}
				break;
			case TEXT:
				try {
					String text = QUtil.stream.toStr(new FileInputStream(file));
					if(verify(text)){
						h.obj = text;
					}else{
						file.delete();
						error();
					}
				} catch (IOException e) {
					e.printStackTrace();
					error();
				}
				break;
			}
			Message msg = handler.obtainMessage();
			msg.what = 0;
			msg.obj = h;
			handler.sendMessage(msg);
		}
		
		void error(){
			Message msg = handler.obtainMessage();
			msg.what = 1;
			handler.sendMessage(msg);
		}
		
		protected abstract TYPE getReturnType();
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
		protected abstract boolean verify(String text);
		protected abstract boolean verify(File file);
		protected abstract void onCompleted(String str, String url);
		protected abstract void onCompleted(Bitmap bm, String url);
		protected abstract void onError();
	}
	
	public static abstract class CallbackFile extends Callback {
		protected abstract void onCompleted(String file, String url);
		@Override
		protected TYPE getReturnType() {return TYPE.FILE;}
		@Override
		protected boolean verify(String text) {return true;};
		@Override
		protected void onCompleted(Bitmap bm, String url) {return;}
	}
	
	public static abstract class CallbackText extends Callback {
		protected abstract void onCompleted(String text, String url);
		@Override
		protected TYPE getReturnType() {return TYPE.TEXT;}
		@Override
		protected boolean verify(File file) {return true;};
		@Override
		protected void onCompleted(Bitmap bm, String url) {return;}
	}
	
	public static abstract class CallbackBitmap extends Callback {
		@Override
		protected TYPE getReturnType() {return TYPE.BITMAP;}
		@Override
		protected void onCompleted(String str, String url) {return;}
	}
    
}
