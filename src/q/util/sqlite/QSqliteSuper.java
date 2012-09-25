package q.util.sqlite;

import java.util.ArrayList;

import q.util.QUtil;
import q.util.sqlite.Demo.Entity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class QSqliteSuper<T extends QUtil.sqlite.entity> {
	
	public QSqliteSuper(Context ctx){
		dbHelper = QSqliteManager.getInstance(ctx);
	}
	
	protected SQLiteDatabase db; // 执行open（）打开数据库时，保存返回的数据库对象
	private SQLiteOpenHelper dbHelper;// 由SQLiteOpenHelper继承过来
	
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
	
	public long insert(T e) {
		return db.insert(getTableName(), "id", buildContentValues(e));
		//db.execSQL("INSERT INTO "+DB_TABLE+"()
	}
	
	public boolean update(T e) {
		return db.update(getTableName(), buildContentValues(e), "id=" + e.getId(), null) > 0;
		//db.execSQL("UPDATE "+DB_TABLE+" SET "+KEY_DATA+" = ? WHERE "+KEY_ID+" = ?", new Object[]{e.data, Integer.valueOf(e.id)})
	}
	
	public boolean delete(int id) {
		return db.delete(getTableName(), "id=" + id, null) > 0;
		//db.execSQL("DELETE FROM "+DB_TABLE+" WHERE "+KEY_ID+" = ?", new Object[]{Integer.valueOf(id)});
	}
	
	public ArrayList<T> queryAll() {
		//Cursor cs = db.query(DB_TABLE, new String[] { KEY_ID, KEY_DATA }, null, null, null, null, null);
		Cursor cs = db.rawQuery("SELECT * FROM " + getTableName(), null);
		ArrayList<T> list = new ArrayList<T>();
		while(cs.moveToNext()){
			list.add(buildEntity(cs));
		}
		if(list.size() == 0){
			return null;
		}
		return list;
	}
	
	public T query(int id) throws SQLException {
		//Cursor cs = db.query(true, DB_TABLE, new String[] { KEY_ID, KEY_DATA }, KEY_ID + "=" + id, null, null, null,null, null);
		Cursor cs = db.rawQuery("SELECT * FROM " + getTableName() + " WHERE id= ?", new String[]{String.valueOf(id)});
		if(cs.moveToNext()){
			return buildEntity(cs);
		}else{
			return null;
		}
	}
	
	/**
	 * 表名
	 */
	protected abstract String getTableName();
	/**
	 * 构建ContentValues
	 */
	protected abstract ContentValues buildContentValues(T entity);
	/**
	 * 构建实体
	 */
	protected abstract T buildEntity(Cursor cs);
	
	private static class QSqliteManager extends SQLiteOpenHelper {
		
		private static QSqliteManager instance;
		public static QSqliteManager getInstance(Context ctx){
			if(instance == null){
				synchronized (QSqliteManager.class) {
					if(instance == null){
						instance = new QSqliteManager(ctx);
					}
				}
			}
			return instance;
		}
		
		private static final String DB_NAME = "db.db";	//数据库名称
		private static final int DB_VERSION = 1; 			//数据库版本 
		   	
		//调用getWritableDatabase()或 getReadableDatabase()方法时创建数据库
		private QSqliteManager(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		// 函数在数据库第一次建立时被调用， 一般用来用来创建数据库中的表，并做适当的初始化工作
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("QSqlite", "onCreate");
			//可插入多个
			//db.execSQL(Demo.DB_CREATE);
			//db.execSQL(QLayoutOauth.TokenDB.DB_CREATE);
		}

		//数据库版本号DB_VERSION发生变化时调用
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("QSqlite", "onUpgrade");
			//插入多个数据表的变化
			//db.execSQL("DROP TABLE IF EXISTS " + Table1.DB_TABLE);
			onCreate(db);
		}

		

	}
}
