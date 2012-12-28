package q;

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

import q.code.CodeUtil;
import q.http.HttpUtil;

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
public class QLayoutOauth extends RelativeLayout {
	
	public static final int 
		TYPE_SINA_WEIBO = 1, //http://open.weibo.com/
		TYPE_QQ_WEIBO = 2, //http://dev.t.qq.com/
		TYPE_QQ_ZONE = 3, //http://opensns.qq.com/
		TYPE_RENREN = 4;
		
	
	/**
	 * 未授权
	 */
	public static class UnAuthException extends Exception{
		private static final long serialVersionUID = 1L;
	}
	
	public interface Callback {
		void onSuccess(QLayoutOauth.Token token);
		void onError();
	}
	
	public interface Handle {
		int getType();
		String getAuthUrl();
		String getUrlParsePattern();
		void parseUrl(Activity act, Matcher m, Callback callback);
	}
	
	private Activity act;
	private Handle handle;
	
	public QLayoutOauth(Context ctx, Handle handle, Callback callback) {
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
		QLog.log(QLayoutOauth.class, "auth url:" + handle.getAuthUrl());
		webView.loadUrl(handle.getAuthUrl());
	}

	private ProgressBar layoutLoading;

	
	private void initWebView(final WebView webView, final Callback callback){
		WebSettings set = webView.getSettings();
		set.setJavaScriptEnabled(true);
		set.setSupportZoom(true);
		set.setBuiltInZoomControls(true);
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress > 30 && (layoutLoading.getVisibility()==View.VISIBLE) ) {
					layoutLoading.setVisibility(View.GONE);
				}
			}
		});
		//获得授权码
		WebViewClient wvc = new WebViewClient() {
			int index = 0;
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				QLog.log(QLayoutOauth.class, "url:" + url);
				Pattern p = Pattern.compile(handle.getUrlParsePattern());
				Matcher m = p.matcher(url);
				if (m.find() && index == 0) {
					index++;
					//layoutLoading.setText("授权中");
					layoutLoading.setVisibility(View.VISIBLE);
					CookieManager.getInstance().removeAllCookie();//清除cookie
					webView.setVisibility(View.GONE);
					handle.parseUrl(act, m, callback);
				}
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				QLog.log(QLayoutOauth.class, "shouldOverrideUrlLoading url=" + url);
				if(url.contains("error_uri")  //新浪
					|| url.contains("checkType=error") //QQ微博
					|| url.contains("error=login_denied") //人人
				){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		};
		webView.setWebViewClient(wvc);
	}
	
	private static boolean onCheckToken(Token token){
		if(token.getExpireTime() == 0 || token.getToken() == null || token.getToken().equals("")){
			QLog.log(QLayoutOauth.class, "onCheckToken error");
			return false;
		}
		return true;
	}
	
	protected void Q(){}
	
	public static class Token implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private int type;
		private String token;
		private long expireTime;
		private String id;//id号
		private String name;//昵称
		private String photo; //头像
		
		/**
		 * @return true表已过期，false表未过期
		 */
		public boolean isExpire(){
			long timeRemain = expireTime - new Date().getTime();
			QLog.log(QLayoutOauth.class, "timeRemain:" + timeRemain + " expire:" + expireTime);
			if(timeRemain > 0){ //未过期
				return false;
			}else{ //已过期
				return true;
			}
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public long getExpireTime() {
			return expireTime;
		}
		public void setExpireTime(long expireTime) {
			this.expireTime = expireTime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPhoto() {
			return photo;
		}
		public void setPhoto(String photo) {
			this.photo = photo;
		}
	}
	
	private static class Sqlite extends SQLiteOpenHelper {
				   	
		//调用getWritableDatabase()或 getReadableDatabase()方法时创建数据库
		public Sqlite(Context context) {
			super(context, "oauth.db", null, 1);
		}
		
		// 函数在数据库第一次建立时被调用， 一般用来用来创建数据库中的表，并做适当的初始化工作
		@Override
		public void onCreate(SQLiteDatabase db) {
			QLog.log(QLayoutOauth.class, "QLayoutOauth Sqlite onCreate");
			//可插入多个
			//db.execSQL(Demo.DB_CREATE);
			db.execSQL(QLayoutOauth.TokenDB.DB_CREATE);
		}

		//数据库版本号DB_VERSION发生变化时调用
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			QLog.log(QLayoutOauth.class, "QLayoutOauth Sqlite onUpgrade");
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
		private ContentValues buildContentValues(QLayoutOauth.Token e){
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
		private QLayoutOauth.Token buildEntity(Cursor cs){
			QLayoutOauth.Token e = new QLayoutOauth.Token();
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
		
		public void insert(QLayoutOauth.Token e) {
			if(queryOne(e) == null){
				db.insert("oauth_token", null, buildContentValues(e));
			}else{
				update(e);
			}
			//db.execSQL("INSERT INTO "+DB_TABLE+"()
		}
		
		public boolean update(QLayoutOauth.Token e) {
			return db.update("oauth_token", buildContentValues(e), "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
			//db.execSQL("UPDATE "+DB_TABLE+" SET "+KEY_DATA+" = ? WHERE "+KEY_ID+" = ?", new Object[]{e.data, Integer.valueOf(e.id)})
		}
		
		public boolean delete(QLayoutOauth.Token e) {
			return db.delete("oauth_token", "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
			//db.execSQL("DELETE FROM "+DB_TABLE+" WHERE "+KEY_ID+" = ?", new Object[]{Integer.valueOf(id)});
		}
		
		public List<QLayoutOauth.Token> queryAll() {
			//Cursor cs = db.query(DB_TABLE, new String[] { KEY_ID, KEY_DATA }, null, null, null, null, null);
			Cursor cs = db.rawQuery("SELECT * FROM oauth_token", null);
			List<QLayoutOauth.Token> es = new ArrayList<QLayoutOauth.Token>(cs.getCount());
			while(cs.moveToNext()){
				es.add(buildEntity(cs));
			}
			return es;
		}
		
		public QLayoutOauth.Token queryOne(QLayoutOauth.Token e) throws SQLException {
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
	
	public static class HandleSinaWeibo implements Handle {
		
		private static final String 
			CLIENT_ID = "3811434321", 
			CALLBACK_URL = "http://www.poco.cn";
		
		@Override
		public int getType() {
			return TYPE_SINA_WEIBO;
		}

		@Override
		public String getAuthUrl() {
			return "https://api.weibo.com/oauth2/authorize?"
					+ "client_id=" + CLIENT_ID 
					+ "&redirect_uri=" + CALLBACK_URL 
					+ "&response_type=token"
					+ "&display=mobile";
		}
		
		@Override
		public String getUrlParsePattern() {
			return ".+access_token=(.+)&.+expires_in=(.+)&uid=(.+)";
		}

		@Override
		public void parseUrl(final Activity act, Matcher m, final Callback callback) {
			/*if(true){
				callback.onError();
				return;
			}*/
			final Token token = new Token();
			token.setType(getType());
			token.setToken(m.group(1));
			token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
			token.setId(m.group(3));
			QLog.log(QLayoutOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " uid=" + token.getId());
			if(!onCheckToken(token)){
				callback.onError();
				return;
			}
			if(token.getId() == null || token.getId().equals("")){
				callback.onError();
				return;
			}
			new Thread(){
				public void run() {
					try {
						JSONObject json = new JSONObject(HttpUtil.get(urlUsersShow(token)));
						token.setName(json.getString("screen_name"));
						token.setPhoto(json.getString("profile_image_url"));
						QLog.log(QLayoutOauth.class, "screen_name=" + token.getName() + " profile_image_url=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							callback.onError();
							return;
						}
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onSuccess(token);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onError();
							}
						});
					} 
				};
			}.start();
		}
		
		public static String postText(Token token, String text, String lat, String lng) throws UnAuthException, IOException {
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			//加入空格，否则无法重复发送
			int space = new Random().nextInt(50);
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < space; i++){
				sb.append(" ");
			}
			String param = "access_token=" + token.getToken() + "&status=" + URLEncoder.encode(text + sb, "utf-8");
			//
			if(lat != null && lng != null){
				param += "&lat=" + lat + "&long=" + lng;
			}
			//
			return HttpUtil.post("https://api.weibo.com/2/statuses/update.json", param);
		}
		
		public static String postPic(Token token, String text, String pic, String lat, String lng) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String boundary = "-----114975832116442893661388290519";
			StringBuffer params = new StringBuffer();
			boundary = "\r\n" + "--" + boundary + "\r\n";
			//
			params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
		    params.append(token.getToken());
		    //
		    params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "status" + "\"\r\n\r\n");
			int space = new Random().nextInt(50); //加入空格，否则无法重复发送
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < space; i++){
				sb.append(" ");
			}
		    params.append(text + sb);
		    //
		    if(lat != null && lng != null){
		    	params.append(boundary);
		 		params.append("Content-Disposition: form-data; name=\"" + "lat" + "\"\r\n\r\n");
		 	    params.append(lat);
		 	    //
		 	    params.append(boundary);
		 		params.append("Content-Disposition: form-data; name=\"" + "long" + "\"\r\n\r\n");
		 	    params.append(lng);
		    }
		    //
		    params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "pic" + "\"; filename=\"" + new File(pic).getName() + "\"\r\n");
		    params.append("Content-Type: " + "image/x-png" + "\r\n\r\n");
		    //
			return HttpUtil.post("https://upload.api.weibo.com/2/statuses/upload.json", params.toString(), "-----114975832116442893661388290519", pic);
		}
		
		public static String urlUsersShow(Token token) {
			return "https://api.weibo.com/2/users/show.json?"
						+ "access_token=" + token.getToken()
						+ "&uid=" + token.getId();
		}
		
		public static String urlFriends(Token token, int count, int cursor) throws IOException, UnAuthException{
			return "https://api.weibo.com/2/friendships/friends.json?"
						+ "access_token=" + token.getToken()
						+ "&uid=" + token.getId()
						+ "&count=" + count
						+ "&cursor=" + cursor;
		}

	}

	public static class HandleQQWeibo implements Handle {
		
		private static final String 
			CLIENT_ID = "801140374", 
			CALLBACK_URL = "http:%2F%2Fwww.xxd.cn";
		
		@Override
		public int getType() {
			return TYPE_QQ_WEIBO;
		}
	
		@Override
		public String getAuthUrl() {
			return "https://open.t.qq.com/cgi-bin/oauth2/authorize?" 
					+ "client_id=" + CLIENT_ID 
					+ "&redirect_uri=" + CALLBACK_URL
					+ "&response_type=token";
		}
		
		@Override
		public String getUrlParsePattern() {
			return ".+access_token=(.+)&expires_in=(.+)&openid=(.+)&openkey";
		}
	
		@Override
		public void parseUrl(final Activity act, Matcher m, final Callback callback) {
			final Token token = new Token();
			token.setType(getType());
			token.setToken(m.group(1));
			token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
			token.setId(m.group(3));
			QLog.log(QLayoutOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime() + " openid=" + token.getId());
			if(!onCheckToken(token)){
				callback.onError();
				return;
			}
			if(token.getId() == null || token.getId().equals("")){
				callback.onError();
				return;
			}
			new Thread(){
				public void run() {
					try {
						JSONObject json = new JSONObject(HttpUtil.get(urlUserInfoSimple(token, token.getId())));
						json = json.getJSONObject("data").getJSONArray("info").getJSONObject(0);
						token.setName(json.getString("nick"));
						token.setPhoto(json.getString("head") + "/50");
						QLog.log(QLayoutOauth.class, "nick=" + token.getName() + " head=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							callback.onError();
							return;
						}
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onSuccess(token);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onError();
							}
						});
					} 
				};
			}.start();
		}
		
		public static String postText(Token token, String text) throws UnAuthException, IOException {
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String param = 
					"oauth_consumer_key=" + CLIENT_ID 
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&clientip=127.0.0.1"
					+ "&oauth_version=2.a"
					+ "&format=json"
					+ "&content=" + URLEncoder.encode(text, "utf-8");
			return HttpUtil.post("https://open.t.qq.com/api/t/add", param);
		}
		
		public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String boundary = "-----114975832116442893661388290519";
			StringBuffer params = new StringBuffer();
			boundary = "\r\n" + "--" + boundary + "\r\n";
			//
			params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "oauth_consumer_key" + "\"\r\n\r\n");
	        params.append(CLIENT_ID);
			//
			params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
	        params.append(token.getToken());
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "openid" + "\"\r\n\r\n");
	        params.append(token.getId());
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "oauth_version" + "\"\r\n\r\n");
	        params.append("2.a");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "format" + "\"\r\n\r\n");
	        params.append("json");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "content" + "\"\r\n\r\n");
	        params.append(text);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "clientip" + "\"\r\n\r\n");
	        params.append("127.0.0.1");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "pic" + "\"; filename=\"" + "pic.png" + "\"\r\n");
	        params.append("Content-Type: " + "application/octet-stream" + "\r\n\r\n");
	        //
			return HttpUtil.post("https://open.t.qq.com/api/t/add_pic", params.toString(), "-----114975832116442893661388290519", pic);
		}
		
		public static String urlUserInfoSimple(Token token, String id) {
			return "https://open.t.qq.com/api/user/infos?"
					+ "oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
					+ "&fopenids=" + id
					;
		}
		
		/*public static String urlUserInfo(Token token) {
			return "https://open.t.qq.com/api/user/info?"
					+ "oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
					;
		}*/
		
		public static String urlFriendsIdolistSimple(Token token, int reqnum, int startindex) throws IOException{
			return "https://open.t.qq.com/api/friends/idollist_s?"
					+ "oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
					+ "&reqnum=" + reqnum
					+ "&startindex=" + startindex
					;
		}
		
		/*public static String urlFriends(Token token) throws IOException{
			return "https://open.t.qq.com/api/friends/idollist?"
					+ "oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&clientip=127.0.0.1&oauth_version=2.a&format=json"
					+ "&reqnum=30"
					;
		}*/
	
	}
	
	public static class HandleQQZone implements Handle {
		
		private static final String CLIENT_ID = "100246308";
		
		@Override
		public int getType() {
			return TYPE_QQ_ZONE;
		}

		@Override
		public String getAuthUrl() {
			return "https://graph.qq.com/oauth2.0/authorize?" 
					+ "client_id=" + CLIENT_ID 
					+ "&redirect_uri=tencentauth:%2F%2Fauth.qq.com"
					+ "&response_type=token"
					+ "&display=mobile"
					+ "&scope=get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album";
		}
		
		@Override
		public String getUrlParsePattern() {
			return ".+access_token=(.+)&expires_in=(.+)";
		}

		@Override
		public void parseUrl(final Activity act, Matcher m, final Callback callback) {
			final Token token = new Token();
			token.setType(getType());
			token.setToken(m.group(1));
			token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
			QLog.log(QLayoutOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime());
			if(!onCheckToken(token)){
				callback.onError();
				return;
			}
			new Thread(){
				public void run() {
					try {
						String data = HttpUtil.get("https://graph.qq.com/oauth2.0/me?access_token=" + token.getToken());
						if(data != null){
							String sep = "openid\":\"";
							int startIndex = data.indexOf(sep) + sep.length(); 
							int endIndex = data.indexOf("\"", startIndex);
							token.setId(data.substring(startIndex, endIndex));
						}
						QLog.log(QLayoutOauth.class, "openid=" + token.getId());
						if(token.getId() == null || token.getId().equals("")){
							callback.onError();
							return;
						}
						//
						JSONObject json = new JSONObject(HttpUtil.get(urlUserInfo(token)));
						token.setName(json.getString("nickname"));
						token.setPhoto(json.getString("figureurl_1"));
						QLog.log(QLayoutOauth.class, "nickname=" + token.getName() + " figureurl_1=" + token.getPhoto());
						if(token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							callback.onError();
							return;
						}
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onSuccess(token);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onError();
							}
						});
					} 
				};
			}.start();
		}
		
		public static String postText(Token token, String text) throws UnAuthException, IOException {
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String param = 
					"oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId()
					+ "&con=" + URLEncoder.encode(text, "utf-8")
					+ "&third_source=1"
					;
			return HttpUtil.post("https://graph.qq.com/shuoshuo/add_topic", param);
		}
		
		public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String boundary = "-----114975832116442893661388290519";
			StringBuffer params = new StringBuffer();
			boundary = "\r\n" + "--" + boundary + "\r\n";
			//
			params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
	        params.append(token.getToken());
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "oauth_consumer_key" + "\"\r\n\r\n");
	        params.append(CLIENT_ID);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "openid" + "\"\r\n\r\n");
	        params.append(token.getId());
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "photodesc" + "\"\r\n\r\n");
	        params.append(text);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "title" + "\"\r\n\r\n");
	        params.append(System.currentTimeMillis() + ".png");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "picture" + "\"; filename=\"" + "pic.png" + "\"\r\n");
	        params.append("Content-Type: " + "image/x-png" + "\r\n\r\n");
	        //
			return HttpUtil.post("https://graph.qq.com/photo/upload_pic", params.toString(), "-----114975832116442893661388290519", pic);
	    }
		
		public static String urlUserInfo(Token token) throws IOException, UnAuthException{
			return "https://graph.qq.com/user/get_user_info?"
					+ "oauth_consumer_key=" + CLIENT_ID
					+ "&access_token=" + token.getToken()
					+ "&openid=" + token.getId();
		}

	}
	
	public static class HandleRenren implements Handle {
		
		private static final String 
			RENREN_CLIENT_ID = "ce90074fea9c4650b6c860aaf149c758", 
			RENREN_CLIENT_SECRET = "1edb76174931407f80c25984385f176a";
		
		@Override
		public int getType() {
			return TYPE_RENREN;
		}

		@Override
		public String getAuthUrl() {
			return "http://graph.renren.com/oauth/authorize?" 
					+ "client_id=" + RENREN_CLIENT_ID 
					+ "&redirect_uri=http:%2F%2Fgraph.renren.com%2Foauth%2Flogin_success.html"
					+ "&response_type=token"
					+ "&display=touch"
					+ "&scope=status_update+photo_upload";
		}
		
		@Override
		public String getUrlParsePattern() {
			return ".+access_token=(.+)&expires_in=(.+)&scope";
		}

		@Override
		public void parseUrl(final Activity act, Matcher m, final Callback callback) {
			final Token token = new Token();
			token.setType(getType());
			token.setToken(m.group(1));
			token.setExpireTime(new Date().getTime() + Long.parseLong(m.group(2)) * 1000 );
			QLog.log(QLayoutOauth.class, "access_token=" + token.getToken() + " expires_in=" + token.getExpireTime());
			if(!onCheckToken(token)){
				callback.onError();
				return;
			}
			if(token.getToken().contains("%7C")){//accessToken 人人网特殊处理
				token.setToken(token.getToken().replace("%7C", "|"));
			}
			new Thread(){
				public void run() {
					try {
						JSONArray jsonA = new JSONArray(postUsersInfo(token));
						JSONObject json = jsonA.getJSONObject(0);
						token.setId(json.getString("uid"));
						token.setName(json.getString("name"));
						token.setPhoto(json.getString("tinyurl"));
						QLog.log(QLayoutOauth.class, "uid=" + token.getId() + " name=" + token.getName() + " tinyurl=" + token.getPhoto());
						if(token.getId() == null || token.getId().equals("")
								|| token.getName() == null || token.getName().equals("") 
								|| token.getPhoto() == null || token.getPhoto().equals("")){
							callback.onError();
							return;
						}
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onSuccess(token);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								callback.onError();
							}
						});
					} 
				};
			}.start();
		}
		
		public static String postUsersInfo(Token token) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String md5 = CodeUtil.md5(
					"access_token=" + token.getToken()
					+ "format=JSON"
					+ "method=users.getInfo"
					+ "v=1.0"
					+ RENREN_CLIENT_SECRET);
			String param = 
					"&access_token=" + token.getToken()
					+ "&format=JSON"
					+ "&method=users.getInfo"
					+ "&v=1.0"
					+ "&sig="+md5;
			return HttpUtil.post("http://api.renren.com/restserver.do", param);
		}
		
		public static String postFriends(Token token) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String md5 = CodeUtil.md5(
					"access_token=" + token.getToken()
					+ "format=JSON"
					+ "method=" + "friends.getFriends"
					+ "v=1.0"
					+ RENREN_CLIENT_SECRET);
			String param = 
					"&access_token=" + token.getToken() 
					+ "&format=JSON"
					+ "&method=" + "friends.getFriends"
					+ "&v=1.0"
					+ "&sig="+md5;
			return HttpUtil.post("http://api.renren.com/restserver.do", param);
		}
		
		public static String postText(Token token, String text) throws UnAuthException, IOException {
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String md5 = CodeUtil.md5(
					"access_token="+token.getToken()
					+ "format=JSON"
					+ "method=status.set"
					+ "status="+text
					+ "v=1.0"
					+ RENREN_CLIENT_SECRET);
			String param = "&access_token=" + token.getToken()
					+ "&format=JSON"
					+ "&method=status.set"
					+ "&status="+URLEncoder.encode(text, "utf-8")
					+ "&v=1.0"
					+ "&sig="+md5;
			return HttpUtil.post("http://api.renren.com/restserver.do", param);
		}
		
		public static String postPic(Token token, String text, String pic) throws IOException, UnAuthException{
			if(token == null || token.isExpire()){
				throw new UnAuthException();
			}
			String md5 = CodeUtil.md5(
					"access_token=" + token.getToken()
					+ "api_key="+ RENREN_CLIENT_ID
					+ "caption=" + text
					+ "format=JSON"
					+ "method=photos.upload"
					+ "v=1.0"
					+ RENREN_CLIENT_SECRET);					
			//
			String boundary = "-----------------------------114975832116442893661388290519";
			StringBuffer params = new StringBuffer();
			boundary = "\r\n" + "--" + boundary + "\r\n";
			//
			params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "method" + "\"\r\n\r\n");
	        params.append("photos.upload");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "api_key" + "\"\r\n\r\n");
	        params.append(RENREN_CLIENT_ID);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "access_token" + "\"\r\n\r\n");
	        params.append(token.getToken());
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "sig" + "\"\r\n\r\n");
	        params.append(md5);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "v" + "\"\r\n\r\n");
	        params.append("1.0");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "format" + "\"\r\n\r\n");
	        params.append("JSON");
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "caption" + "\"\r\n\r\n");
	        params.append(text);
	        //
	        params.append(boundary);
			params.append("Content-Disposition: form-data; name=\"" + "upload" + "\"; filename=\"" + "pic.png" + "\"\r\n");
	        params.append("Content-Type: " + "image/png" + "\r\n\r\n");
	        //
			return HttpUtil.post("http://api.renren.com/restserver.do", params.toString(), "-----------------------------114975832116442893661388290519", pic);
	    }

	}
	
}
