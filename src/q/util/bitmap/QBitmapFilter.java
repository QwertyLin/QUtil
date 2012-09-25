package q.util.bitmap;

import q.util.QUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;

public class QBitmapFilter {
	
	/**
	 * 将bitmap不透明的部分填充成指定颜色
	 */
	public static final Bitmap fillColor(Bitmap bm, int color2){
		int width = bm.getWidth(), height = bm.getHeight();
		int[] colors = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	int color = bm.getPixel(x, y); 
            	if(color != 0) {
            		colors[y * width + x] = color2;
            	}
            }
        }
        return Bitmap.createBitmap(colors, width, height, Config.ARGB_8888);
	}
	
	/**
	 * 倒影
	 * @param bm
	 * @param heightRatio 原图与倒影的比例
	 * @param gapRatio 原图与空隙的比例
	 * @return
	 */
	public static final Bitmap reflect(Bitmap bm, int heightRatio, int gapRatio){
		//倒影与原图之间的空隙
		int width = bm.getWidth();
		int height = bm.getHeight();
		final int reflectHeight = height / heightRatio;//倒影高度
		final int gap = height / gapRatio;
		
		Bitmap newBm = Bitmap.createBitmap(width, height + reflectHeight, Config.ARGB_8888);
		//创建倒影图像
		Matrix matrix = new Matrix();
		matrix.setScale(1, -1);//垂直反转
		Bitmap reflection = Bitmap.createBitmap(bm, 0, height - reflectHeight, width, reflectHeight, matrix, false);
		//先画原图，再画倒影
		Canvas canvas = new Canvas(newBm);
		canvas.drawBitmap(bm, 0, 0, null);
		canvas.drawBitmap(reflection, 0, height + gap, null);
		//倒影添加渐变
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, height, 0, newBm.getHeight() + gap, //渐变
				0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, newBm.getHeight() + gap, paint);
		//
		QUtil.bitmap.util.recycle(reflection);
		return newBm;
	}
	
	/**
	 * 圆角
	 * @param bm
	 * @param round
	 * @return
	 */
	public static final Bitmap roundCorner(Bitmap bm, int round){
		//圆角弧度
		int width = bm.getWidth();
		int height = bm.getHeight();
		Bitmap newBm = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		//画圆角
		Paint paint = new Paint();
		paint.setAntiAlias(true);//抗锯齿
		//paint.setColor(color);
		Rect rect = new Rect(0, 0, width, height);
		Canvas canvas = new Canvas(newBm);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(new RectF(rect), round, round, paint);
		//合并圆角与图片
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));//使用图像合成的16条Porter-Duff规则的任意一条来控制Paint如何与已有的Canvas图像进行交互。
		canvas.drawBitmap(bm, rect, rect, paint);
		return newBm;
	}
	
	/**
	 * 加边框
	 * @param bm
	 * @param border 边框大小
	 * @return
	 */
	public static final Bitmap border(Bitmap bm, int border){
		final int color = 0xFFFF0000;
		int width = bm.getWidth(), height = bm.getHeight();
		//final int size = (width > height ? width : height) * 20 / 750;//边框大小
		Bitmap newBm = bm.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(newBm);         
        Paint paint = new Paint();  
        paint.setAntiAlias(true);
        paint.setColor(color);
        //上边
        canvas.drawRect(0, 0, width, border, paint);
        //下边
        canvas.drawRect(0, height - border, width, height, paint);
        //左边
        canvas.drawRect(0, 0, border, height, paint);
        //右边
        canvas.drawRect(width - border, 0, width, height, paint);    
        return newBm;
	}

}
