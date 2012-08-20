package q.util;

import q.manager.QWindow;
import android.app.Application;

public class QApp extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		qWindow = new QWindow(this); 
	}

	private QWindow qWindow;

	public QWindow getQWindow() {
		return qWindow;
	}
	
	
}
