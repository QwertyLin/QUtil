package q.util;

import android.content.Context;
import q.util.activity.QActivityManager;
import q.util.bitmap.QBitmapDecoder;
import q.util.bitmap.QBitmapFilter;
import q.util.bitmap.QBitmapFilterColor;
import q.util.bitmap.QBitmapFilterMatrix;
import q.util.bitmap.QBitmapManager;
import q.util.bitmap.QBitmapUtil;
import q.util.code.QCodeUtil;
import q.util.file.QFileManager;
import q.util.http.QHttpManager;
import q.util.http.QHttpUtil;
import q.util.intent.QIntentUtil;
import q.util.os.QWindowManager;
import q.util.os.QWindowUtil;
import q.util.stream.QStreamUtil;
import q.util.thread.QThreadManager;

public final class QUtil {
	
	public static class log extends QLog{}
	
	/* Activity */
	
	public static QActivityManager activityM(){
		return QActivityManager.getInstance();
	}
	
	 /* Bitmap 位图 */
	
	public static class bitmapDecoder extends QBitmapDecoder{}
	
	public static class bitmapFilter extends QBitmapFilter{}
	
	public static class bitmapFilterColor extends QBitmapFilterColor{}
	
	public static class bitmapFilterMatrix extends QBitmapFilterMatrix{}
	
	public static QBitmapManager bitmapM(){
		return QBitmapManager.getInstance();
	}
	
	public static class bitmap extends QBitmapUtil{}
	
	/* Code 编码 */
	
	public static class code extends QCodeUtil{}
	
	/* File 文件*/
	
	public static QFileManager fileM(Context ctx){
		return QFileManager.getInstance(ctx);
	}
	
	/* HTTP 网络 */
	
	public static QHttpManager httpM(Context ctx){
		return QHttpManager.getInstance(ctx);
	}
	
	public static class http extends QHttpUtil{}
	
	/* Intent */
	
	public static class intent extends QIntentUtil{}
	
	/* OS 系统 */
	
	public static QWindowManager windowM(Context ctx){
		return QWindowManager.getInstance(ctx);
	}
	
	public static class window extends QWindowUtil{}
	
	/* Stream 数据流 */
	
	public static class stream extends QStreamUtil{}
	
	/* Thread 线程 */
	
	public static QThreadManager threadM(){
		return QThreadManager.getInstance();
	}
	
}
