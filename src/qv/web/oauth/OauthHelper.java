package qv.web.oauth;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import qv.web.oauth.request.QweiboPostText;
import qv.web.oauth.request.QzonePostText;
import qv.web.oauth.request.RenrenPostText;
import qv.web.oauth.request.SinaPostText;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OauthHelper {
	
	public static final int 
	TYPE_SINA = 1, //http://open.weibo.com/
	TYPE_QWEIBO = 2, //http://dev.t.qq.com/
	TYPE_QZONE = 3, //http://opensns.qq.com/
	TYPE_RENREN = 4;
	
	public OauthHelper(final Context ctx, final OnOauthListener listener, final OauthHandle handle, final WebView webView){
		WebSettings set = webView.getSettings();
		set.setJavaScriptEnabled(true);
		set.setSupportZoom(true);
		set.setBuiltInZoomControls(true);
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//
		webView.setWebChromeClient(new WebChromeClient(){
			private boolean isLoadingFinish;
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (!isLoadingFinish && newProgress > 30) {
					isLoadingFinish = true;
					listener.onWvOauthLoadingFinish();
				}
			}
		});
		//get auth
		WebViewClient wvc = new WebViewClient() {
			int index = 0;
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				//QLog.log(this, "url:" + url);
				Pattern p = Pattern.compile(handle.getUrlParsePattern());
				Matcher m = p.matcher(url);
				if (m.find() && index == 0) {
					index++;
					listener.onWvOauthAuthing();
					CookieManager.getInstance().removeAllCookie();//clean cookie
					webView.setVisibility(View.GONE);
					handle.parseUrl(ctx, m, listener);
				}
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//QLog.log(this, "shouldOverrideUrlLoading url=" + url);
				if(url.contains("error_uri")  //sina
					|| url.contains("checkType=error") //QQ
					|| url.contains("error=login_denied") //renren
				){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		};
		webView.setWebViewClient(wvc);
		webView.loadUrl(handle.getAuthUrl());
	}
	
	public static List<Token> getTokens(Context ctx){
		TokenSqilte sqlite = new TokenSqilte(ctx);
		sqlite.open(false);
		List<Token> list = sqlite.queryAll();
		sqlite.close();
		return list;
	}
	
	public static void deleteTokenByType(Context ctx, int type){
		TokenSqilte sqlite = new TokenSqilte(ctx);
		sqlite.open(true);
		sqlite.deleteByType(type);
		sqlite.close();
	}
	
	public static void postText(int type, Token token, String text, String lat, String lng) throws UnAuthException, IOException{
		switch(type){
		case TYPE_SINA:
			SinaPostText.postText(token, text, lat, lng);
			break;
		case TYPE_QWEIBO:
			QweiboPostText.postText(token, text);
			break;
		case TYPE_QZONE:
			QzonePostText.postText(token, text);
			break;
		case TYPE_RENREN:
			RenrenPostText.postText(token, text);
			break;
		}
	}
	
	

}
