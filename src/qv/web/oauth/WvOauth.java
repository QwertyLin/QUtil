package qv.web.oauth;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import q.QLog;
import q.code.CodeUtil;
import q.http.QHttpUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Oauth2.0授权
 *
 */
public class WvOauth extends RelativeLayout {
	
	public static final int 
		TYPE_SINA_WEIBO = 1, //http://open.weibo.com/
		TYPE_QQ_WEIBO = 2, //http://dev.t.qq.com/
		TYPE_QQ_ZONE = 3, //http://opensns.qq.com/
		TYPE_RENREN = 4;
	
	private Activity act;
	private WvOauthHandle handle;
	
	public WvOauth(Context ctx, WvOauthHandle handle, OnWvOauthListener callback) {
		super(ctx);
		this.act = (Activity)ctx;
		this.handle = handle;
		//
		//this.type = act.getIntent().getIntExtra(EXTRA_TYPE, 0);
		//
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		//
		WebView webView = new WebView(ctx);
		initWebView(webView, callback);
		this.addView(webView, rlp);
		//
		layoutLoading = new ProgressBar(ctx);
		this.addView(layoutLoading, rlp);
		//
		QLog.log(WvOauth.class, "auth url:" + handle.getAuthUrl());
		webView.loadUrl(handle.getAuthUrl());
	}

	private ProgressBar layoutLoading;

	
	private void initWebView(final WebView webView, final OnWvOauthListener callback){
		
	}
	
	private static boolean onCheckToken(WvOauthToken token){
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			QLog.log(WvOauth.class, "onCheckToken error");
			return false;
		}
		return true;
	}
	
	protected void Q(){}
		
	private static class Sqlite extends SQLiteOpenHelper {
				   	
		//调用getWritableDatabase()或 getReadableDatabase()方法时创建数据库
		public Sqlite(Context context) {
			super(context, "oauth.db", null, 1);
		}
		
		// 函数在数据库第一次建立时被调用， 一般用来用来创建数据库中的表，并做适当的初始化工作
		@Override
		public void onCreate(SQLiteDatabase db) {
			QLog.log(WvOauth.class, "QLayoutOauth Sqlite onCreate");
			//可插入多个
			//db.execSQL(Demo.DB_CREATE);
			db.execSQL(WvOauth.TokenDB.DB_CREATE);
		}

		//数据库版本号DB_VERSION发生变化时调用
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			QLog.log(WvOauth.class, "QLayoutOauth Sqlite onUpgrade");
			//插入多个数据表的变化
			//db.execSQL("DROP TABLE IF EXISTS " + Table1.DB_TABLE);
			onCreate(db);
		}
	}
	
	public static class TokenDB  {
		
		// 创建表
		public static final String DB_CREATE = "CREATE TABLE oauth_token ("
			+ "type INTEGER," 
			+ "id TEXT,"
			+ "token TEXT," 
			+ "expire LONG," 
			+ "name TEXT," 
			+ "photo TEXT" 
			+ ")";
		
		/**
		* 构建ContentValues
		*/
		private ContentValues buildContentValues(WvOauthToken e){
			ContentValues cv = new ContentValues();
			cv.put("type", e.getType());
			cv.put("id", e.getId());
			cv.put("token", e.getToken());
			cv.put("expire", e.getExpireTime());
			cv.put("name", e.getName());
			cv.put("photo", e.getPhoto());
			return cv;
		}
		
		/**
		* 构建实体
		*/
		private WvOauthToken buildEntity(Cursor cs){
			WvOauthToken e = new WvOauthToken();
			e.setType(cs.getInt(0));
			e.setId(cs.getString(1));
			e.setToken(cs.getString(2));
			e.setExpireTime(cs.getLong(3));
			e.setName(cs.getString(4));
			e.setPhoto(cs.getString(5));
			return e;
		}
		
		public TokenDB(Context ctx){
			dbHelper = new Sqlite(ctx);
		}
		
		public void open(boolean writable) throws SQLException {
			if(writable){
				db = dbHelper.getWritableDatabase();
			}else{
				db = dbHelper.getReadableDatabase();
			}
		}
		
		public void close(){
			dbHelper.close();
		}
		
		public void insert(WvOauthToken e) {
			if(queryOne(e) == null){
				db.insert("oauth_token", null, buildContentValues(e));
			}else{
				update(e);
			}
			//db.execSQL("INSERT INTO "+DB_TABLE+"()
		}
		
		public boolean update(WvOauthToken e) {
			return db.update("oauth_token", buildContentValues(e), "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
			//db.execSQL("UPDATE "+DB_TABLE+" SET "+KEY_DATA+" = ? WHERE "+KEY_ID+" = ?", new Object[]{e.data, Integer.valueOf(e.id)})
		}
		
		public boolean delete(WvOauthToken e) {
			return db.delete("oauth_token", "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
			//db.execSQL("DELETE FROM "+DB_TABLE+" WHERE "+KEY_ID+" = ?", new Object[]{Integer.valueOf(id)});
		}
		
		public List<WvOauthToken> queryAll() {
			//Cursor cs = db.query(DB_TABLE, new String[] { KEY_ID, KEY_DATA }, null, null, null, null, null);
			Cursor cs = db.rawQuery("SELECT * FROM oauth_token", null);
			List<WvOauthToken> es = new ArrayList<WvOauthToken>(cs.getCount());
			while(cs.moveToNext()){
				es.add(buildEntity(cs));
			}
			return es;
		}
		
		public WvOauthToken queryOne(WvOauthToken e) throws SQLException {
			//Cursor cs = db.query(true, DB_TABLE, new String[] { KEY_ID, KEY_DATA }, KEY_ID + "=" + id, null, null, null,null, null);
			Cursor cs = db.rawQuery("SELECT * FROM oauth_token WHERE type = ? AND id = ?", new String[]{String.valueOf(e.getType()), e.getId()});
			if(cs.moveToNext()){
				return buildEntity(cs);
			}else{
				return null;
			}
		}
		
		private SQLiteDatabase db; // 执行open（）打开数据库时，保存返回的数据库对象
		private SQLiteOpenHelper dbHelper;// 由SQLiteOpenHelper继承过来
		
	}
	
	
	
}
