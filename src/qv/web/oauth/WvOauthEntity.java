package qv.web.oauth;

import android.webkit.WebView;

public class WvOauthEntity {

	private int id;
	private OnWvOauthListener listener;
	private WebView webView;
	private WvOauthHandle handle;
	private WvOauthToken token;
	
	public WvOauthEntity(int id, OnWvOauthListener listener, WebView webView, WvOauthHandle handle) {
		super();
		this.id = id;
		this.listener = listener;
		this.webView = webView;
		this.handle = handle;
	}
	
	public int getId() {
		return id;
	}
	public OnWvOauthListener getListener() {
		return listener;
	}
	public WvOauthHandle getHandle() {
		return handle;
	}
	public WvOauthToken getToken() {
		return token;
	}
	public void setToken(WvOauthToken token) {
		this.token = token;
	}

	public WebView getWebView() {
		return webView;
	}
	
	
	
}
