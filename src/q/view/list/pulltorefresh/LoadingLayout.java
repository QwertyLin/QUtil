/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package q.view.list.pulltorefresh;

import q.util.R;
import q.view.list.pulltorefresh.PullToRefreshBase.Mode;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class LoadingLayout extends FrameLayout {
	
	private static final String
		PULL_DOWN_TEXT = "下拉刷新...", 
		PULL_DOWN_RELEASE_TEXT = "松手刷新...",
		PULL_DOWN_REFRESHING_TEXT = "载入中...", 
		PULL_UP_TEXT = "上拉加载更多...", 
		PULL_UP_RELEASE_TEXT = "松手加载更多...",
		PULL_UP_REFRESHING_TEXT = "载入中...";
	
	private static final int 
		textColor = 0xFF000000, //提示文字字体颜色
		subTextColor = 0xFF000000,//刷新时间字体颜色
		drawable = R.drawable.view_list_pulltorefresh_icon;//下拉图片
		
	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 600;

	private final ImageView mHeaderImage;
	private final Matrix mHeaderImageMatrix;

	private final TextView mHeaderText;
	private final TextView mSubHeaderText;

	private String mPullLabel;
	private String mRefreshingLabel;
	private String mReleaseLabel;

	private float mRotationPivotX, mRotationPivotY;

	private final Animation mRotateAnimation;

	public LoadingLayout(Context context, final Mode mode) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_list_pulltorefresh, this);
		mHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		mSubHeaderText = (TextView) header.findViewById(R.id.pull_to_refresh_sub_text);
		mHeaderImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);

		switch (mode) {
			case PULL_UP_TO_REFRESH:
				// Load in labels
				mPullLabel =  PULL_UP_TEXT;
				mRefreshingLabel = PULL_UP_REFRESHING_TEXT;
				mReleaseLabel = PULL_UP_RELEASE_TEXT;
				break;

			case PULL_DOWN_TO_REFRESH:
			default:
				// Load in labels
				mPullLabel = PULL_DOWN_TEXT;
				mRefreshingLabel = PULL_DOWN_REFRESHING_TEXT;
				mReleaseLabel = PULL_DOWN_RELEASE_TEXT;
				break;
		}

		setTextColor(ColorStateList.valueOf(textColor));
		setSubTextColor(ColorStateList.valueOf(subTextColor));
		Drawable imageDrawable = context.getResources().getDrawable(drawable);
		setLoadingDrawable(imageDrawable);

		reset();
	}

	public void reset() {
		mHeaderText.setText(wrapHtmlLabel(mPullLabel));
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderImage.clearAnimation();

		resetImageRotation();

		if (TextUtils.isEmpty(mSubHeaderText.getText())) {
			mSubHeaderText.setVisibility(View.GONE);
		} else {
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	public void releaseToRefresh() {
		mHeaderText.setText(wrapHtmlLabel(mReleaseLabel));
	}

	public void setPullLabel(String pullLabel) {
		mPullLabel = pullLabel;
	}

	public void refreshing() {
		mHeaderText.setText(wrapHtmlLabel(mRefreshingLabel));
		mHeaderImage.startAnimation(mRotateAnimation);

		mSubHeaderText.setVisibility(View.GONE);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		mRefreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		mReleaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		mHeaderText.setText(wrapHtmlLabel(mPullLabel));
	}

	public void setTextColor(ColorStateList color) {
		mHeaderText.setTextColor(color);
		mSubHeaderText.setTextColor(color);
	}

	public void setSubTextColor(ColorStateList color) {
		mSubHeaderText.setTextColor(color);
	}

	public void setTextColor(int color) {
		setTextColor(ColorStateList.valueOf(color));
	}

	public void setLoadingDrawable(Drawable imageDrawable) {
		// Set Drawable, and save width/height
		mHeaderImage.setImageDrawable(imageDrawable);
		mRotationPivotX = imageDrawable.getIntrinsicWidth() / 2f;
		mRotationPivotY = imageDrawable.getIntrinsicHeight() / 2f;
	}

	public void setSubTextColor(int color) {
		setSubTextColor(ColorStateList.valueOf(color));
	}

	public void setSubHeaderText(CharSequence label) {
		if (TextUtils.isEmpty(label)) {
			mSubHeaderText.setVisibility(View.GONE);
		} else {
			mSubHeaderText.setText(label);
			mSubHeaderText.setVisibility(View.VISIBLE);
		}
	}

	public void onPullY(float scaleOfHeight) {
		mHeaderImageMatrix.setRotate(scaleOfHeight * 90, mRotationPivotX, mRotationPivotY);
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	private void resetImageRotation() {
		mHeaderImageMatrix.reset();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}
	
	private CharSequence wrapHtmlLabel(String label) {
		if (!isInEditMode()) {
			return Html.fromHtml(label);
		} else {
			return label;
		}
	}
}
