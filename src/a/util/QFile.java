package a.util;

import java.io.File;

import q.util.a.QLog;

import android.content.Context;
import android.os.Environment;

public final class QFile {
	
	private String
		sysRoot = "/data/data/cn.xxd.tx/tx",
		sdRoot = Environment.getExternalStorageDirectory().getPath() + "/xxd.cn/tx";
	
	private String root;
	
	private String[] dirs = new String[]{
			root + "/img", root + "/http",
			root + "/cache", root + "/temp"
			};
	

	private static QFile instance;
	private QFile(){};
	public static final QFile getInstance(){
		if(instance == null){
			instance = new QFile();
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//挂载sd卡
				instance.root = instance.sdRoot;
			}else{
				instance.root = instance.sysRoot;
			}
			instance.initDirs();
		}
		return instance;
	}
	
	private void initDirs(){
		QLog.log("QFile 初始化文件夹");
		File dir;
		//
		dir = new File(sysRoot);
		if(!dir.exists()){
			dir.mkdirs();
		}
		//
		dir = new File(sdRoot);
		if(!dir.exists()){
			dir.mkdirs();
		}
		//
		for(String d : dirs){
			dir = new File(d);
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
	}
	
	public String getImg(String id){
		File f = new File(dirs[0] + "/" + id);
		if(!f.exists()){
			f.mkdir();
		}
		return f.getPath() + "/";
	}
	
	public String toString(Context ctx) {
		StringBuffer sb = new StringBuffer();
		sb.append("/***\n");
		sb.append("system root dir = " + Environment.getRootDirectory() + "\n");
		sb.append("external storage dir = " + Environment.getExternalStorageDirectory() + "\n");
		sb.append("external storage state = " + Environment.getExternalStorageState() + "\n");
		sb.append("system dcim dir = " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "\n");
		sb.append("app file dir = " + ctx.getFilesDir() + "\n");
		sb.append("app cache dir = " + ctx.getCacheDir() + "\n");
		sb.append("***/");
		return sb.toString();
	}
}
