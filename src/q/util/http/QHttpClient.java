package q.util.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class QHttpClient {
	
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
	
	
	
	private ExecutorService threadPool;//线程池，不为空表建立线程池
	private String cacheDir; //必须以"/"结尾，不为空表读取缓存
	private boolean checkExist;
		
	/**
	 * @param ctx
	 * @param threadNumber 线程池容量
	 * @param cacheExpire 缓存过期时间，单位为分钟
	 */
	public QHttpClient(Context ctx, int threadNumber, long cacheExpire){
		if(threadNumber > 1){
			this.threadPool = Executors.newFixedThreadPool(threadNumber);
		}
		this.cacheDir = ctx.getCacheDir().getAbsolutePath() + "/";
		//
		log(1);
	}
	
	private void run(Runnable run){
		if(threadPool != null){//线程池
    		threadPool.submit(run);
    	}else{//普通线程
    		new Thread(run).start();
    	}
	}
	
	private boolean checkCache(File cacheFile, long cacheExpire){
		//缓存，如果读取本地缓存，则不开线程请求网络
		if(cacheExpire != 0 && cacheFile.exists() && cacheExpire > new Date().getTime() - cacheFile.lastModified()){
			log(3);
			return true;
    	}
		log(4);
		return false;
	}
    
    public void get(final String url, long cacheTime, final Callback callback){    	
    	final File cacheFile = new File(cacheDir + md5(url));
    	log(6, url, cacheFile);
    	//
    	if(checkCache(cacheFile, cacheTime)){
    		callback.success(cacheFile.getAbsolutePath(), url);
    		return;
    	}
    	//
    	run(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(2000);
				try {
					QHttpClientSimple.getFile(url, cacheFile.getAbsolutePath(), checkExist);
					callback.success(cacheFile.getAbsolutePath(), url);
				} catch (IOException e) {
					e.printStackTrace();
					callback.error(e);
				}
			}
		});
    }
	
	public void setCheckExist(boolean bool){
		this.checkExist = bool;
		log(7);
	};
	
	/**
	 * 删除缓存
	 */
	public final void deleteCache(String url){
		new File(cacheDir + md5(url)).delete();
		log(5, url);
	}
	
	public String getFilePath(String url){
		return cacheDir + md5(url);
	}
    
    /**
	 * MD5编码，如果不支持该编码则返回原始字符
	 */
	private String md5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return str;
		}
		md.update(str.getBytes());
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		return buf.toString();
	}
	
	private void log(int i, Object...obj){
    	if(false){
    		return;
    	}
    	StringBuffer sb = new StringBuffer();
    	switch(i){
    	case 1:
    		if(threadPool == null){
    			sb.append(" 线程=单线程");
    		}else{
    			sb.append(" 线程=多线程");
    		}
    		if(cacheDir == null){
    			sb.append(" 缓存文件夹不合法");
    		}else{
    			sb.append(" 缓存文件夹=" + cacheDir);
    		}
    		break;
    	case 3: 
    		sb.append(" 缓存有效");
    		break;
    	case 4: 
    		sb.append(" 缓存失效");
    		break;
    	case 5: 
    		sb.append(" 删除缓存=" + new File(cacheDir + md5((String)obj[0])));
    		break;
    	case 6: 
    		sb.append(" 目标url=" + (String)obj[0] + " 目标文件=" + ((File)obj[1]).getAbsolutePath());
    		break;
    	case 7: 
    		sb.append(" 比较本地文件与远程文件的大小");
    		break;
    	case 8: QLog.log(""); break;
    	case 9: QLog.log(""); break;
    	case 10: QLog.log(""); break;
    	}
    	QLog.log(sb.toString());
    }
    
}
