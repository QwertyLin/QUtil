package q.a0;

import java.lang.Thread.UncaughtExceptionHandler;

import q.QLog;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {  
	  
    private final static String TAG = "UncaughtExceptionHandler";  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    private static CrashHandler mInstance;  
    private Context mContext;  
    private CrashHandler() {  
    }  
  
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        if (mInstance == null)  
            mInstance = new CrashHandler();  
        return mInstance;  
    }  
  
    @Override  
    public void uncaughtException(Thread thread, Throwable throwable) {  
        if (!handleException(throwable) && mDefaultHandler != null) {    
            // 如果用户没有处理则让系统默认的异常处理器来处理     
            mDefaultHandler.uncaughtException(thread, throwable);    
        } else {    
            // Sleep一会后结束程序     
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序     
            try {    
                Thread.sleep(3000);    
            } catch (InterruptedException e) {    
                Log.e(TAG, "Error : ", e);    
            }    
            android.os.Process.killProcess(android.os.Process.myPid());    
            System.exit(10);    
        }    
    }  
      
    private boolean handleException(Throwable ex) {    
        if (ex == null) {    
            return true;    
        }    
        final String msg = ex.getLocalizedMessage();    
        QLog.error(mContext, msg);
        // 使用Toast来显示异常信息     
        /*new Thread() {    
            @Override    
            public void run() {    
                // Toast 显示需要出现在一个线程的消息队列中     
                Looper.prepare();    
                Toast.makeText(mContext, "Exception:" + msg, Toast.LENGTH_LONG).show();    
                Looper.loop();    
            }    
        }.start();*/    
        return true;    
    }    
      
    public void init(Context context) {    
        mContext = context;    
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();    
        Thread.setDefaultUncaughtExceptionHandler(this);    
    }    
      
  
} 