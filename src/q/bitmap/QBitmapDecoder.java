package q.bitmap;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

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
	 * 按指定尺度解码Bitmap（宽松），只能缩小不能放大，最后的图片比指定尺寸稍微偏大。
	 * @param width 值为0时只考虑高度
	 * @param height 值为0时只考虑宽度
	 */
	public static final Bitmap deLoose(String file, int width, int height) {
		if(file == null){
			return null;
		}
		if(width == 0 && height == 0){
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, opts);
		return BitmapFactory.decodeFile(file, deLooseOptions(opts, width, height));
	}
	
	/**
	 * 按指定尺度解码Bitmap（宽松），只能缩小不能放大，最后的图片比指定尺寸稍微偏大。
	 * @param uri 通过ContextProvider获得的uri
	 * @param width 值为0时只考虑高度
	 * @param height 值为0时只考虑宽度
	 */
	public static final Bitmap deLoose(Context ctx, Uri uri, int width, int height) {
		if(ctx == null || uri == null){
			return null;
		}
		if(width == 0 && height == 0){
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, opts);
			return BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, deLooseOptions(opts, width, height));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final BitmapFactory.Options deLooseOptions(BitmapFactory.Options opts, int width, int height){
		int s1 = 0, s2 = 0;
		if(width != 0){
			s1 = opts.outWidth / width;//原宽度与期望宽度的比例
		}
		if(height != 0){
			s2 = opts.outHeight / height;
		}
		int s = s1 > s2 ? s1 : s2; //取大值
		if (s == 0) {
			s = 1;
		}
		opts = new BitmapFactory.Options();
		opts.inSampleSize = s;
		return opts;
	}
	
	/**
	 * 在指定尺度内(不超出)解码Bitmap，可放大缩小。
	 * @param width 值为0时只考虑高度
	 * @param height 值为0时只考虑宽度
	 */
	public static final Bitmap deInSize(Bitmap bm, int width, int height) {
		if(bm == null){
			return null;
		}
		if(width == 0 && height == 0){
			return null;
		}
		if(bm.getWidth() == width && bm.getHeight() == height){
			return bm;
		}
		float scale1 = 0, scale2 = 0;
		if(width != 0){
			scale1 = 1f * width / bm.getWidth();
		}
		if(height != 0){
			scale2 = 1f * height / bm.getHeight();
		}
		if(scale1 == 0){
			scale1 = scale2;
		}
		if(scale2 == 0){
			scale2 = scale1;
		}
		float scale = scale1 < scale2 ? scale1 : scale2;//取小值
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}
	
	/**
	 * 在指定尺度内(不超出)解码Bitmap，可放大缩小。
	 * @param width 值为0时只考虑高度
	 * @param height 值为0时只考虑宽度
	 */
	public static final Bitmap deInSize(String file, int width, int height) {
		if(file == null){
			return null;
		}
		if(width == 0 && height == 0){
			return null;
		}
		Bitmap temp = deLoose(file, width, height);
		Bitmap bm = deInSize(temp, width, height);
		BitmapUtil.recycle(temp);
		return bm;
	}
	
	/**
	 * 在指定尺度内(不超出)解码Bitmap，可放大缩小。
	 * @param width 值为0时只考虑高度
	 * @param height 值为0时只考虑宽度
	 */
	public static final Bitmap deInSize(Context ctx, Uri uri, int width, int height) {
		if(ctx == null || uri == null){
			return null;
		}
		if(width == 0 && height == 0){
			return null;
		}
		Bitmap temp = deLoose(ctx, uri, width, height);
		Bitmap bm = deInSize(temp, width, height);
		BitmapUtil.recycle(temp);
		return bm;
	}
	
}
