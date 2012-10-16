package qv.list.more;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

public class LvAutoMoreInstance {
	
	public static final int MSG_SUCCESS = 0, MSG_ENABLE = 1, MSG_DISABLE = 2;
	
	private static LvAutoMoreInstance mInstance;
	
	private LvAutoMoreInstance(){}
	
	public static LvAutoMoreInstance getInstance(){
		if(mInstance == null){
			synchronized (LvAutoMoreInstance.class) {
				if(mInstance == null){
					mInstance = new LvAutoMoreInstance();
				}
			}
		}
		return mInstance;
	}

	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	
	public ExecutorService getThreadPool() {
		return threadPool;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			LvAutoMoreEntity en = (LvAutoMoreEntity)msg.obj;
			switch(msg.what){
			case MSG_SUCCESS:
				en.getListener().onLvAutoMoreFinish(en);
				en.setDoing(false);
				break;
			case MSG_ENABLE://enable true
				en.getListView().removeFooterView(en.getViewFooter());
				en.getListView().addFooterView(en.getViewFooter());
				break;
			case MSG_DISABLE://enable false
				en.getListView().removeFooterView(en.getViewFooter());
				break;
			}
		};
	};
	
	public Handler getHandler(){
		return mHandler;
	}
	
}
