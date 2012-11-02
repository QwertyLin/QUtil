package qv.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class EndlessListViewHelper {
	
	public interface OnEndlessListViewListener {
		String onEndlessListViewGetUrl();
		void onEndlessListViewSuccess(String str);
	}
	
	private OnEndlessListViewListener mListener;
	private ListView mListView;
	private View mViewFooter;
	private boolean mIsEnable, mIsDoing;
	
	public EndlessListViewHelper(OnEndlessListViewListener listener, ListView listView, View viewFooter){
		mListener = listener;
		mListView = listView;
		mViewFooter = viewFooter;
		listView.addFooterView(viewFooter);
		mIsEnable = true;
		setEnable(false);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(mIsEnable && !mIsDoing && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
					System.out.println("onScroll");
					mIsDoing = true;
					new Thread(){
						public void run() {
							try {
								Message msg = mHandler.obtainMessage();
								msg.obj = httpGet(mListener.onEndlessListViewGetUrl());
								msg.what = MSG_SUCCESS;
								mHandler.sendMessage(msg);
							} catch (IOException e) {
								e.printStackTrace();
								setEnable(false);
							} 
						};
					}.start();
				}
			}
		});
	}
	
	private static final int MSG_SUCCESS = 1, MSG_ENABLE = 3, MSG_DISABLE = 4;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_SUCCESS:
				mListener.onEndlessListViewSuccess((String)msg.obj);
				mIsDoing = false;
				break;
			case MSG_ENABLE://enable true
				mListView.removeFooterView(mViewFooter);
				mListView.addFooterView(mViewFooter);
				break;
			case MSG_DISABLE://enable false
				mListView.removeFooterView(mViewFooter);
				break;
			}
		};
	};	
	
	public void setEnable(boolean isEnable) {
		if(mIsEnable ^ isEnable){
			mIsEnable = isEnable;
			if(isEnable){
				mHandler.sendEmptyMessage(MSG_ENABLE);
			}else{
				mHandler.sendEmptyMessage(MSG_DISABLE);
			}
			
		}
	}
	
	private static String httpGet(String urlStr) throws IOException {
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
