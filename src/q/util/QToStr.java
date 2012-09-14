package q.util;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Camera;

public final class QToStr {

	
	public static final String toStr(Paint p){
		StringBuffer sb = new StringBuffer();
		sb.append(" isAntiAlias=" + p.isAntiAlias());
		sb.append(" alpha=" + p.getAlpha());
		sb.append(" color=" + p.getColor());
		sb.append(" flags=" + p.getFlags());
		sb.append(" stokeWidth=" + p.getStrokeWidth());
		sb.append(" textScaleX=" + p.getTextScaleX());
		sb.append(" textSize=" + p.getTextSize());
		sb.append(" style=" + p.getStyle());
		return sb.toString();
	}
	
	public static final String toStr(Camera c){
		StringBuffer sb = new StringBuffer();
		Camera.Parameters p = c.getParameters();
		sb.append(" preview-size=" + p.getPreviewSize().width + "*" + p.getPreviewSize().height);
		sb.append(" picture-size=" + p.getPictureSize().width + "*" + p.getPictureSize().height);
		return sb.toString();
	}
	
	public static String toStr(Context ctx, List<PackageInfo> list){
		PackageManager pm = ctx.getPackageManager();
		StringBuffer sb = new StringBuffer();
		for(PackageInfo item : list){
			sb.append(item.applicationInfo.loadLabel(pm) + " ");
			sb.append(item.applicationInfo.sourceDir + " ");
			sb.append(item.packageName + " ");
			sb.append("\n");
		}
		return sb.toString();
	}
}
