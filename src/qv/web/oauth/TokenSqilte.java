package qv.web.oauth;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TokenSqilte extends SQLiteOpenHelper {
   	
	public TokenSqilte(Context context) {
		super(context, "oauth.db", null, 1);
	}
	
	@Override
		public void onCreate(SQLiteDatabase db) {
		//QLog.log(this, "QLayoutOauth Sqlite onCreate");
		db.execSQL(DB_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//QLog.log(this, "QLayoutOauth Sqlite onUpgrade");
	}
	
	private static final String DB_CREATE = "CREATE TABLE oauth_token ("
		+ "type INTEGER," 
		+ "id TEXT,"
		+ "token TEXT," 
		+ "expire LONG," 
		+ "name TEXT," 
		+ "photo TEXT" 
		+ ")";
		
	private ContentValues buildContentValues(Token e){
		ContentValues cv = new ContentValues();
		cv.put("type", e.getType());
		cv.put("id", e.getId());
		cv.put("token", e.getToken());
		cv.put("expire", e.getExpireTime());
		cv.put("name", e.getName());
		cv.put("photo", e.getPhoto());
		return cv;
	}
		
	private Token buildEntity(Cursor cs){
		Token e = new Token();
		e.setType(cs.getInt(0));
		e.setId(cs.getString(1));
		e.setToken(cs.getString(2));
		e.setExpireTime(cs.getLong(3));
		e.setName(cs.getString(4));
		e.setPhoto(cs.getString(5));
		return e;
	}
		
	public void open(boolean writable) throws SQLException {
		if(writable){
			db = getWritableDatabase();
		}else{
			db = getReadableDatabase();
		}
	}
	
	public void close(){
		db.close();
		super.close();
	}
		
	public void insert(Token e) {
		if(queryOne(e) == null){
			db.insert("oauth_token", null, buildContentValues(e));
		}else{
			update(e);
		}
		//db.execSQL("INSERT INTO "+DB_TABLE+"()
	}
		
	public boolean update(Token e) {
		//return db.update("oauth_token", buildContentValues(e), "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
		return db.update("oauth_token", buildContentValues(e), "type=" + e.getType(), null) > 0;
		//db.execSQL("UPDATE "+DB_TABLE+" SET "+KEY_DATA+" = ? WHERE "+KEY_ID+" = ?", new Object[]{e.data, Integer.valueOf(e.id)})
	}
		
	public boolean delete(Token e) {
		//return db.delete("oauth_token", "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
		return db.delete("oauth_token", "type=" + e.getType(), null) > 0;
		//db.execSQL("DELETE FROM "+DB_TABLE+" WHERE "+KEY_ID+" = ?", new Object[]{Integer.valueOf(id)});
	}
		
	public boolean deleteByType(int type) {
		//return db.delete("oauth_token", "type=" + e.getType() + " AND id=" + e.getId(), null) > 0;
		return db.delete("oauth_token", "type=" + type, null) > 0;
		//db.execSQL("DELETE FROM "+DB_TABLE+" WHERE "+KEY_ID+" = ?", new Object[]{Integer.valueOf(id)});
	}
		
	public List<Token> queryAll() {
		//Cursor cs = db.query(DB_TABLE, new String[] { KEY_ID, KEY_DATA }, null, null, null, null, null);
		Cursor cs = db.rawQuery("SELECT * FROM oauth_token", null);
		List<Token> es = new ArrayList<Token>();
		Token token;
		while(cs.moveToNext()){
			token = buildEntity(cs);
			if(!OauthHandle.isTokenExpire(token)){
				es.add(token);
			}
		}
		return es;
	}
		
	public Token queryOne(Token e) throws SQLException {
		//Cursor cs = db.query(true, DB_TABLE, new String[] { KEY_ID, KEY_DATA }, KEY_ID + "=" + id, null, null, null,null, null);
		//Cursor cs = db.rawQuery("SELECT * FROM oauth_token WHERE type = ? AND id = ?", new String[]{String.valueOf(e.getType()), e.getId()});
		Cursor cs = db.rawQuery("SELECT * FROM oauth_token WHERE type = ?", new String[]{String.valueOf(e.getType())});
		if(cs.moveToNext()){
			return buildEntity(cs);
		}else{
			return null;
		}
	}
		
	private SQLiteDatabase db;
}