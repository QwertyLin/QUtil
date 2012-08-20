package q.util.a.view;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.RadioGroup.OnCheckedChangeListener;

public abstract class QTabActivity extends TabActivity implements OnCheckedChangeListener {
	
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent[] intent = onInitIntent();
		//
		tabHost = new TabHost(this);
		tabHost.setId(android.R.id.tabhost);
		//
		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);
		tabHost.addView(linear);
		//
		TabWidget tabwidget = new TabWidget(this);
		tabwidget.setId(android.R.id.tabs);
		tabwidget.setVisibility(View.GONE);
		linear.addView(tabwidget);
		//
		if(getTabDirection(0, 1) == 0){
			linear.addView(initRadioGroup(intent.length));
			linear.addView(initFrameLayout());
		}else{
			linear.addView(initFrameLayout());
			linear.addView(initRadioGroup(intent.length));
		}
		//
		setContentView(tabHost);
		//
		for(int i = 0, size = intent.length; i < size; i++){
			tabHost.addTab(tabHost.newTabSpec("").setIndicator("").setContent(intent[i]));
		}
		tabHost.setCurrentTab(0);
	}
	
	protected abstract Intent[] onInitIntent();
	protected abstract void onInitRadioButton(RadioButton rbtn, int position);
	protected abstract int getTabDirection(int top, int bottom);
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		tabHost.setCurrentTab(checkedId);
	}
	
	private FrameLayout initFrameLayout(){
		FrameLayout frame = new FrameLayout(this);
		frame.setId(android.R.id.tabcontent);
		frame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, 1));
		return frame;
	}
	
	private RadioGroup initRadioGroup(int size){
		//
		RadioGroup rg = new RadioGroup(this);
		rg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		rg.setOrientation(RadioGroup.HORIZONTAL);
		rg.setOnCheckedChangeListener(this);
		//
		RadioGroup.LayoutParams rglp0w1 = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, 1);
		Bitmap rbtnBmNull = null;
		BitmapDrawable rbtnDrawableNull = new BitmapDrawable(rbtnBmNull);
		RadioButton rbtn;
		for(int i = 0; i < size; i++){
			rbtn = new RadioButton(this);
			rbtn.setId(i);
			rbtn.setLayoutParams(rglp0w1);
			rbtn.setButtonDrawable(rbtnDrawableNull);
			rbtn.setGravity(Gravity.CENTER_HORIZONTAL);
			rbtn.setPadding(0, 0, 0, 0);
			onInitRadioButton(rbtn, i);
			rg.addView(rbtn);
		}
		rg.check(0);
		return rg;
	}
}
