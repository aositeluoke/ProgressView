package com.intent.progressview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.math.BigDecimal;

/**
 * 类描述:
 * 作者:xues
 * 时间:2019年02月23日
 */

public class ProgressView extends View {
    /*裁剪区域*/
    private Path mClipPath;
    private RectF mClipRect;
    /**
     * 进度区域
     */
    private int mProColor = Color.RED;
    private Paint mProgressPaint;
    private RectF mProgressRect;
    private float mProWidth = 0.01f;
    /**
     * 装饰区域
     */
    private int mDecoratorColor = Color.BLACK;
    private RectF mDecoratorRect;
    private Paint mDecoratorPaint;
    private float mDecoratorWidth = 5;
    /*文字相关*/
    private Paint mTextPaint;
    private Paint.FontMetrics fontMetrics;
    private Rect mTextRect = new Rect();
    private int mTextColor = Color.WHITE;
    private float mTextSize = 20;
    private float baseX, baseY;

    /*当前进度和总数*/
    private float mCurPro;
    private float mTotalPro;


    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*初始化属性*/
        initAttr(context, attrs);
        /*初始化画笔*/
        initPaint();


    }


    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);
        mDecoratorWidth = a.getDimension(R.styleable.ProgressView_out_width, mDecoratorWidth);
        mCurPro = a.getFloat(R.styleable.ProgressView_cur_pro, mCurPro);
        mTotalPro = a.getFloat(R.styleable.ProgressView_total_pro, mTotalPro);
        mDecoratorColor = a.getColor(R.styleable.ProgressView_out_ring_color, mDecoratorColor);
        mProColor = a.getColor(R.styleable.ProgressView_pro_color, mProColor);
        mTextColor = a.getColor(R.styleable.ProgressView_text_color, mTextColor);
        mTextSize = a.getDimension(R.styleable.ProgressView_text_size, mTextSize);
        a.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        /*装饰画笔*/
        mDecoratorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDecoratorPaint.setStyle(Paint.Style.STROKE);
        mDecoratorPaint.setColor(mDecoratorColor);
        mDecoratorPaint.setStrokeWidth(mDecoratorWidth);
        mDecoratorPaint.setStrokeCap(Paint.Cap.ROUND);

        /*进度画笔*/
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProColor);
        mProgressPaint.setStrokeWidth(1);
        mProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);


        /*文字画笔*/
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*装饰区域*/
        mDecoratorRect = new RectF(mDecoratorWidth / 2f, mDecoratorWidth / 2f, getMeasuredWidth() - mDecoratorWidth / 2f, getMeasuredHeight() - mDecoratorWidth / 2f);

        /*裁剪区域(进度条区域)*/
        mClipRect = new RectF(mDecoratorWidth + mProWidth / 2f, mDecoratorWidth + mProWidth / 2f, getMeasuredWidth() - mDecoratorWidth + mProWidth / 2f, getMeasuredHeight() - mDecoratorWidth - mProWidth / 2f);
        mClipPath = new Path();
        mClipPath.addRoundRect(mClipRect, mClipRect.height() / 2, mClipRect.height() / 2, Path.Direction.CCW);


       /*进度条区域*/
        mProgressRect = new RectF(mDecoratorWidth + mProWidth / 2f,
                mDecoratorWidth + mProWidth / 2f,
                getMeasuredWidth() - mDecoratorWidth + mProWidth / 2f,
                getMeasuredHeight() - mDecoratorWidth - mProWidth / 2f);

        /*获取文字基准线baseY值*/
        fontMetrics = mTextPaint.getFontMetrics();
        baseY = getMeasuredHeight() / 2f + (fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.bottom;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mDecoratorRect, mDecoratorRect.height() / 2, mDecoratorRect.height() / 2, mDecoratorPaint);
        drawClipAreaAndProArea(canvas);
        drawText(canvas);
    }


    /**
     * 绘制裁剪区域和进度区域
     *
     * @param canvas
     */
    private void drawClipAreaAndProArea(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mClipPath);
        mProgressRect.right = mDecoratorWidth + mProWidth / 2f + (getMeasuredWidth() - mDecoratorWidth * 2f - mProWidth) * (mCurPro / mTotalPro);
        canvas.drawRoundRect(mProgressRect, mProgressRect.height() / 2, mProgressRect.height() / 2, mProgressPaint);
        canvas.restore();
    }


    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        BigDecimal percent = new BigDecimal(mCurPro * 100).divide(new BigDecimal(mTotalPro), 0, BigDecimal.ROUND_FLOOR);
        String mTextStr = percent + "%";
        mTextPaint.getTextBounds(mTextStr, 0, mTextStr.length(), mTextRect);
        if (mProgressRect.width() > mTextRect.width() + 10) {
            float max = mProgressRect.width() + mDecoratorWidth - 10;
            baseX = max - mTextRect.width();
            canvas.drawText(mTextStr, baseX, baseY, mTextPaint);
        }
    }

    public void setMCurPro(float mCurPro) {
        this.mCurPro = mCurPro;
        invalidate();
    }


    public void setProgress(float curPro, float totalPro) {
        this.mTotalPro = totalPro;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "mCurPro", 0, curPro);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();

    }

}
