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
	
	/* Activity 活动 */
	
	public static QActivityManager activityManager(){
		return QActivityManager.getInstance();
	}
	
	 /* Bitmap 位图 */
	
	public static class bitmapDecoder extends QBitmapDecoder{}
	
	public static class bitmapFilter extends QBitmapFilter{}
	
	public static class bitmapFilterColor extends QBitmapFilterColor{}
	
	public static class bitmapFilterMatrix extends QBitmapFilterMatrix{}
	
	public static QBitmapManager bitmapManager(){
		return QBitmapManager.getInstance();
	}
	
	public static class bitmapUtil extends QBitmapUtil{}
	
	/* Code 编码 */
	
	public static class codeUtil extends QCodeUtil{}
	
	/* File 文件*/
	
	public static QFileManager fileManager(Context ctx){
		return QFileManager.getInstance(ctx);
	}
	
	/* HTTP 网络 */
	
	public static QHttpManager httpManager(Context ctx){
		return QHttpManager.getInstance(ctx);
	}
	
	public static class httpUtil extends QHttpUtil{}
	
	/* Intent */
	
	public static class intentUtil extends QIntentUtil{}
	
	/* OS 系统 */
	
	public static QWindowManager windowManager(Context ctx){
		return QWindowManager.getInstance(ctx);
	}
	
	public static class windowUtil extends QWindowUtil{}
	
	/* Stream 数据流 */
	
	public static class streamUtil extends QStreamUtil{}
	
	/* Thread 线程 */
	
	public static QThreadManager threadManager(){
		return QThreadManager.getInstance();
	}
	
}
