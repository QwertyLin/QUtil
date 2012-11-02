package qv.web.oauth.request;

import java.io.IOException;

import qv.web.oauth.Token;
import qv.web.oauth.UnAuthException;

public class QweiboPostPic extends QweiboHandle {

	public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
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
		return httpPost("https://open.t.qq.com/api/t/add_pic", params.toString(), "-----114975832116442893661388290519", pic);
	}
	
}
