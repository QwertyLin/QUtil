package qv.web.oauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Calendar;
import java.util.regex.Matcher;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.os.Handler;

public abstract class OauthHandle {

	public abstract int getType();
	public abstract String getAuthUrl();
	public abstract String getUrlParsePattern();
	public abstract void parseUrl(final Context ctx, Matcher m, final OnOauthListener listener);
	
	protected static final int MSG_SUCCESS = 1, MSG_ERROR = 2;
	
	protected Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Holder holder = (Holder)msg.obj;
			switch(msg.what){
			case MSG_SUCCESS:
				TokenSqilte sqlite = new TokenSqilte(holder.ctx);
				sqlite.open(true);
				sqlite.insert(holder.token);
				sqlite.close();
				holder.listener.onWvOauthSuccess(holder.token);
				break;
			case MSG_ERROR:
				holder.listener.onWvOauthError();
				break;
			}
		};
	};
	
	public class Holder {
		public Context ctx;
		public Token token;
		public OnOauthListener listener;
	}
	
	public static boolean isTokenExpire(Token token){
		long timeRemain = token.getExpireTime() - Calendar.getInstance().getTimeInMillis();
		//QLog.log(OauthHandle.class, "timeRemain:" + timeRemain + " expire:" + token.getExpireTime());
		if(timeRemain > 0){ //can be used
			return false;
		}else{ //can not be used
			return true;
		}
	}
	
	protected String httpGet(String urlStr) throws IOException {
    	HttpURLConnection conn = null;
    	InputStream in = null;
		BufferedReader bufferedReader = null;
    	try {
			URL url = new URL(urlStr);
			if (url.getProtocol().toLowerCase().equals("http")){
				conn = (HttpURLConnection) url.openConnection();
			}else if(url.getProtocol().toLowerCase().equals("https")){
				conn = initHttpsConn(url);
			}
			//
			conn.setRequestMethod("GET");
			if(conn.getResponseCode() == 200){
				StringBuffer temp = new StringBuffer();
				in = conn.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
				String line = bufferedReader.readLine();
				while (line != null) {
					//temp.append(line).append("\r\n");
					temp.append(line);
					line = bufferedReader.readLine();
				}
				if(temp.length() != 0){
					return temp.toString();
				}else{
					throw new IOException();
				}
			}else{
				throw new IOException();
			}
    	} catch (IOException e) {
			throw e;
		} finally {
			if(bufferedReader != null){
				bufferedReader.close();
			}
			if(in != null){
				in.close();
			}
			if(conn != null){
				conn.disconnect();
			}
		}
	}
	
	public static String httpPost(String urlStr, String param) throws IOException {
    	return httpPost(urlStr, param, null, null);    	
    }
	
	public static String httpPost(String urlStr, String param, String boundary, String filePath) throws IOException {
    	HttpURLConnection conn = null;
    	OutputStream output = null;
    	try {
    		URL url = new URL(urlStr);
			if (url.getProtocol().toLowerCase().equals("http")){
				conn = (HttpURLConnection) url.openConnection();
			}else if(url.getProtocol().toLowerCase().equals("https")){
				conn = initHttpsConn(url);
			}
			//
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			//
			//Header
			/*if(header != null){
				for(String key : header.keySet()){
					conn.setRequestProperty(key, header.get(key));
				}
			}*/
			//
			if(filePath == null){
				if(param != null){
					output = conn.getOutputStream();
					output.write(param.getBytes());
					output.flush();
				}
			}else{
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);		
				//conn.setRequestProperty("Host", "www.baidu.com");
				//conn.setRequestProperty("Referer", "http://www.baidu.com");
				//conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
				//
				File f = new File(filePath);  
				FileInputStream fileStream = new FileInputStream(f);  
		        byte[] file = new byte[(int)f.length()];  
		        fileStream.read(file); 
				if(param != null){
					output = conn.getOutputStream();
					output.write(param.getBytes());
					output.write(file);
					output.write(("\r\n--" + boundary + "--\r\n").getBytes());  //end
					output.flush();
				}
			}
			//
			if(conn.getResponseCode() == 200){
				StringBuffer temp = new StringBuffer();
				
					InputStream in = conn.getInputStream();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(in, "utf-8"));
					String line = bufferedReader.readLine();
					while (line != null) {
						temp.append(line);
						line = bufferedReader.readLine();
					}
					bufferedReader.close();
					//
					System.out.println("content:"+temp.toString());
				
				if(temp.length() != 0){
					return temp.toString();
				}else{
					throw new IOException();
				}
			}else{
				throw new IOException();
			}
    	} catch (IOException e) {
			throw e;
		} finally {
			if(output != null){
				output.close();
			}
			if (conn != null){
				conn.disconnect();
			}
		}
		
	}
	
	private static HttpsURLConnection initHttpsConn(URL url) throws IOException {
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
	
	protected static String md5(String str) {
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


