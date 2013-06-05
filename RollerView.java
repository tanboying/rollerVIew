package com.tboing.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

public class RollerView extends View {
    private Scroller mScroller;

    private Paint mPaint = null;
    private FontMetrics mFontMetrics = null;
    private final int mFirst = 0;
    private int mItemHeight = 0;
    private int mItemWidth = 0;
    private int mCountHeight = 0;
    private int mOffset = 0;
    private final int mTextTopOffset = 5;
    private int mShowCount = 3;
    private int mClipRectRight = 0;
    private int mClipRectBottom = 0;
    private int mItemCount = 10;
    private int mPaintColor = Color.BLACK;
    private int mTextSize = 40;

    public RollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context, new OvershootInterpolator());
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setTextSize(sp2px(this.getContext(), mTextSize));
        mItemWidth = (int) mPaint.measureText(String.valueOf(mFirst));
        mFontMetrics = mPaint.getFontMetrics();
        mItemHeight = (int) (mFontMetrics.descent - mFontMetrics.ascent);
        mCountHeight = mItemHeight * mItemCount;
        mClipRectRight = mItemWidth + mPaddingLeft + mPaddingRight;
        mClipRectBottom = (mItemHeight * mShowCount) + mPaddingTop + mPaddingBottom;
    }

    private void doScroller(int time, int value) {
        int showValueOffset = mShowCount / 2;
        mScroller.startScroll(0, 0, mItemHeight * (mItemCount + value - showValueOffset), 0, time);
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mScroller.getCurrX() > 0) {
            mOffset = (mScroller.getCurrX() % mCountHeight);
        }
        canvas.clipRect(0, 0, mClipRectRight, mClipRectBottom);
        for (int n = mFirst; n < mItemCount; n++) {
            int dX = ((n + 1) * mItemHeight) - mOffset;
            if (dX < 0) {
                dX = (mItemCount + 1 + n) * mItemHeight - mOffset;
            }
            canvas.drawText(String.valueOf(n), mPaddingLeft, dX - mTextTopOffset, mPaint);
        }
        if (mScroller.computeScrollOffset()) {
            postInvalidate();
        }
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int value = (int) ((Math.random() * 10) % mItemCount);
                doScroller(3000, value);
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int width = 0;
        int specModel = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specModel == MeasureSpec.EXACTLY) {
            width = specSize;
        } else {
            width = mItemWidth + mPaddingLeft + mPaddingRight;
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = 0;
        int specModel = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specModel == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (mItemHeight * mShowCount) + mPaddingTop + mPaddingBottom;
        }
        return height;
    }

    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
