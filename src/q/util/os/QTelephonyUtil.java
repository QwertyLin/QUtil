package q.util.os;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import q.util.QUtil;
import q.util.http.QHttpUtil;
import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class QTelephonyUtil {

	//http://code.google.com/p/gears/wiki/GeolocationAPI
	public String googleGeolocation(Context ctx) throws JSONException, IOException {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		//
		int cid = 0; //
		int lac = 0; //
		int mcc = 0; //
		int mnc = 0; //
		String radioType = null; // 运营商
		switch (tm.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			CdmaCellLocation cdma = (CdmaCellLocation) tm.getCellLocation();
			if (cdma == null) {
				return null;
			}
			cid = cdma.getBaseStationId();
			lac = cdma.getNetworkId();
			mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
			mnc = cdma.getSystemId();
			radioType = "cdma";
			break;
		// case TelephonyManager.NETWORK_TYPE_GPRS:
		// case TelephonyManager.NETWORK_TYPE_EDGE:
		// case TelephonyManager.NETWORK_TYPE_HSDPA:
		default:
			GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
			if (gcl == null) {
				return null;
			}
			cid = gcl.getCid();
			lac = gcl.getLac();
			mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
			mnc = Integer.valueOf(tm.getNetworkOperator().substring(3, 5));
			radioType = "gsm";
			break;
		}
		// 获得邻近基站信息
		int[] cidN = null;
		int[] lacN = null;
		List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
		int size = list.size();
		if (size > 0) {
			cidN = new int[size];
			lacN = new int[size];
			for (int i = 0; i < size; i++) {
				cidN[i] = list.get(i).getCid();
				lacN[i] = list.get(i).getLac();
			}
		}

		JSONObject json = new JSONObject();
		json.put("version", "1.1.0");
		json.put("host", "maps.google.com");
		json.put("home_mobile_country_code", mcc);
		json.put("home_mobile_network_code", mnc);
		json.put("radio_type", radioType);
		json.put("request_address", true);
		if (mcc == 460) {
			json.put("address_language", "zh_CN");
		} else {
			json.put("address_language", "en_US");
		}
		//
		JSONArray cells = new JSONArray();
		json.put("cell_towers", cells);
		JSONObject cell = new JSONObject();
		cell.put("cell_id", cid);
		cell.put("location_area_code", lac);
		cell.put("mobile_country_code", mcc);
		cell.put("mobile_network_code", mnc);
		cell.put("age", 0);
		cell.put("signal_strength", -60);
		cell.put("timing_advance", 5555);
		cells.put(cell);
		if (cidN != null && lacN != null) {
			for (int i = 0; i < size; i++) {
				cell = new JSONObject();
				cell.put("cell_id", cidN[i]);
				cell.put("location_area_code", lacN[i]);
				cell.put("mobile_country_code", mcc);
				cell.put("mobile_network_code", mnc);
				cell.put("age", 0);
				cells.put(cell);
			}
		}
		//
		System.out.println(json.toString());
		return QUtil.http.util.post("http://www.google.com/loc/json", json.toString());
	}
}
