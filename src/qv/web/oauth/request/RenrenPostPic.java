package qv.web.oauth.request;

import java.io.IOException;

import qv.web.oauth.Token;
import qv.web.oauth.UnAuthException;

public class RenrenPostPic extends RenrenHandle {

	public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
		if(token == null || isTokenExpire(token)){
			throw new UnAuthException();
		}
		String md5 = md5(
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
		return httpPost("http://api.renren.com/restserver.do", params.toString(), "-----------------------------114975832116442893661388290519", pic);
    }
	
}
