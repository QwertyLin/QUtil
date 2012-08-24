package q.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class QIntent {
	
	/**
	 * 打开相册，返回数据调用resultBitmap()
	 */
	public static final void contentImage(Activity act, int requestCode){
		act.startActivityForResult(
				new Intent(Intent.ACTION_GET_CONTENT).addCategory(Intent.CATEGORY_OPENABLE).setType("image/*"), 
				requestCode);
	}
	
	/**
	 * 打开相机，返回数据调用resultBitmap()
	 */
	public static final void mediaCamera(Activity act, int requestCode){
		act.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), requestCode);
	}
	
	/**
	 * 返回Bitmap
	 */
	public static final Bitmap resultBitmap(Context ctx, Intent data){
		if(data != null){
			//2种方式
			try {
				return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(data.getData()));
			} catch (Exception e) {
				e.printStackTrace();
				return data.getParcelableExtra("data");
			}
		}
		return null;
	}
	
	/**
	 * 调用谷歌地图app或在浏览器打开
	 * 
	 * @param lat 纬度
	 * @param lng 经度
	 * @param name 地名
	 */
	public static final void mapGoogle(Context ctx, String lat, String lng, String name){
		boolean isInstallGMap = false;
		List<PackageInfo> packs = ctx.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if (p.versionName == null) { // system packages
				continue;
			}
			if ("com.google.android.apps.maps".equals(p.packageName)) {
				isInstallGMap = true;
				break;
			}
		}
		if (isInstallGMap) {
			Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + lat + "," + lng + "?q=" + (name==null?"":name) ));
			map.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			map.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			ctx.startActivity(map);
		} else {
			Intent it = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q="
							+ lat + "," + lng));
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			ctx.startActivity(it);
		}
	}

}
