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
import q.util.sqlite.QSqliteEntity;
import q.util.sqlite.QSqliteSuper;
import q.util.stream.QStreamUtil;
import q.util.thread.QThreadManager;
import q.view.QView;

public class Q {
	
	/* Log */
	public static class log extends QLog{}
	
	/* View */
	
	public static class view extends QView{}
	
	/* Activity */
	public static final class activity {
		
		public static QActivityManager manager(){
			return QActivityManager.getInstance();
		}
		
	}
	
	 /* Bitmap 位图 */
	public static final class bitmap {
		
		public static class decoder extends QBitmapDecoder{}
		
		public static class filter extends QBitmapFilter{}
		
		public static class filterColor extends QBitmapFilterColor{}
		
		public static class filterMatrix extends QBitmapFilterMatrix{}
		
		public static QBitmapManager manager(){
			return QBitmapManager.getInstance();
		}
		
		public static class util extends QBitmapUtil{}
	}
	
	/* Code 编码 */
	public static class code {
		
		public static class util extends QCodeUtil{}
	}
	
	/* File 文件*/
	public static class file {
		
		public static QFileManager manager(Context ctx){
			return QFileManager.getInstance(ctx);
		}
	}
	
	/* HTTP 网络 */
	public static class http {
		
		public static QHttpManager manager(Context ctx){
			return QHttpManager.getInstance(ctx);
		}
		
		public static class util extends QHttpUtil{}
		
	}
	
	/* Intent */
	public static class intent {
		
		public static class util extends QIntentUtil{}
	}
	
	/* OS 系统 */
	public static class os {
		
		public static class window {
			
			public static QWindowManager manager(Context ctx){
				return QWindowManager.getInstance(ctx);
			}
			
			public static class util extends QWindowUtil{}
		}
		
	}
	
	/* Sqlite 数据库 */
	public static class sqlite {
		
		/**
		 * 数据库表类
		 */
		public static abstract class entity extends QSqliteEntity{}
		
		public static abstract class base<T extends Q.sqlite.entity> extends QSqliteSuper<T>{
			public base(Context ctx) {
				super(ctx);
			}
		}
	}
	
	/* Stream 数据流 */
	public static class stream {
		
		public static class util extends QStreamUtil{}
	}
	
	/* Thread 线程 */
	public static class thread {
		
		public static QThreadManager manager(){
			return QThreadManager.getInstance();
		}
	}
	
}
