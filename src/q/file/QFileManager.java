package q.file;


import java.io.File;

import q.QLog;


import android.content.Context;
import android.os.Environment;

/**
 * 管理手机内存与SD卡内存中的文件路径
 *
 */
public class QFileManager {
	
	private static QFileManager nInstance;
	
	private QFileManager(Context ctx){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//挂载sd卡
			nRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ctx.getPackageName() + File.separator;
		}else{
			nRoot = ctx.getCacheDir() + File.separator;
		}
		File file = new File(nRoot);
		if(!file.exists()){
			file.mkdirs();
		}
		QLog.kv(this, "init", "root", nRoot);
	}
	
	public static QFileManager getInstance(Context ctx){
		if(nInstance == null){
			synchronized (QFileManager.class) {
				if(nInstance == null){
					nInstance = new QFileManager(ctx);
				}
			}
		}
		return nInstance;
	}
	
	private String nRoot;
	
	public String get(String dir){
		String filePath = nRoot + dir + File.separator;
		File file = new File(filePath);
		if(file.exists() || file.mkdirs()){
			return filePath;
		}else{
			return null;
		}
	}
}
