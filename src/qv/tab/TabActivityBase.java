package qv.tab;

import q.util.R;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

public abstract class TabActivityBase extends TabActivity implements OnCheckedChangeListener {
	
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tabhost);
		//
		tabHost = getTabHost();
		//
		Intent[] intent = initIntent();
		for(int i = 0, size = intent.length; i < size; i++){
			tabHost.addTab(tabHost.newTabSpec(String.valueOf(i))
					.setIndicator(String.valueOf(i))
					.setContent(intent[i]));
		}
		tabHost.setCurrentTab(0);
		//
		((FrameLayout)findViewById(R.id.tabhost_rg)).addView(initRadioGroup(intent.length));
	}
	
	protected abstract Intent[] initIntent();
	protected abstract void initRadioButton(int position, RadioButton rbtn);
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		tabHost.setCurrentTab(checkedId);
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
			initRadioButton(i, rbtn);
			rg.addView(rbtn);
		}
		rg.check(0);
		return rg;
	}
}
