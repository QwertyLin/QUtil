package q.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import q.QLog;

import android.os.Message;

public class HttpUtil { 
	
    /**
     * @param url
     * @param li
     */
    public static void get(final HttpEntity en){    
    	QLog.kv(HttpUtil.class, "get", "path", en.getCacheFile().getPath());
    	//
    	if(en.getCacheFile().exists()){
			if(en.getCacheTime() == -1 || en.getCacheTime() > Calendar.getInstance().getTimeInMillis() -  en.getCacheFile().lastModified()){
				QLog.kv(HttpUtil.class, "checkCache", "cache available", true);
				getSuccess(en);
				return;
			}
    	}
		QLog.kv(HttpUtil.class, "checkCache", "cache available", false);
    	//
    	HttpInstance.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getFile(en);
					getSuccess(en);
				} catch (IOException e) {
					e.printStackTrace();
					getError(en);
				}
			}
		});
    }
    
    public static void getOnCurrentThread(final HttpEntity en){    
    	QLog.kv(HttpUtil.class, "get", "path", en.getCacheFile().getPath());
    	//
    	if(en.getCacheFile().exists()){
			if(en.getCacheTime() == -1 || en.getCacheTime() > Calendar.getInstance().getTimeInMillis() -  en.getCacheFile().lastModified()){
				QLog.kv(HttpUtil.class, "checkCache", "cache available", true);
				getSuccess(en);
				return;
			}
    	}
		QLog.kv(HttpUtil.class, "checkCache", "cache available", false);
    	//
		try {
			getFile(en);
			getSuccess(en);
		} catch (IOException e) {
			e.printStackTrace();
			getError(en);
		}
    }
    
    private static void getSuccess(HttpEntity en){
    	if(en.getListener().onHttpVerify(en)){
    		Message msg = HttpInstance.getInstance().getHandler().obtainMessage();
    		msg.what = HttpInstance.MSG_SUCCESS;
    		msg.obj = en;
    		HttpInstance.getInstance().getHandler().sendMessage(msg);
		}else{
			en.getCacheFile().delete();
			getError(en);
		}
	}
    
    private static void getError(HttpEntity en){
    	Message msg = HttpInstance.getInstance().getHandler().obtainMessage();
		msg.what = HttpInstance.ERROR;
		msg.obj = en;
		HttpInstance.getInstance().getHandler().sendMessage(msg);
	}
    
    private static void getFile(HttpEntity en) throws IOException {
    	HttpURLConnection conn = null;
    	InputStream in = null;
    	FileOutputStream out = null;
    	try {
    		URL url = new URL(en.getUrl());
    		conn = (HttpURLConnection) url.openConnection();
			//
			conn.setRequestMethod("GET");
			//
			if(conn.getResponseCode() == 200){
				//文件大小不变时,不更新
				if(en.getCacheFile().exists() && en.getCacheFile().length() == conn.getContentLength()){
					QLog.log(HttpUtil.class, "文件无变化");
					return;
				}
				//
				in = conn.getInputStream();
				File temp = new File(en.getCacheFile().getPath() + ".temp");
				out = new FileOutputStream(temp);
				byte[] buffer = new byte[1024];
		        int len = 0;		        
		        while((len = in.read(buffer)) != -1){
		        	out.write(buffer, 0, len);
				}
		        if(temp.length() == 0 || !temp.renameTo(en.getCacheFile())){
					throw new IOException();
				}
			}else{
				throw new IOException();
			}
    	} catch (IOException e) {
			throw e;
		} finally {
			if(out != null){
				out.close();
			}
			if(in != null){
				in.close();
			}
			if (conn != null){
				conn.disconnect();
			}
		}
	}
    
    public static String readFile(File file) {
    	try{
	    	FileInputStream input = new FileInputStream(file);
	        byte[] buf = new byte[1024];
	        int len = -1;
	        ByteArrayOutputStream output = new ByteArrayOutputStream();
	        while ((len = input.read(buf)) != -1) {
	        	output.write(buf, 0, len);
	        }
	        byte[] data = output.toByteArray();
	        output.close();
	        input.close();
	        return new String(data);
    	}catch (Exception e) {
		}
        return null;
   } 
    
    public static String md5(String str) {
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
	
	

}
