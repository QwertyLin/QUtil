package q.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class QBitmapDecoder {
	
	/**
	 * 只解码图片的信息，如宽度高度。
	 */
	public static final BitmapFactory.Options deBounds(String file){
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opts);
		return opts;
	}
	
	/**
	 * 按指定宽度解码Bitmap（宽松），只能缩小不能放大。
	 */
	public static final Bitmap deWidthLoose(String file, int width) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opts);
		int s = opts.outWidth / width;//原宽度与期望宽度的比例
		if (s == 0) {
			s = 1;
		}
		opts = new BitmapFactory.Options();
		opts.inSampleSize = s;
		Bitmap bm = BitmapFactory.decodeFile(file, opts);
		return bm;
	}
	
	/**
	 * 按指定宽度解码Bitmap（严格），可放大缩小。
	 */
	public static final Bitmap deWidthStrict(String file, int width) {
		Bitmap temp = deWidthLoose(file, width);
		if(temp.getWidth() != width){
			Matrix matrix = new Matrix();
			float scale = 1f * width / temp.getWidth();
			matrix.setScale(scale, scale);
			Bitmap bm =  Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
			QBitmapUtil.recycle(temp);
			return bm;
		}
		return temp;
	}
	

}
