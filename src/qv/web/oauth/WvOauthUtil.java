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
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import q.QLog;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WvOauthUtil {
	
	public static final int 
		TYPE_SINA_WEIBO = 1, //http://open.weibo.com/
		TYPE_QQ_WEIBO = 2, //http://dev.t.qq.com/
		TYPE_QQ_ZONE = 3, //http://opensns.qq.com/
		TYPE_RENREN = 4;
		
	public static void init(final WvOauthEntity en){
		WebSettings set = en.getWebView().getSettings();
		set.setJavaScriptEnabled(true);
		set.setSupportZoom(true);
		set.setBuiltInZoomControls(true);
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//
		en.getWebView().setWebChromeClient(new WebChromeClient(){
			private boolean isLoadingFinish;
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (!isLoadingFinish && newProgress > 30) {
					isLoadingFinish = true;
					en.getListener().onWvOauthLoadingFinish(en);
				}
			}
		});
		//get auth
		WebViewClient wvc = new WebViewClient() {
			int index = 0;
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				QLog.log(WvOauth.class, "url:" + url);
				Pattern p = Pattern.compile(en.getHandle().getUrlParsePattern());
				Matcher m = p.matcher(url);
				if (m.find() && index == 0) {
					index++;
					en.getListener().onWvOauthAuthing(en);
					CookieManager.getInstance().removeAllCookie();//clean cookie
					en.getWebView().setVisibility(View.GONE);
					en.getHandle().parseUrl(en, m);
				}
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				QLog.log(WvOauth.class, "shouldOverrideUrlLoading url=" + url);
				if(url.contains("error_uri")  //sina
					|| url.contains("checkType=error") //QQ
					|| url.contains("error=login_denied") //renren
				){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		};
		en.getWebView().setWebViewClient(wvc);
		en.getWebView().loadUrl(en.getHandle().getAuthUrl());
	}
	
	public static boolean isTokenExpire(WvOauthToken token){
		long timeRemain = token.getExpireTime() - Calendar.getInstance().getTimeInMillis();
		QLog.log(WvOauthUtil.class, "timeRemain:" + timeRemain + " expire:" + token.getExpireTime());
		if(timeRemain > 0){ //can be used
			return false;
		}else{ //can not be used
			return true;
		}
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
	
	protected static String httpGet(String urlStr) throws IOException {
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
				conn.setConnectTimeout(5000);//
				conn.setReadTimeout(5000);// 
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

}
