package q.util.file;

import java.io.File;

import q.util.QLog;

import android.content.Context;
import android.os.Environment;

/**
 * 管理手机内存与SD卡内存中的文件路径
 *
 */
public class QFileManager {
	
	private static QFileManager instance;
	
	private QFileManager(){}
	
	public static QFileManager getInstance(Context ctx){
		if(instance == null){
			synchronized (QFileManager.class) {
				if(instance == null){
					instance = new QFileManager();
					instance.init(ctx);
				}
			}
		}
		return instance;
	}
	
	public String root;
		
	/**
	 * 初始化
	 */
	private void init(Context ctx){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//挂载sd卡
			root = Environment.getExternalStorageDirectory().getPath() + File.separator + ctx.getPackageName() + File.separator;
		}else{
			root = ctx.getCacheDir() + File.separator;
		}
		File file = new File(root);
		if(!file.exists()){
			file.mkdirs();
		}
		QLog.kv(this, "init", "root", root);
	}
	
	public String get(String dir){
		String filePath = root + dir + File.separator;
		File file = new File(filePath);
		if(file.exists() || file.mkdirs()){
			return filePath;
		}else{
			return null;
		}
	}
}
