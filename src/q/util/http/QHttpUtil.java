package q.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class QHttpUtil {
	
	public static String toString(HttpURLConnection conn) throws IOException{
    	return "response " + 
        		"url: " + conn.getURL()
    			+ " ContentEncoding: " + conn.getContentEncoding()
        		+ " ResponseCode: " + conn.getResponseCode()
        		+ " ResponseMessage: " + conn.getResponseMessage()
        		+ " ContentType: " + conn.getContentType()
        		+ " ConnectTimeout: " + conn.getConnectTimeout()
        		+ " ReadTimeout: " + conn.getReadTimeout()
        		+ " ContentLength: " + conn.getContentLength()
        	;
    	/*QLog.log();*/
		//System.out.println("method:"+conn.getRequestMethod());
		//System.out.println("defaultPort:"+conn.getURL().getDefaultPort());
		//System.out.println("file:"+conn.getURL().getFile());
		//System.out.println("host:"+conn.getURL().getHost());
		//System.out.println("path:"+conn.getURL().getPath());
		//System.out.println("port:"+conn.getURL().getPort());
		//System.out.println("protocol:"+conn.getURL().getProtocol());
		//System.out.println("query:"+conn.getURL().getQuery());
		//System.out.println("ref:"+conn.getURL().getRef());
		//System.out.println("userInfo:"+conn.getURL().getUserInfo());
    }

	
}
