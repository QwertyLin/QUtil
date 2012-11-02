package qv.web.oauth.request;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONObject;

import qv.web.oauth.OauthHandle;
import qv.web.oauth.OauthHelper;
import qv.web.oauth.OnOauthListener;
import qv.web.oauth.Token;

import android.content.Context;
import android.os.Message;


public class QweiboHandle extends OauthHandle {
	
	protected static final String 
		CLIENT_ID = "801140374", 
		CALLBACK_URL = "http:%2F%2Fwww.xxd.cn";
	
	@Override
	public int getType() {
		return OauthHelper.TYPE_QWEIBO;
	}

	@Override
	public String getAuthUrl() {
		return "https://open.t.qq.com/cgi-bin/oauth2/authorize?" 
				+ "client_id=" + CLIENT_ID 
				+ "&redirect_uri=" + CALLBACK_URL
				+ "&response_type=token";
	}
	
	@Override
	public String getUrlParsePattern() {
		return ".+access_token=(.+)&expires_in=(.+)&openid=(.+)&openkey";
	}

	@Override
	public void parseUrl(final Context ctx, Matcher m, final OnOauthListener listener) {
		final Token token = new Token();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		token.setId(m.group(3));
		//QLog.log(this, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " openid=" + token.getId());
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
					JSONObject json = new JSONObject(httpGet(urlUserInfoSimple(token, token.getId())));
					json = json.getJSONObject("data").getJSONArray("info").getJSONObject(0);
					token.setName(json.getString("nick"));
					token.setPhoto(json.getString("head") + "/50");
					//QLog.log(this, "nick=" + token.getName() + " head=" + token.getPhoto());
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
	
	
	
	public static String urlUserInfoSimple(Token token, String id) {
		return "https://open.t.qq.com/api/user/infos?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
				+ "&fopenids=" + id
				;
	}
	
	/*public static String urlUserInfo(Token token) {
		return "https://open.t.qq.com/api/user/info?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
				;
	}*/
	
	public static String urlFriendsIdolistSimple(Token token, int reqnum, int startindex) throws IOException{
		return "https://open.t.qq.com/api/friends/idollist_s?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
				+ "&reqnum=" + reqnum
				+ "&startindex=" + startindex
				;
	}
	
	/*public static String urlFriends(Token token) throws IOException{
		return "https://open.t.qq.com/api/friends/idollist?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
				+ "&reqnum=30"
				;
	}*/

}