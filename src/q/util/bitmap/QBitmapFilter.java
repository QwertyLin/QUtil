package q.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

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

}
