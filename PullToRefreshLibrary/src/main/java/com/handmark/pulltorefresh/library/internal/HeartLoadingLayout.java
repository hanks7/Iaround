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
package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class HeartLoadingLayout extends LoadingLayout {
	private static final int CLIPDRAWABLE_ZOOM_IN_DURATION = 350;// 心型框缩小的时间
	private static final int HEARTDRAWABLE_ZOOM_OUT_DURATION = 400;// 心型框放大的时间
	private static final int HEARTDRAWABLE_ZOOM_HEART_DURATION = 300;// 心型框心跳的时间

	private ClipDrawable mClipDrawable;
	private AnimatorSet mCurrentAnimationSet;
	private ObjectAnimator mScaleXAnimatorZoomIn, mScaleYAnimatorZoomIn,
			mScaleXAnimatorZoomOut, mScaleYAnimatorZoomOut,
			mScaleXAnimatorHeartZoom, mScaleYAnimatorHeartZoom;
	private DecelerateInterpolator mDecelerateInterpolator;
	private AnimatorListener mClipAnimationListener;

	public HeartLoadingLayout(Context context, Mode mode,
			Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		initAnimation();
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.heart_clip_drawable;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		if (imageDrawable != null && imageDrawable instanceof ClipDrawable) {
			mClipDrawable = (ClipDrawable) imageDrawable;
		} else {
			mClipDrawable = null;
		}
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		if (mClipDrawable != null) {
			if (scaleOfLayout < 0.8f) {
				mClipDrawable.setLevel(0);
			} else if (scaleOfLayout >= 0.8f && scaleOfLayout <= 1.0f) {
				mClipDrawable.setLevel((int) ((scaleOfLayout - 0.8) * 50000));
			} else {
				mClipDrawable.setLevel(10000);
			}
		}
	}

	@Override
	protected void refreshingImpl() {
		startHeart();
	}

	@Override
	protected void resetImpl() {
		if (mCurrentAnimationSet != null) {
			mCurrentAnimationSet.cancel();
			mCurrentAnimationSet.end();
		}

		goneAllViews();
		mHeaderImage.setVisibility(View.VISIBLE);
		mHeaderImage.setImageDrawable(mClipDrawable);
		mHeaderImage.setBackgroundResource(R.drawable.heart_bg);
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	/**
	 * 开启心跳效果
	 */
	private void startHeart() {
		mCurrentAnimationSet = new AnimatorSet();
		mCurrentAnimationSet.playTogether(mScaleXAnimatorZoomIn,
				mScaleYAnimatorZoomIn, mScaleXAnimatorZoomOut,
				mScaleYAnimatorZoomOut, mScaleXAnimatorHeartZoom,
				mScaleYAnimatorHeartZoom);
		mCurrentAnimationSet.start();
	}

	private void initAnimation() {
		mDecelerateInterpolator = new DecelerateInterpolator();// 减速插补器
		mClipAnimationListener = new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// 设置背景为全心
				mHeaderImage.setImageResource(R.drawable.heart_img);
				mHeaderImage.setBackgroundDrawable(null);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
			}
		};

		mScaleXAnimatorZoomIn = ObjectAnimator.ofFloat(mHeaderImage, "scaleX",
				1.0f, 0.0f);
		mScaleXAnimatorZoomIn.setDuration(CLIPDRAWABLE_ZOOM_IN_DURATION);
		mScaleXAnimatorZoomIn.addListener(mClipAnimationListener);

		mScaleYAnimatorZoomIn = ObjectAnimator.ofFloat(mHeaderImage, "scaleY",
				1.0f, 0.0f);
		mScaleYAnimatorZoomIn.setDuration(CLIPDRAWABLE_ZOOM_IN_DURATION);

		mScaleXAnimatorZoomOut = ObjectAnimator.ofFloat(mHeaderImage, "scaleX",
				0.1f, 1.0f);
		mScaleXAnimatorZoomOut.setStartDelay(CLIPDRAWABLE_ZOOM_IN_DURATION);
		mScaleXAnimatorZoomOut.setDuration(HEARTDRAWABLE_ZOOM_OUT_DURATION);

		mScaleYAnimatorZoomOut = ObjectAnimator.ofFloat(mHeaderImage, "scaleY",
				0.1f, 1.0f);
		mScaleYAnimatorZoomOut.setStartDelay(CLIPDRAWABLE_ZOOM_IN_DURATION);
		mScaleYAnimatorZoomOut.setDuration(HEARTDRAWABLE_ZOOM_OUT_DURATION);

		mScaleXAnimatorHeartZoom = ObjectAnimator.ofFloat(mHeaderImage,
				"scaleX", 1.0f, 0.8f);
		mScaleXAnimatorHeartZoom.setRepeatCount(ValueAnimator.INFINITE);
		mScaleXAnimatorHeartZoom.setRepeatMode(ValueAnimator.REVERSE);
		mScaleXAnimatorHeartZoom.setStartDelay(CLIPDRAWABLE_ZOOM_IN_DURATION
				+ HEARTDRAWABLE_ZOOM_OUT_DURATION);
		mScaleXAnimatorHeartZoom.setInterpolator(mDecelerateInterpolator);
		mScaleXAnimatorHeartZoom.setDuration(HEARTDRAWABLE_ZOOM_HEART_DURATION);

		mScaleYAnimatorHeartZoom = ObjectAnimator.ofFloat(mHeaderImage,
				"scaleY", 1.0f, 0.8f);
		mScaleYAnimatorHeartZoom.setRepeatCount(ValueAnimator.INFINITE);
		mScaleYAnimatorHeartZoom.setRepeatMode(ValueAnimator.REVERSE);
		mScaleYAnimatorHeartZoom.setStartDelay(CLIPDRAWABLE_ZOOM_IN_DURATION
				+ HEARTDRAWABLE_ZOOM_OUT_DURATION);
		mScaleYAnimatorHeartZoom.setInterpolator(mDecelerateInterpolator);
		mScaleYAnimatorHeartZoom.setDuration(HEARTDRAWABLE_ZOOM_HEART_DURATION);
	}
}
