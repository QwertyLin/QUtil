package qv.web.oauth.request;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

import qv.web.oauth.OauthHandle;
import qv.web.oauth.OauthHelper;
import qv.web.oauth.UnAuthException;
import qv.web.oauth.OnOauthListener;
import qv.web.oauth.Token;
import qv.web.oauth.OauthHandle.Holder;

import android.content.Context;
import android.os.Message;


public class RenrenHandle extends OauthHandle {
	
	protected static final String 
		RENREN_CLIENT_ID = "ce90074fea9c4650b6c860aaf149c758", 
		RENREN_CLIENT_SECRET = "1edb76174931407f80c25984385f176a";
	
	@Override
	public int getType() {
		return OauthHelper.TYPE_RENREN;
	}

	@Override
	public String getAuthUrl() {
		return "http://graph.renren.com/oauth/authorize?" 
				+ "client_id=" + RENREN_CLIENT_ID 
				+ "&redirect_uri=http:%2F%2Fgraph.renren.com%2Foauth%2Flogin_success.html"
				+ "&response_type=token"
				+ "&display=touch"
				+ "&scope=status_update+photo_upload";
	}
	
	@Override
	public String getUrlParsePattern() {
		return ".+access_token=(.+)&expires_in=(.+)&scope";
	}

	@Override
	public void parseUrl(final Context ctx, Matcher m, final OnOauthListener listener) {
		final Token token = new Token();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		//QLog.log(this, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime());
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			//QLog.log(this, "onCheckToken error");
			listener.onWvOauthError();
			return;
		}
		if(token.getToken().contains("%7C")){//accessToken special
			token.setToken(token.getToken().replace("%7C", "|"));
		}
		new Thread() {
			@Override
			public void run() {
				Message msg  = mHandler.obtainMessage();
				Holder holder = new Holder();
				holder.listener = listener;
				holder.ctx = ctx;
				try {
					JSONArray jsonA = new JSONArray(postUsersInfo(token));
					JSONObject json = jsonA.getJSONObject(0);
					token.setId(json.getString("uid"));
					token.setName(json.getString("name"));
					token.setPhoto(json.getString("tinyurl"));
					//QLog.log(this, "uid=" + token.getId() + " name=" + token.getName() + " tinyurl=" + token.getPhoto());
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
	
	public static String postUsersInfo(Token token) throws IOException, UnAuthException{
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
		}
		String md5 = md5(
				"access_token=" + token.getToken()
				+ "format=JSON"
				+ "method=users.getInfo"
				+ "v=1.0"
				+ RENREN_CLIENT_SECRET);
		String param = 
				"&access_token=" + token.getToken()
				+ "&format=JSON"
				+ "&method=users.getInfo"
				+ "&v=1.0"
				+ "&sig="+md5;
		return httpPost("http://api.renren.com/restserver.do", param);
	}
	
	public static String postFriends(Token token) throws IOException, UnAuthException{
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
		}
		String md5 = md5(
				"access_token=" + token.getToken()
				+ "format=JSON"
				+ "method=" + "friends.getFriends"
				+ "v=1.0"
				+ RENREN_CLIENT_SECRET);
		String param = 
				"&access_token=" + token.getToken() 
				+ "&format=JSON"
				+ "&method=" + "friends.getFriends"
				+ "&v=1.0"
				+ "&sig="+md5;
		return httpPost("http://api.renren.com/restserver.do", param);
	}
	
	

}