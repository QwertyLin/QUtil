package q.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public final class QUtil2 {
	
	//=============================== 网络类 =====================================
    protected void Net(){}
    
    /**
     * 获得HTTP请求的响应实体
     * @param url 如 http://www.baidu.com/
     * @return 请求失败时返回NULL
     */
    public static final HttpEntity Net_getHttpResponseEntity(String url) throws ClientProtocolException, IOException {
    	HttpClient httpclient = new DefaultHttpClient();
		HttpGet http = new HttpGet(url);
		addHeader(http, url);//可选
		HttpResponse resp = httpclient.execute(http);
		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			//连接成功
			return resp.getEntity();
		}else{
			//连接失败
			return null;
		}
		//httpclient.getConnectionManager().shutdown();
    }
    
    /**
     * 获得HTTP请求的响应输入流
     * @param url 如 http://www.baidu.com/
     * @return 请求失败时返回NULL
     */
    public static final InputStream Net_getHttpInputStream(String url) throws ClientProtocolException, IOException {
		HttpEntity entity = Net_getHttpResponseEntity(url);
		if(null == entity){
			return null;
		}
		return entity.getContent();
		//httpclient.getConnectionManager().shutdown();
	}
    
    /**
	 * 通过GET请求获得远程数据
	 * @param url 如 http://www.baidu.com/
	 * @return 远程数据，连接失败时返回null
	 */
	public static final String Net_getHttp(String url) throws ClientProtocolException, IOException {
		HttpEntity entity = Net_getHttpResponseEntity(url);
		if(null == entity){
			return null;
		}
		return EntityUtils.toString(entity, "UTF-8");
        //httpclient.getConnectionManager().shutdown();
	}
	
	/**
	 * 下载文件
	 * @param url 如 http://www.baidu.com/
	 * @param path 文件保存路劲，如 /sdcard/1
	 */
	public static final void Net_downFile(String url, String path) throws IllegalStateException, ClientProtocolException, IOException { 
		InputStream in = Net_getHttpInputStream(url);
		if(null == in){
			return;
		}
		OutputStream out = new FileOutputStream(path);
		byte[] b = new byte[1024];       
		int len;   
		while ((len = in.read(b)) != -1) { 
        	out.write(b, 0, len);       
        }
		out.flush();
		out.close();
		in.close();
        //httpclient.getConnectionManager().shutdown();
	}
	
	/**
	 * 通过POST请求获得远程数据
	 * @param url 如 http://www.baidu.com/
	 * @param params 请求参数，如"query"=>"abc"
	 * @return 远程数据，连接失败时返回null
	 */
	public static final String Net_postHttp(String url, Map<String, String> params) throws ClientProtocolException, IOException { 
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost http = new HttpPost(url);
		addHeader(http, url);//可选
		//参数队列
		List<NameValuePair> formparams=new ArrayList<NameValuePair>();
		for(String key:params.keySet()){
			formparams.add(new BasicNameValuePair(key, params.get(key)));
		}
        http.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
        HttpResponse resp = httpclient.execute(http);
		HttpEntity entity;
		String src;
		if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			//连接成功
			entity = resp.getEntity();
			src = EntityUtils.toString(entity, "UTF-8");
		}else{
			//连接失败
			src = null;
		}
        httpclient.getConnectionManager().shutdown();
        return src;
	}
	
	//可选，设置Header
	private static final void addHeader(AbstractHttpMessage http, String url){
		String host = url;
		int i = host.indexOf("://");
  		if (i != -1) {
  			host = host.substring(i + 3);
  		}
  		i = host.indexOf("/");
  		if (i != -1) {
  			host = host.substring(0, i);
  		}
  		System.out.println("==addHeaders: host="+host + " Referer="+url);
		http.addHeader("Host", host);
		http.addHeader("Referer", url);
		http.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
	}
}
