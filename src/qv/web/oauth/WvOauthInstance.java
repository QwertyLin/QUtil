package qv.web.oauth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

class WvOauthInstance {
	
	public static final int MSG_SUCCESS = 1, MSG_ERROR = 2;
	
	private static WvOauthInstance mInstance;
	
	private WvOauthInstance(){}
	
	public static WvOauthInstance getInstance(){
		if(mInstance == null){
			synchronized (WvOauthInstance.class) {
				if(mInstance == null){
					mInstance = new WvOauthInstance();
				}
			}
		}
		return mInstance;
	}
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	
	public ExecutorService getThreadPool(){
		return threadPool;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			WvOauthEntity en = (WvOauthEntity) msg.obj;
			switch(msg.what){
			case MSG_SUCCESS:
				en.getListener().onWvOauthSuccess(en);
				break;
			case MSG_ERROR:
				en.getListener().onWvOauthError(en);
				break;
			}
		};
	};
	
	public Handler gethandler(){
		return mHandler;
	}

}
