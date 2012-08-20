package q.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QJson {

	/**
	 * 将数组编码成Json字符串
	 */
	public static final String encodeArray(String[] arrays) {
		JSONArray jsonArr = new JSONArray();
		for(String str : arrays) {
			jsonArr.put(str);
		}
		return jsonArr.toString();
	}
	public static final String encodeObject() throws JSONException{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("aKey", "aValue");
		jsonObj.put("bKey", "bValue");
		return jsonObj.toString();
	}
	
	/**
	 * 将Json字符串解码成数组
	 */
	public static final String[] decodeArray(String jsonStr) throws JSONException {
		ArrayList<String> list = new ArrayList<String>();
		JSONArray jsonArr = new JSONArray(jsonStr);
		for(int i=0; i < jsonArr.length(); i++) {
			list.add(jsonArr.getString(i));
		}
		return list.toArray(new String[list.size()]);
	}
}
