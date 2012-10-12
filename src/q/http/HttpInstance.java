package q.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;

public class HttpInstance {
	
	public static final int SUCCESS = 0, ERROR = 1;
	
	private static HttpInstance nInstance;
	
	private HttpInstance(){}
	
	public static HttpInstance getInstance(){
		if(nInstance == null){
			synchronized (HttpUtil.class) {
				if(nInstance == null){
					nInstance = new HttpInstance();
				}
			}
		}
		return nInstance;
	}
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(5);
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			HttpEntity entity = (HttpEntity)msg.obj;
			switch (msg.what) {
			case SUCCESS:
				entity.getListener().onHttpFinish(entity);
				break;
			case ERROR:
			default:
				entity.getListener().onHttpError(entity);
				break;
			}
		};
	};

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	public Handler getHandler() {
		return handler;
	}	
	
	

}
