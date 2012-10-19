package qv.web.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;

import org.json.JSONObject;

import android.os.Message;

import q.QLog;

public class WvOauthHandleQQWeibo implements WvOauthHandle {
	
	private static final String 
		CLIENT_ID = "801140374", 
		CALLBACK_URL = "http:%2F%2Fwww.xxd.cn";
	
	@Override
	public int getType() {
		return WvOauthUtil.TYPE_QQ_WEIBO;
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
	public void parseUrl(final WvOauthEntity en, Matcher m) {
		final OnWvOauthListener callback = en.getListener();
		final WvOauthToken token = new WvOauthToken();
		token.setType(getType());
		token.setToken(m.group(1));
		token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
		token.setId(m.group(3));
		QLog.log(WvOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " openid=" + token.getId());
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
					JSONObject json = new JSONObject(WvOauthUtil.httpGet(urlUserInfoSimple(token, token.getId())));
					json = json.getJSONObject("data").getJSONArray("info").getJSONObject(0);
					token.setName(json.getString("nick"));
					token.setPhoto(json.getString("head") + "/50");
					QLog.log(WvOauth.class, "nick=" + token.getName() + " head=" + token.getPhoto());
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
	
	public static String postText(WvOauthToken token, String text) throws WvOauthUnAuthException, IOException {
		if(token == null || WvOauthUtil.isTokenExpire(token)){
			throw new WvOauthUnAuthException();
		}
		String param = 
				"oauth_consumer_key=" + CLIENT_ID 
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1"
				+ "&oauth_version=2.a"
				+ "&format=json"
				+ "&content=" + URLEncoder.encode(text, "utf-8");
		return WvOauthUtil.httpPost("https://open.t.qq.com/api/t/add", param);
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
		params.append("Content-Disposition: form-data; name=\"" + "oauth_consumer_key" + "\"\r\n\r\n");
        params.append(CLIENT_ID);
		//
		params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
        params.append(token.getToken());
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "openid" + "\"\r\n\r\n");
        params.append(token.getId());
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "oauth_version" + "\"\r\n\r\n");
        params.append("2.a");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "format" + "\"\r\n\r\n");
        params.append("json");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "content" + "\"\r\n\r\n");
        params.append(text);
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "clientip" + "\"\r\n\r\n");
        params.append("127.0.0.1");
        //
        params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "pic" + "\"; filename=\"" + "pic.png" + "\"\r\n");
        params.append("Content-Type: " + "application/octet-stream" + "\r\n\r\n");
        //
		return WvOauthUtil.httpPost("https://open.t.qq.com/api/t/add_pic", params.toString(), "-----114975832116442893661388290519", pic);
	}
	
	public static String urlUserInfoSimple(WvOauthToken token, String id) {
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
	
	public static String urlFriendsIdolistSimple(WvOauthToken token, int reqnum, int startindex) throws IOException{
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