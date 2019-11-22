package team.fzo.puppas.mini_player.view;

/*
 * Copyright (c) 2016 André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.andremion.music.MusicCoverViewTransition;
import team.fzo.puppas.mini_player.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MusicCoverView extends android.support.v7.widget.AppCompatImageView implements Animatable {

    public static final int SHAPE_RECTANGLE = 0;
    public static final int SHAPE_CIRCLE = 1;

    static final int ALPHA_TRANSPARENT = 0;
    static final int ALPHA_OPAQUE = 255;

    private static final float TRACK_SIZE = 10;
    private static final float TRACK_WIDTH = 1;
    private static final int TRACK_COLOR = Color.parseColor("#56FFFFFF");

    private static final float FULL_ANGLE = 360;
    private static final float HALF_ANGLE = FULL_ANGLE / 2;
    private static final int DURATION = 5000;
    private static final float DURATION_PER_DEGREES = DURATION / FULL_ANGLE;

    private final ValueAnimator mStartRotateAnimator;
    private final ValueAnimator mEndRotateAnimator;
    private final Transition mCircleToRectTransition;
    private final Transition mRectToCircleTransition;

    private final float mTrackSize;
    private final Paint mTrackPaint;
    private int mTrackAlpha;

    private final Path mClipPath = new Path();
    private final Path mRectPath = new Path();
    private final Path mTrackPath = new Path();

    private boolean mIsMorphing;
    private float mRadius = 0;

    private MusicCoverView.Callbacks mCallbacks;
    private int mShape;

    public MusicCoverView(Context context) {
        this(context, null, 0);
    }

    public MusicCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicCoverView(Context context, AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // TODO: Canvas.clipPath works wrong when running with hardware acceleration on Android N
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        final float density = getResources().getDisplayMetrics().density;
        mTrackSize = TRACK_SIZE * density;
        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setStyle(Paint.Style.STROKE);
        mTrackPaint.setStrokeWidth(TRACK_WIDTH * density);

        mStartRotateAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, 0, FULL_ANGLE);
        mStartRotateAnimator.setInterpolator(new LinearInterpolator());
        mStartRotateAnimator.setRepeatCount(Animation.INFINITE);
        mStartRotateAnimator.setDuration(DURATION);
        mStartRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                float current = getRotation();
                float target = current > HALF_ANGLE ? FULL_ANGLE : 0; // Choose the shortest distance to 0 rotation
                float diff = target > 0 ? FULL_ANGLE - current : current;

                //调用mEndRotateAnimator并设置4倍速
                mEndRotateAnimator.setFloatValues(current, target);
                mEndRotateAnimator.setDuration((int) (DURATION_PER_DEGREES * diff / 4));
                mEndRotateAnimator.start();
            }
        });

        mEndRotateAnimator = ObjectAnimator.ofFloat(MusicCoverView.this, View.ROTATION, 0);
        mEndRotateAnimator.setInterpolator(new LinearInterpolator());
        mEndRotateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setRotation(0);
                // isRunning method return true if it's called form here.
                // So we need call from post method to get the right returning.
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallbacks != null) {
                            mCallbacks.onBackPressed(MusicCoverView.this);
                        }
                    }
                });
            }
        });

        mRectToCircleTransition = new MusicCoverView.MorphTransition(SHAPE_RECTANGLE);
        mRectToCircleTransition.addTarget(this);
        mRectToCircleTransition.addListener(new MusicCoverView.TransitionAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                mIsMorphing = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mIsMorphing = false;
                mShape = SHAPE_CIRCLE;
                if (mCallbacks != null) {
                    mCallbacks.onMorphEnd(MusicCoverView.this);
                }
            }
        });

        mCircleToRectTransition = new MusicCoverView.MorphTransition(SHAPE_CIRCLE);
        mCircleToRectTransition.addTarget(this);
        mCircleToRectTransition.addListener(new MusicCoverView.TransitionAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                mIsMorphing = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mIsMorphing = false;
                mShape = SHAPE_RECTANGLE;
                if (mCallbacks != null) {
                    mCallbacks.onMorphEnd(MusicCoverView.this);
                }
            }
        });


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MusicCoverView);
        @Shape int shape = a.getInt(R.styleable.MusicCoverView_shape, SHAPE_RECTANGLE);
        @ColorInt int trackColor = a.getColor(R.styleable.MusicCoverView_trackColor, TRACK_COLOR);
        a.recycle();

        setShape(shape);
        setTrackColor(trackColor);
        setScaleType();
    }

    public void setCallbacks(MusicCoverView.Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public int getShape() {
        return mShape;
    }

    public void setShape(@Shape int shape) {
        if (shape != mShape) {
            mShape = shape;
            setScaleType();
            if (!isInLayout() && !isLayoutRequested()) {
                calculateRadius();
                resetPaths();
            }
        }
    }

    public void setTrackColor(@ColorInt int trackColor) {
        if (trackColor != getTrackColor()) {
            int alpha = mShape == SHAPE_CIRCLE ? ALPHA_OPAQUE : ALPHA_TRANSPARENT;
            mTrackPaint.setColor(trackColor);
            mTrackAlpha = Color.alpha(trackColor);
            mTrackPaint.setAlpha(alpha * mTrackAlpha / ALPHA_OPAQUE);
            invalidate();
        }
    }

    public int getTrackColor() {
        return mTrackPaint.getColor();
    }

    float getTransitionRadius() {
        return mRadius;
    }

    void setTransitionRadius(float radius) {
        if (radius != mRadius) {
            mRadius = radius;
            resetPaths();
            invalidate();
        }
    }

    int getTransitionAlpha() {
        return mTrackPaint.getAlpha() * ALPHA_OPAQUE / mTrackAlpha;
    }

    void setTransitionAlpha(@IntRange(from = ALPHA_TRANSPARENT, to = ALPHA_OPAQUE) int alpha) {
        if (alpha != getTransitionAlpha()) {
            mTrackPaint.setAlpha(alpha * mTrackAlpha / ALPHA_OPAQUE);
            invalidate();
        }
    }

    float getMinRadius() {
        final int w = getWidth();
        final int h = getHeight();
        return Math.min(w, h) / 2f;
    }

    float getMaxRadius() {
        final int w = getWidth();
        final int h = getHeight();
        return (float) Math.hypot(w / 2f, h / 2f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateRadius();
        resetPaths();
    }

    private void calculateRadius() {
        if (SHAPE_CIRCLE == mShape) {
            mRadius = getMinRadius();
        } else {
            mRadius = getMaxRadius();
        }
    }

    private void setScaleType() {
        if (SHAPE_CIRCLE == mShape) {
            setScaleType(ScaleType.CENTER_INSIDE);
        } else {
            setScaleType(ScaleType.CENTER_CROP);
        }
    }

    private void resetPaths() {

        final int w = getWidth();
        final int h = getHeight();
        final float centerX = w / 2f;
        final float centerY = h / 2f;

        mClipPath.reset();
        mClipPath.addCircle(centerX, centerY, mRadius, Path.Direction.CW);

        final int trackRadius = Math.min(w, h);
        final int trackCount = (int) (trackRadius / mTrackSize);

        mTrackPath.reset();
        for (int i = 3; i < trackCount; i++) {
            mTrackPath.addCircle(centerX, centerY, trackRadius * (i / (float) trackCount), Path.Direction.CW);
        }

        mRectPath.reset();
        mRectPath.addRect(0, 0, w, h, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mClipPath);
        super.onDraw(canvas);
        canvas.drawPath(mTrackPath, mTrackPaint);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        // Don't need to consume the system window insets
        return insets;
    }

    public void morph() {
        if (SHAPE_CIRCLE == mShape) {
            morphToRect();
        } else {
            morphToCircle();
        }
    }

    private void morphToCircle() {
        if (mIsMorphing) {
            return;
        }
        TransitionManager.beginDelayedTransition((ViewGroup) getParent(), mRectToCircleTransition);
        setScaleType(ScaleType.CENTER_INSIDE);
    }

    private void morphToRect() {
        if (mIsMorphing) {
            return;
        }
        TransitionManager.beginDelayedTransition((ViewGroup) getParent(), mCircleToRectTransition);
        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    public void start() {
        if (SHAPE_RECTANGLE == mShape) { // Only start rotate when shape is a circle
            return;
        }
        if (!isRunning()) {
            mStartRotateAnimator.start();
        }
    }

    @Override
    public void stop() {
        if (mStartRotateAnimator.isRunning()) {
            mStartRotateAnimator.cancel();
        }
        else{
            //直接启动EndRotateAnimator
            mEndRotateAnimator.setFloatValues(0, 0);
            mEndRotateAnimator.setDuration(0);
            mEndRotateAnimator.start();
        }
    }

    public void pause(){
        mStartRotateAnimator.pause();
    }

    public void resume(){
        mStartRotateAnimator.resume();
    }

    public boolean isStarted(){
        return mStartRotateAnimator.isStarted();
    }

    @Override
    public boolean isRunning() {
        return mStartRotateAnimator.isRunning() || mEndRotateAnimator.isRunning() || mIsMorphing;
    }

    @IntDef({SHAPE_CIRCLE, SHAPE_RECTANGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    public interface Callbacks {
        void onMorphEnd(MusicCoverView coverView);

        void onRotateEnd(MusicCoverView coverView);

        void onBackPressed(MusicCoverView coverView);
    }

    private static class MorphTransition extends TransitionSet {
        private MorphTransition(int shape) {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new MusicCoverViewTransition(shape));
            addTransition(new ChangeImageTransform());
            addTransition(new ChangeTransform());
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        MusicCoverView.SavedState ss = new MusicCoverView.SavedState(superState);
        ss.shape = getShape();
        ss.trackColor = getTrackColor();
        ss.isRotating = mStartRotateAnimator.isRunning();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        MusicCoverView.SavedState ss = (MusicCoverView.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setShape(ss.shape);
        setTrackColor(ss.trackColor);
        if (ss.isRotating) {
            start();
        }
    }

    public static class SavedState extends AbsSavedState {

        private int shape;
        private int trackColor;
        private boolean isRotating;

        private SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            shape = in.readInt();
            trackColor = in.readInt();
            isRotating = (boolean) in.readValue(Boolean.class.getClassLoader());
        }

        private SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(shape);
            dest.writeInt(trackColor);
            dest.writeValue(isRotating);
        }

        @Override
        public String toString() {
            return MusicCoverView.class.getSimpleName() + "." + MusicCoverView.SavedState.class.getSimpleName() + "{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " shape=" + shape + ", trackColor=" + trackColor + ", isRotating=" + isRotating + "}";
        }

        public static final Parcelable.Creator<MusicCoverView.SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<MusicCoverView.SavedState>() {
            @Override
            public MusicCoverView.SavedState createFromParcel(Parcel parcel, ClassLoader loader) {
                return new MusicCoverView.SavedState(parcel, loader);
            }

            @Override
            public MusicCoverView.SavedState[] newArray(int size) {
                return new MusicCoverView.SavedState[size];
            }
        });
    }

    private static class TransitionAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {
        }

        @Override
        public void onTransitionEnd(Transition transition) {
        }

        @Override
        public void onTransitionCancel(Transition transition) {
        }

        @Override
        public void onTransitionPause(Transition transition) {
        }

        @Override
        public void onTransitionResume(Transition transition) {
        }
    }

}

