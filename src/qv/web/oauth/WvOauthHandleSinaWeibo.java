package qv.web.oauth;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;

import org.json.JSONObject;

import q.QLog;
import android.os.Message;

public class WvOauthHandleSinaWeibo implements WvOauthHandle {
	
	private static final String 
		CLIENT_ID = "3811434321", 
		CALLBACK_URL = "http://www.poco.cn";
	
	@Override
	public int getType() {
		return WvOauthUtil.TYPE_SINA_WEIBO;
	}

	@Override
	public String getAuthUrl() {
		return "https://api.weibo.com/oauth2/authorize?"
				+ "client_id=" + CLIENT_ID 
				+ "&redirect_uri=" + CALLBACK_URL 
				+ "&response_type=token"
				+ "&display=mobile";
	}
	
	@Override
	public String getUrlParsePattern() {
		return ".+access_token=(.+)&.+expires_in=(.+)&uid=(.+)";
	}

	@Override
	public void parseUrl(final WvOauthEntity en, Matcher m) {
		final OnWvOauthListener callback = en.getListener();
		/*if(true){
			callback.onError();
			return;
		}*/
		final WvOauthToken token = new WvOauthToken();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		token.setId(m.group(3));
		QLog.log(WvOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " uid=" + token.getId());
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			QLog.log(WvOauth.class, "onCheckToken error");
			callback.onWvOauthError(en);
			return;
		}
		if(token.getId() == null || token.getId().equals("")){
			callback.onWvOauthError(en);
			return;
		}
		WvOauthInstance.getInstance().getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Message msg  = WvOauthInstance.getInstance().gethandler().obtainMessage();
					try {
						JSONObject json = new JSONObject(WvOauthUtil.httpGet(urlUsersShow(token)));
						token.setName(json.getString("screen_name"));
						token.setPhoto(json.getString("profile_image_url"));
						QLog.log(WvOauth.class, "screen_name=" + token.getName() + " profile_image_url=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							msg.what = WvOauthInstance.MSG_ERROR;
						}else{
							en.setToken(token);
							msg.what = WvOauthInstance.MSG_SUCCESS;
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = WvOauthInstance.MSG_ERROR;
					} finally {
						msg.obj = en;
						WvOauthInstance.getInstance().gethandler().sendMessage(msg);
					}
					
				}
		});
	}
	
	public static String postText(WvOauthToken token, String text, String lat, String lng) throws WvOauthUnAuthException, IOException {
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		//space repeat
		int space = new Random().nextInt(50);
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < space; i++){
			sb.append(" ");
		}
		String param = "access_token=" + token.getToken() + "&status=" + URLEncoder.encode(text + sb, "utf-8");
		//
		if(lat != null && lng != null){
			param += "&lat=" + lat + "&long=" + lng;
		}
		//
		return WvOauthUtil.httpPost("https://api.weibo.com/2/statuses/update.json", param);
	}
	
	public static String postPic(WvOauthToken token, String text, String pic, String lat, String lng) throws IOException, WvOauthUnAuthException{
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String boundary = "-----114975832116442893661388290519";
		StringBuffer params = new StringBuffer();
		boundary = "\r\n" + "--" + boundary + "\r\n";
		//
		params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
	    params.append(token.getToken());
	    //
	    params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "status" + "\"\r\n\r\n");
		int space = new Random().nextInt(50); //space repeat
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < space; i++){
			sb.append(" ");
		}
	    params.append(text + sb);
	    //
	    if(lat != null && lng != null){
	    	params.append(boundary);
	 		params.append("Content-Disposition: form-data; name=\"" + "lat" + "\"\r\n\r\n");
	 	    params.append(lat);
	 	    //
	 	    params.append(boundary);
	 		params.append("Content-Disposition: form-data; name=\"" + "long" + "\"\r\n\r\n");
	 	    params.append(lng);
	    }
	    //
	    params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "pic" + "\"; filename=\"" + new File(pic).getName() + "\"\r\n");
	    params.append("Content-Type: " + "image/x-png" + "\r\n\r\n");
	    //
		return WvOauthUtil.httpPost("https://upload.api.weibo.com/2/statuses/upload.json", params.toString(), "-----114975832116442893661388290519", pic);
	}
	
	public static String urlUsersShow(WvOauthToken token) {
		return "https://api.weibo.com/2/users/show.json?"
					+ "access_token=" + token.getToken()
					+ "&uid=" + token.getId();
	}
	
	public static String urlFriends(WvOauthToken token, int count, int cursor) throws IOException, WvOauthUnAuthException{
		return "https://api.weibo.com/2/friendships/friends.json?"
					+ "access_token=" + token.getToken()
					+ "&uid=" + token.getId()
					+ "&count=" + count
					+ "&cursor=" + cursor;
	}

}