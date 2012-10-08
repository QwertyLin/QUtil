package q.bitmap;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class QBitmapFilterMatrix {

	/**
	 * 旋转
	 */
	public static final Bitmap rotate(Bitmap bm, float degrees){
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
	}
	
	/**
	 * 缩放
	 * @param width 目标宽度，高度优先时传0
	 * @param height 目标高度，宽度优先时传0
	 * @return
	 */
	public static final Bitmap scale(Bitmap bm, float width, float height){
		Matrix matrix = new Matrix();
		if(width == 0){
			float scale = height / bm.getHeight();
			matrix.setScale(scale, scale);
		}else if(height == 0){
			float scale = width / bm.getWidth();
			matrix.setScale(scale, scale);
		}else{
			matrix.setScale(width / bm.getWidth(), height / bm.getHeight());
		}
		return  Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
	}
	
	/**
	 * 水平或垂直反转
	 * @param bm
	 * @param direction 方向，true表水平，false表垂直
	 * @return
	 */
	public static final Bitmap reverse(Bitmap bm, boolean direction){
		Matrix matrix = new Matrix();
		if(direction){//水平
			matrix.setScale(-1, 1);
		}else{//垂直
			matrix.setScale(1, -1);
		}
		return  Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
	}
	
}
