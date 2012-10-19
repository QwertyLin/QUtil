package qv.web.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONObject;

import android.os.Message;

import q.QLog;

public class WvOauthHandleQQZone implements WvOauthHandle {
	
	private static final String CLIENT_ID = "100246308";
	
	@Override
	public int getType() {
		return WvOauthUtil.TYPE_QQ_ZONE;
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
	public void parseUrl(final WvOauthEntity en, Matcher m) {
		final OnWvOauthListener callback = en.getListener();
		final WvOauthToken token = new WvOauthToken();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		QLog.log(WvOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime());
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			QLog.log(WvOauth.class, "onCheckToken error");
			callback.onWvOauthError(en);
			return;
		}
		WvOauthInstance.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Message msg  = WvOauthInstance.getInstance().gethandler().obtainMessage();
				try {
					String data = WvOauthUtil.httpGet("https://graph.qq.com/oauth2.0/me?access_token=" + token.getToken());
					if(data != null){
						String sep = "openid\":\"";
						int startIndex = data.indexOf(sep) + sep.length(); 
						int endIndex = data.indexOf("\"", startIndex);
						token.setId(data.substring(startIndex, endIndex));
					}
					QLog.log(WvOauth.class, "openid=" + token.getId());
					if(token.getId() == null || token.getId().equals("")){
						msg.what = WvOauthInstance.MSG_ERROR;
					}else{
						JSONObject json = new JSONObject(WvOauthUtil.httpGet(urlUserInfo(token)));
						token.setName(json.getString("nickname"));
						token.setPhoto(json.getString("figureurl_1"));
						QLog.log(WvOauth.class, "nickname=" + token.getName() + " figureurl_1=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							msg.what = WvOauthInstance.MSG_ERROR;
						}else{
							en.setToken(token);
							msg.what = WvOauthInstance.MSG_SUCCESS;
						}
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
	
	public static String postText(WvOauthToken token, String text) throws WvOauthUnAuthException, IOException {
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String param = 
				"oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&con=" + URLEncoder.encode(text, "utf-8")
				+ "&third_source=1"
				;
		return WvOauthUtil.httpPost("https://graph.qq.com/shuoshuo/add_topic", param);
	}
	
	public static String postPic(WvOauthToken token, String text, String pic) throws IOException, WvOauthUnAuthException{
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
		params.append("Content-Disposition: form-data; name=\"" + "oauth_consumer_key" + "\"\r\n\r\n");
        params.append(CLIENT_ID);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "openid" + "\"\r\n\r\n");
        params.append(token.getId());
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "photodesc" + "\"\r\n\r\n");
        params.append(text);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "title" + "\"\r\n\r\n");
        params.append(System.currentTimeMillis() + ".png");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "picture" + "\"; filename=\"" + "pic.png" + "\"\r\n");
        params.append("Content-Type: " + "image/x-png" + "\r\n\r\n");
        //
		return WvOauthUtil.httpPost("https://graph.qq.com/photo/upload_pic", params.toString(), "-----114975832116442893661388290519", pic);
    }
	
	public static String urlUserInfo(WvOauthToken token) throws IOException, WvOauthUnAuthException{
		return "https://graph.qq.com/user/get_user_info?"
				+ "oauth_consumer_key=" + CLIENT_ID
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId();
	}

}