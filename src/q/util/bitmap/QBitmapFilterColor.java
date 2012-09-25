package q.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class QBitmapFilterColor {

	/**
	 * 颜色过滤器ColorFilter
	 */
	private static final Bitmap colorFilter(Bitmap bitmap, float[] values) {
		Bitmap newBitmap  = bitmap.copy(Bitmap.Config.ARGB_8888, true);	
		Canvas canvas = new Canvas(newBitmap);		
		Paint paint = new Paint();		
		paint.setAntiAlias(true);
		paint.setColorFilter(new ColorMatrixColorFilter(values));	
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return newBitmap;		
	}
	
	/**
	 * 调节亮度
	 *  
	 * @param value 亮度值，范围从暗到亮[-255, 255]
	 */  
	public static final Bitmap brightness(Bitmap bitmap, int value) {
		value = Math.max( -255, Math.min( value, 255 ) );
		float[] brightness = new float[]{
				1, 0, 0, 0, value,
				0, 1, 0, 0, value,
				0, 0, 1, 0, value,
				0, 0, 0, 1, 0,
				0, 0, 0, 0, 1
		};
		return colorFilter(bitmap, brightness);		
	}
	
	/**
	 * 调节对比度	
	 *
	 * @param value 对比度值，范围在[-100, 100]。
	 */  
	public static Bitmap contrast(Bitmap bitmap, int value) {
		value = Math.max( -100, Math.min( value, 100 ) );
		float[] DELTA_INDEX = new float[]  {
				0f,    0.01f, 0.02f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.1f,  0.11f,
				0.12f, 0.14f, 0.15f, 0.16f, 0.17f, 0.18f, 0.20f, 0.21f, 0.22f, 0.24f,
				0.25f, 0.27f, 0.28f, 0.30f, 0.32f, 0.34f, 0.36f, 0.38f, 0.40f, 0.42f,
				0.44f, 0.46f, 0.48f, 0.5f,  0.53f, 0.56f, 0.59f, 0.62f, 0.65f, 0.68f, 
				0.71f, 0.74f, 0.77f, 0.80f, 0.83f, 0.86f, 0.89f, 0.92f, 0.95f, 0.98f,
				1.0f,  1.06f, 1.12f, 1.18f, 1.24f, 1.30f, 1.36f, 1.42f, 1.48f, 1.54f,
				1.60f, 1.66f, 1.72f, 1.78f, 1.84f, 1.90f, 1.96f, 2.0f,  2.12f, 2.25f, 
				2.37f, 2.50f, 2.62f, 2.75f, 2.87f, 3.0f,  3.2f,  3.4f,  3.6f,  3.8f,
				4.0f,  4.3f,  4.7f,  4.9f,  5.0f,  5.5f,  6.0f,  6.5f,  6.8f,  7.0f,
				7.3f,  7.5f,  7.8f,  8.0f,  8.4f,  8.7f,  9.0f,  9.4f,  9.6f,  9.8f, 
				10.0f
			};
		float x = 0;
		if( value < 0 ){
			x = 127 + value * 127 / 100;
		}
		else {
			x = value % 1;
			if( x == 0 ) {
				x = DELTA_INDEX[value];
			} else {
				//x = DELTA_INDEX[(p_val<<0)]; // this is how the IDE does it.
				x = DELTA_INDEX[(value<<0)] * (1 - x) + DELTA_INDEX[(value<<0) + 1] * x; // use linear interpolation for more granularity.
			}
			x = x * 127 + 127;
		}
		float[] contrast = new float[]{
				x/127, 0,     0,     0, 0.5f*(127-x),
				0,     x/127, 0,     0, 0.5f*(127-x),
				0,     0,     x/127, 0, 0.5f*(127-x),
				0,     0,     0,     1, 0,
				0,     0,     0,     0, 1
		};
		return colorFilter(bitmap, contrast);		
	}
	
	/**
	 * 调节饱和度
	 *
	 * @param value 饱和度值，范围在[-100, 100]
	 */  
	public static Bitmap saturation(Bitmap bitmap, int value) {
		value = Math.max( -100, Math.min( value, 100 ) );
		float x = 1 + ((value > 0) ? 3 * value / 100f : value / 100f);
		float lumR = 0.3086f;
		float lumG = 0.6094f;
		float lumB = 0.0820f;
		float[] saturation = new float[]{
				lumR*(1-x)+x, lumG*(1-x),   lumB*(1-x),   0, 0,
				lumR*(1-x),   lumG*(1-x)+x, lumB*(1-x),   0, 0,
				lumR*(1-x),   lumG*(1-x),   lumB*(1-x)+x, 0, 0,
				0,            0,            0,            1, 0,
				0,            0,            0,            0, 1
		};
		return colorFilter(bitmap, saturation);		
	}
	
	/**
	 * 底片效果
	 */  
	public static Bitmap negative(Bitmap bitmap) {		
		float[] negative = new float[]{
				-1, 0,  0,  0, 255,
				0,  -1, 0,  0, 255,
				0,  0,  -1, 0, 255,
				0,  0,  0,  1, 1
		};
		return colorFilter(bitmap, negative);	
	}
	
	/**
	 * 灰白效果
	 */
	public static Bitmap gray(Bitmap bitmap) {
		float[] gray = new float[]{
				0.3086f, 0.6094f, 0.0820f, 0, 0,
				0.3086f, 0.6094f, 0.0820f, 0, 0,
				0.3086f, 0.6094f, 0.0820f, 0, 0,
				0,      0,      0,      1, 0
				};		
		return colorFilter(bitmap, gray);
	}
}
