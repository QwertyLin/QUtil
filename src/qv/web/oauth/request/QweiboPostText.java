package qv.web.oauth.request;

import java.io.IOException;
import java.net.URLEncoder;

import qv.web.oauth.Token;
import qv.web.oauth.UnAuthException;

public class QweiboPostText extends QweiboHandle {
	
	public static String postText(Token token, String text) throws UnAuthException, IOException {
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
		}
		String param = 
				"oauth_consumer_key=" + CLIENT_ID 
				+ "&access_token=" + token.getToken()
				+ "&openid=" + token.getId()
				+ "&clientip=127.0.0.1"
				+ "&oauth_version=2.a"
				+ "&format=json"
				+ "&content=" + URLEncoder.encode(text, "utf-8");
		return httpPost("https://open.t.qq.com/api/t/add", param);
	}

}
