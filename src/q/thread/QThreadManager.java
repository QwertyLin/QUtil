package q.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QThreadManager {
	
	private static QThreadManager instance;
	private QThreadManager(){}
	public static final QThreadManager getInstance(){
		if(instance == null){
			synchronized (QThreadManager.class) {
				if(instance == null){
					instance = new QThreadManager();
					instance.nThreadPool = Executors.newFixedThreadPool(5);//5个线程
				}
			}
		}
		return instance;
	}
	
	private ExecutorService nThreadPool;//线程池
	
	public ExecutorService getExecutorService(){
		return nThreadPool;
	}
	
	public void execute(Runnable command){
		nThreadPool.execute(command);
	}
	
	public Future<?> submit(Runnable task){
		return nThreadPool.submit(task);
	}
	
	

}
