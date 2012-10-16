package q.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadMgr {
	
	private static ThreadMgr instance;
	private ThreadMgr(){}
	public static final ThreadMgr getInstance(){
		if(instance == null){
			synchronized (ThreadMgr.class) {
				if(instance == null){
					instance = new ThreadMgr();
					instance.nThreadPool = Executors.newFixedThreadPool(5);//5个线程
				}
			}
		}
		return instance;
	}
	
	private ExecutorService nThreadPool;//线程池
	
	public void execute(Runnable command){
		nThreadPool.execute(command);
	}
	
	public ExecutorService getExecutorService(){
		return nThreadPool;
	}

}
