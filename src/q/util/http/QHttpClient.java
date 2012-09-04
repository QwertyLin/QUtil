package q.util.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import q.util.QLog;
import q.util.http.QHttpUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class QHttpClient {
		
	private interface Callback{
		void onError(IOException e);
	}
	public interface CallbackString extends Callback {
		void onCompleted(String str);
	}
	public interface CallbackStream extends Callback {
		void onCompleted(InputStream stream);
	}
	public interface CallbackBitmap extends Callback {
		void onCompleted(Bitmap bm);
	}
	public interface CallbackBitmapList extends Callback {
		void onCompleted(Bitmap bm, String tag);
	}
	
	private ExecutorService threadPool;//线程池，不为空表建立线程池
	private String cacheDir; //必须以"/"结尾，不为空表读取缓存
	private long cacheExpire; //单位为分钟min
	private boolean checkConnContentLength;
	private Callback callback; private Handler handler;
	
	private long currentTime = new Date().getTime(); //当前时间，可以接受误差，所以只初始化一次
		
	public QHttpClient(Context ctx, int threadNumber, long cacheExpire, final Callback callback){
		if(threadNumber > 1){
			this.threadPool = Executors.newFixedThreadPool(threadNumber);
		}
		this.cacheExpire = cacheExpire;
		this.callback = callback;
		//
		this.cacheDir = ctx.getCacheDir().getAbsolutePath() + "/";
		//
		this.handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					try {
						if(msg.arg1 == 0){
							initCallback(callback, (String)msg.obj, null);
						}else{
							initCallback(callback, (String)msg.obj, msg.getData().getString("tag"));
						}
					} catch (IOException e) {
						callback.onError(e);
					}
					break;
				case 1:
					callback.onError((IOException)msg.obj);
					break;
				}
			};
		};
		//
		log(1);
	}
	
	private void initCallback(Callback callback, String file, String tag) throws IOException{
		if(callback instanceof CallbackString){
			((CallbackString)callback).onCompleted(streamToString(new FileInputStream(file)));
		}else if(callback instanceof CallbackBitmapList){
			((CallbackBitmapList)callback).onCompleted(BitmapFactory.decodeFile(file), tag);
		}else if(callback instanceof CallbackBitmap){
			((CallbackBitmap)callback).onCompleted(BitmapFactory.decodeFile(file));
		}else if(callback instanceof CallbackStream){
			((CallbackStream)callback).onCompleted(new FileInputStream(file));
		}
	}
	
	public long getCacheExpire(){
		return this.cacheExpire;
	}
	
	public void setCacheExpire(long cacheExpire){
		this.cacheExpire = cacheExpire;
		log(2);
	}
	
	public void setCheckConnContentLength(boolean bool){
		this.checkConnContentLength = bool;
		log(7);
	};
	
	public String getFilePath(String url){
		return cacheDir + md5(url);
	}
	
	/**
	 * 删除缓存
	 */
	public final void deleteCache(String url){
		new File(cacheDir + md5(url)).delete();
		log(5, url);
	}
	
	private void run(Runnable run){
		if(threadPool != null){//线程池
    		threadPool.submit(run);
    	}else{//普通线程
    		new Thread(run).start();
    	}
	}
	
	private boolean checkCache(File cacheFile){
		//缓存，如果读取本地缓存，则不开线程请求网络
		if(cacheExpire != 0 && cacheFile.exists() && cacheExpire * 60 * 1000 > this.currentTime - cacheFile.lastModified()){
			log(3);
			return true;
    	}
		log(4);
		return false;
	}
	
    public void  get(final String urlStr){    	
    	get(urlStr, null);
    }
    
    public void  get(final String urlStr, final String tag){    	
    	final File cacheFile = new File(cacheDir + md5(urlStr));
    	log(6, urlStr, cacheFile);
    	//
    	if(checkCache(cacheFile)){
    		if(tag == null){
    			try {
        			initCallback(callback, cacheFile.getPath(), tag);
    			} catch (IOException e) {
    				e.printStackTrace();
    				callback.onError(e);
    			}
    		}else{ //列表要稍微延迟一会，否则findViewWithTag会找不到
    			Message msg = handler.obtainMessage();
				msg.obj = cacheFile.getPath();
				msg.arg1 = 1;
				msg.getData().putString("tag", tag);
				handler.sendMessage(msg);
    		}
    		return;
    	}
    	//
    	run(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				//
				/*if(checkCache(cacheFile)){
					msg.obj = cacheFile.getPath();
					msg.arg1 = tag;
					handler.sendMessage(msg);
		    		return;
		    	}*/
				//
				try {
					getFile(urlStr, cacheFile);
					msg.obj = cacheFile.getPath();
					if(tag != null){
						msg.arg1 = 1;
						msg.getData().putString("tag", tag);
					}
					msg.what = 0;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = 1;
					msg.obj = e;
				} finally{
					handler.sendMessage(msg);
				}
			}
		});
    }
    
    /**
     * 发送HTTP GET请求
     * @param urlStr 如 http://www.baidu.com/
     * @param charset "UTF-8"或"GBK"
     * @return
     * @throws IOException
     */
    public void getFile(String urlStr, File saveFile) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = null;
		if (url.getProtocol().toLowerCase().equals("http")){
			conn = (HttpURLConnection) url.openConnection();
		}else if(url.getProtocol().toLowerCase().equals("https")){
			conn = getHttpsConnection(url);
		}
		//
		conn.setRequestMethod("GET");
		QLog.log(QHttpUtil.toString(conn));
		//
		if(conn.getResponseCode() == 200){
			//文件大小不变时,不更新
			if(this.checkConnContentLength && saveFile.exists() && saveFile.length() == conn.getContentLength()){
				QLog.log("文件无变化");
				return;
			}
			File temp = new File(saveFile.getAbsoluteFile() + ".temp");
			try {
				InputStream in = conn.getInputStream();
				FileOutputStream out = new FileOutputStream(temp);
				byte[] buffer = new byte[1024];
		        int len = 0;		        
		        while((len = in.read(buffer)) != -1){
		        	out.write(buffer, 0, len);
				}
		        //
		       /* if(true){
				throw new IOException();
		        }*/
		        if(!temp.renameTo(saveFile)){
					throw new IOException();
				}
		        //
		        in.close();
		        out.close();
			} catch (IOException e) {
				throw e;
			} finally {
				if (conn != null)
					conn.disconnect();
			}
		}else{
			throw new IOException();
		}
	}
    
    private final String streamToString(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int ret = 0;
		while ((ret = is.read(buf)) > 0) {
			os.write(buf, 0, ret);
		}
		return new String(os.toByteArray());
	}
    
    /**
	 * MD5编码，如果不支持该编码则返回原始字符
	 */
	public String md5(String str) {
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
    		sb.append(" 缓存有效时间=" + cacheExpire + "分钟");
    		break;
    	case 2: 
    		sb.append(" 修改缓存时间为=" + cacheExpire + "分钟");
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
	
	protected void Q(){}
    
    public static InputStream staticGetStream(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = null;
		if (url.getProtocol().toLowerCase().equals("http")){
			conn = (HttpURLConnection) url.openConnection();
		}else if(url.getProtocol().toLowerCase().equals("https")){
			conn = getHttpsConnection(url);
		}
		conn.setRequestMethod("GET");
		//
		if(conn.getResponseCode() == 200){
			try {
				return conn.getInputStream();
			} catch (IOException e) {
				throw e;
			} finally {
				if (conn != null)
					conn.disconnect();
			}
		}else{
			throw new IOException();
		}
	}
    
    
    
    public static HttpsURLConnection getHttpsConnection(URL url) throws IOException {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	    https.setHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	    return https;
	}
    
}
