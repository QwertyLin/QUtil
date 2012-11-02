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


public class QzoneHandle extends OauthHandle {
	
	protected static final String CLIENT_ID = "100246308";
	
	@Override
	public int getType() {
		return OauthHelper.TYPE_QZONE;
	}

	@Override
	public String getAuthUrl() {
		return "https://graph.qq.com/oauth2.0/authorize?" 
				+ "client_id=" + CLIENT_ID 
				+ "&redirect_uri=tencentauth:%2F%2Fauth.qq.com"
				+ "&response_type=token"
				+ "&display=mobile"
				+ "&scope=get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";
	}
	
	@Override
	public String getUrlParsePattern() {
		return ".+access_token=(.+)&expires_in=(.+)";
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
		new Thread() {
			@Override
			public void run() {
				Message msg  = mHandler.obtainMessage();
				Holder holder = new Holder();
				holder.listener = listener;
				holder.ctx = ctx;
				try {
					String data = httpGet("https://graph.qq.com/oauth2.0/me?access_token=" + token.getToken());
					if(data != null){
						String sep = "openid\":\"";
						int startIndex = data.indexOf(sep) + sep.length(); 
						int endIndex = data.indexOf("\"", startIndex);
						token.setId(data.substring(startIndex, endIndex));
					}
					//QLog.log(this, "openid=" + token.getId());
					if(token.getId() == null || token.getId().equals("")){
						msg.what = MSG_ERROR;
					}else{
						JSONObject json = new JSONObject(httpGet(urlUserInfo(token)));
						token.setName(json.getString("nickname"));
						token.setPhoto(json.getString("figureurl_1"));
						//QLog.log(this, "nickname=" + token.getName() + " figureurl_1=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							msg.what = MSG_ERROR;
						}else{
							holder.token = token;
							msg.what = MSG_SUCCESS;
						}
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
	
	
	
	public static String urlUserInfo(Token token) throws IOException, UnAuthException{
		return "https://graph.qq.com/user/get_user_info?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId();
	}

}