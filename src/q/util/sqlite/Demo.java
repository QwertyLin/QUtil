package q.util.sqlite;

import q.util.QUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Demo extends QUtil.sqlite.base<Demo.Entity> {


	public Demo(Context ctx) {
		super(ctx);
	}

	// 创建表
	public static final String DB_CREATE = "CREATE TABLE tableName ("
			+ "id INTEGER PRIMARY KEY," 
			+ "name TEXT" 
			+ ")";
	
	@Override
	protected String getTableName() {
		return "tableName";
	}
	
	
	/**
	 * 构建实体
	 */
	protected Entity buildEntity(Cursor cs){
		Entity e = new Entity();
		e.id = cs.getInt(0);
		e.name = cs.getString(1);
		return e;
	}
		
	/**
	 * 假设的实体
	 */
	public class Entity extends QUtil.sqlite.entity {
		public String name;
	}

	@Override
	protected ContentValues buildContentValues(Entity e) {
		ContentValues cv = new ContentValues();
		cv.put("id", e.id);
		cv.put("name", e.name);
		return cv;
	}

}