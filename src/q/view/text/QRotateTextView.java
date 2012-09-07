package q.view.text;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 旋转的TextView
 *
 */
public class QRotateTextView extends TextView {
	
	private float degree;
	private float width, height;
	
	public QRotateTextView(Context context, float degree) {
		super(context);
		this.degree = degree;
		this.setGravity(Gravity.CENTER);
	}

	public QRotateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setGravity(Gravity.CENTER);
	}

	/**
	 * 设置旋转的角度
	 * @param degree
	 */
	public void setRotate(float degree){
		this.degree = degree;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(degree, width/2, height/2);
		super.onDraw(canvas);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(width == 0){
			width = getWidth();
			height = getHeight();
		}
	}

}
