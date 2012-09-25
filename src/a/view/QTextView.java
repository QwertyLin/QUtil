package a.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

public class QTextView {
	
	/**
	 * 去掉HTML中的超链接和样式等
	 */
	public static final void clearHtml(final TextView tv){
		CharSequence text = Html.fromHtml(tv.getText().toString());
		SpannableStringBuilder newStyleText = new SpannableStringBuilder(text);  
		newStyleText.clearSpans();
		tv.setMovementMethod(null);
        tv.setText(newStyleText);   
	}
	
	/**
	 * HTML化
	 */
	public static final void formatHtml(TextView tv){
		tv.setText(Html.fromHtml(tv.getText().toString()));
	}

	/**
	 * 带超链接的HTML化
	 * 
	 * @param mCtx
	 * @param tv
	 * @param anchorColor 超链接颜色
	 * @param isAnchorUnderline 超链接是否带下划线
	 */
	public interface AnchorClickListener {
		void onClick(Uri uri, String text);
	}
	public static final void formatHtmlWithAnchor(final Context ctx, final TextView tv, final int anchorColor, final boolean isAnchorUnderline, final AnchorClickListener listener){
		Spanned spd = Html.fromHtml(tv.getText().toString());
		CharSequence text = spd;
		//设置可点击
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		//新建样式
		final SpannableStringBuilder newStyleText=new SpannableStringBuilder(text);   
        newStyleText.clearSpans();//超链接必须cleanSpan，否则会自动调用浏览器
		//超链接处理
        if(text instanceof Spannable){   
            Spannable sp = (Spannable) spd;   
            URLSpan[] urls=sp.getSpans(0, text.length(), URLSpan.class);    
            for(final URLSpan url : urls){   
                newStyleText.setSpan(new ClickableSpan() {
                	@Override
                	public void updateDrawState(TextPaint ds) {
                		ds.setColor(anchorColor);//字体颜色
                	    ds.setUnderlineText(isAnchorUnderline); //下划线
                	}
					@Override
					public void onClick(View widget) {
						listener.onClick(Uri.parse(url.getURL()), newStyleText.toString());
					}
				}, sp.getSpanStart(url),sp.getSpanEnd(url),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);   
            }   
        }
        tv.setText(newStyleText);
	}
	
	public static final void formatHtmlWithImage(final Context ctx, final TextView tv, String replaceStr, int resId){
		Spanned spd = Html.fromHtml(tv.getText().toString());
		CharSequence text = spd;
		//设置可点击
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		//新建样式
		final SpannableStringBuilder newStyleText=new SpannableStringBuilder(text);   
		//超链接处理
		ImageGetter imageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				//根据id从资源文件中获取图片对象
				Drawable d = ctx.getResources().getDrawable(id);
				d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
				return d;
			}
		};
		int index = -1;
		String str = newStyleText.toString();
		while( (index = str.indexOf(replaceStr)) != -1){
			newStyleText.replace(index, index + replaceStr.length(), "");
			newStyleText.insert(index, Html.fromHtml("<img src='"+resId+"'/>", imageGetter, null));
			str = newStyleText.toString();
		}
        tv.setText(newStyleText);
	}
}
