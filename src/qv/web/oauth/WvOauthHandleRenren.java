package qv.web.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Message;

import q.QLog;

public class WvOauthHandleRenren implements WvOauthHandle {
	
	private static final String 
		RENREN_CLIENT_ID = "ce90074fea9c4650b6c860aaf149c758", 
		RENREN_CLIENT_SECRET = "1edb76174931407f80c25984385f176a";
	
	@Override
	public int getType() {
		return WvOauthUtil.TYPE_RENREN;
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
		if(token.getToken().contains("%7C")){//accessToken 人人网特殊处理
			token.setToken(token.getToken().replace("%7C", "|"));
		}
		WvOauthInstance.getInstance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				Message msg  = WvOauthInstance.getInstance().gethandler().obtainMessage();
				try {
					JSONArray jsonA = new JSONArray(postUsersInfo(token));
					JSONObject json = jsonA.getJSONObject(0);
					token.setId(json.getString("uid"));
					token.setName(json.getString("name"));
					token.setPhoto(json.getString("tinyurl"));
					QLog.log(WvOauth.class, "uid=" + token.getId() + " name=" + token.getName() + " tinyurl=" + token.getPhoto());
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
	
	public static String postUsersInfo(WvOauthToken token) throws IOException, WvOauthUnAuthException{
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String md5 = WvOauthUtil.md5(
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
		return WvOauthUtil.httpPost("http://api.renren.com/restserver.do", param);
	}
	
	public static String postFriends(WvOauthToken token) throws IOException, WvOauthUnAuthException{
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String md5 = WvOauthUtil.md5(
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
		return WvOauthUtil.httpPost("http://api.renren.com/restserver.do", param);
	}
	
	public static String postText(WvOauthToken token, String text) throws WvOauthUnAuthException, IOException {
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String md5 = WvOauthUtil.md5(
				"access_token="+token.getToken()
				+ "format=JSON"
				+ "method=status.set"
				+ "status="+text
				+ "v=1.0"
				+ RENREN_CLIENT_SECRET);
		String param = "&access_token=" + token.getToken()
				+ "&format=JSON"
				+ "&method=status.set"
				+ "&status="+URLEncoder.encode(text, "utf-8")
				+ "&v=1.0"
				+ "&sig="+md5;
		return WvOauthUtil.httpPost("http://api.renren.com/restserver.do", param);
	}
	
	public static String postPic(WvOauthToken token, String text, String pic) throws IOException, WvOauthUnAuthException{
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String md5 = WvOauthUtil.md5(
				"access_token=" + token.getToken()
				+ "api_key="+ RENREN_CLIENT_ID
				+ "caption=" + text
				+ "format=JSON"
				+ "method=photos.upload"
				+ "v=1.0"
				+ RENREN_CLIENT_SECRET);					
		//
		String boundary = "-----------------------------114975832116442893661388290519";
		StringBuffer params = new StringBuffer();
		boundary = "\r\n" + "--" + boundary + "\r\n";
		//
		params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "method" + "\"\r\n\r\n");
        params.append("photos.upload");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "api_key" + "\"\r\n\r\n");
        params.append(RENREN_CLIENT_ID);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
        params.append(token.getToken());
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "sig" + "\"\r\n\r\n");
        params.append(md5);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "v" + "\"\r\n\r\n");
        params.append("1.0");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "format" + "\"\r\n\r\n");
        params.append("JSON");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "caption" + "\"\r\n\r\n");
        params.append(text);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "upload" + "\"; filename=\"" + "pic.png" + "\"\r\n");
        params.append("Content-Type: " + "image/png" + "\r\n\r\n");
        //
		return WvOauthUtil.httpPost("http://api.renren.com/restserver.do", params.toString(), "-----------------------------114975832116442893661388290519", pic);
    }

}