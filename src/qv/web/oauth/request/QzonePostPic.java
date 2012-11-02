package qv.web.oauth.request;

import java.io.IOException;

import qv.web.oauth.Token;
import qv.web.oauth.UnAuthException;

public class QzonePostPic extends QzoneHandle {

	public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
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
		return httpPost("https://graph.qq.com/photo/upload_pic", params.toString(), "-----114975832116442893661388290519", pic);
    }
}
