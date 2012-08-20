package q.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class QPackage {

	public static List<PackageInfo> list(Context ctx){
		List<PackageInfo> temp = ctx.getPackageManager().getInstalledPackages(0);
		List<PackageInfo> list = new ArrayList<PackageInfo>();
		for(PackageInfo item : temp){
			if(!isHidden(item.packageName)){
				list.add(item);
			}
		}
		return list;
	}
	
	private static boolean isHidden(String packageName) {
		if (packageName == null) {
			return true;
		}
		if (packageName.startsWith("com.google.android.apps.")) {
			return false;
		}
	    for (String prefix : new String[]{"com.android.", "android", "com.google.android.", "com.htc",}) {
			if (packageName.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
}
