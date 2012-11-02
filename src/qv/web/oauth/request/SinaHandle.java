package qv.web.oauth.request;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONObject;

import qv.web.oauth.OauthHandle;
import qv.web.oauth.OauthHelper;
import qv.web.oauth.UnAuthException;
import qv.web.oauth.OnOauthListener;
import qv.web.oauth.Token;
import qv.web.oauth.OauthHandle.Holder;

import android.content.Context;
import android.os.Message;

public class SinaHandle extends OauthHandle {
	
	private static final String 
		CLIENT_ID = "3811434321", 
		CALLBACK_URL = "http://www.poco.cn";
	
	@Override
	public int getType() {
		return OauthHelper.TYPE_SINA;
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
	public void parseUrl(final Context ctx, Matcher m, final OnOauthListener listener) {
		/*if(true){
			callback.onError();
			return;
		}*/
		final Token token = new Token();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		token.setId(m.group(3));
		//QLog.log(this, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " uid=" + token.getId());
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			//QLog.log(this, "onCheckToken error");
			listener.onWvOauthError();
			return;
		}
		if(token.getId() == null || token.getId().equals("")){
			listener.onWvOauthError();
			return;
		}
		new Thread() {
				@Override
				public void run() {
					Message msg  = mHandler.obtainMessage();
					Holder holder = new Holder();
					holder.listener = listener;
					holder.ctx = ctx;
					try {
						JSONObject json = new JSONObject(httpGet(urlUsersShow(token)));
						token.setName(json.getString("screen_name"));
						token.setPhoto(json.getString("profile_image_url"));
						//QLog.log(this, "screen_name=" + token.getName() + " profile_image_url=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							msg.what = MSG_ERROR;
						}else{
							holder.token = token;
							msg.what = MSG_SUCCESS;
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = MSG_ERROR;
					} finally {
						msg.obj = holder;
						mHandler.sendMessage(msg);
					}
					
				}
		}.start();
	}
	
	
	
	public static String urlUsersShow(Token token) {
		return "https://api.weibo.com/2/users/show.json?"
					+ "access_token=" + token.getToken()
					+ "&uid=" + token.getId();
	}
	
	public static String urlFriends(Token token, int count, int cursor) throws IOException, UnAuthException{
		return "https://api.weibo.com/2/friendships/friends.json?"
					+ "access_token=" + token.getToken()
					+ "&uid=" + token.getId()
					+ "&count=" + count
					+ "&cursor=" + cursor;
	}

}