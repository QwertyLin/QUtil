package qv.web.oauth.request;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import qv.web.oauth.Token;
import qv.web.oauth.UnAuthException;

public class SinaPostPic extends SinaHandle {

	public static String postPic(Token token, String text, String pic, String lat, String lng) throws IOException, UnAuthException{
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
		params.append("Content-Disposition: form-data; name=\"" + "status" + "\"\r\n\r\n");
		int space = new Random().nextInt(50); //space repeat
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < space; i++){
			sb.append(" ");
		}
	    params.append(text + sb);
	    //
	    if(lat != null && lng != null){
	    	params.append(boundary);
	 		params.append("Content-Disposition: form-data; name=\"" + "lat" + "\"\r\n\r\n");
	 	    params.append(lat);
	 	    //
	 	    params.append(boundary);
	 		params.append("Content-Disposition: form-data; name=\"" + "long" + "\"\r\n\r\n");
	 	    params.append(lng);
	    }
	    //
	    params.append(boundary);
		params.append("Content-Disposition: form-data; name=\"" + "pic" + "\"; filename=\"" + new File(pic).getName() + "\"\r\n");
	    params.append("Content-Type: " + "image/x-png" + "\r\n\r\n");
	    //
		return httpPost("https://upload.api.weibo.com/2/statuses/upload.json", params.toString(), "-----114975832116442893661388290519", pic);
	}
}
