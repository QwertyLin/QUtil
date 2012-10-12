package q.http;

import java.io.File;

public class HttpEntity {

	private int id;
	private OnHttpListener listener;
	private String url;
	private File cacheFile;
	private long cacheTime;
	
	public HttpEntity(OnHttpListener listener, int id, String url, File cacheFile, long cacheTime){
		this.listener = listener;
		this.id = id;
		this.url = url;
		this.cacheFile = cacheFile;
		this.cacheTime = cacheTime;
	}

	public int getId() {
		return id;
	}

	public OnHttpListener getListener() {
		return listener;
	}

	public String getUrl() {
		return url;
	}

	public File getCacheFile() {
		return cacheFile;
	}

	public long getCacheTime() {
		return cacheTime;
	}

	
}
