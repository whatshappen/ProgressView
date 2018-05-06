package com.whathappen.progresslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.whathappen.progresslibrary.R;
import com.whathappen.progresslibrary.control.LinearProgressStyle;
import com.whathappen.progresslibrary.utils.DensityUtils;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/6 ;
 * @Description 水平进度
 */
public class LinearProgress extends View {

    private Paint paint;
    private Context context;
    private float textSize;//进度值字体大小
    private int defaultWidth;//默认宽度
    private int defaultHeight;//默认高度
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    //进度条底色
    private int backgroundColor = 0xffdddddd;
    private int progressColor = 0xff1c8bfb;
    private float roundRectX;//圆角x轴半径
    private float roundRectY;//圆角y轴半径
    private float currentProgress = 0;//当前进度
    private float maxProgress = 100;//最大进度
    //渐变背景颜色
    private int[] progressColors = {0xffcdd513, 0xffff4081, 0xff3cdf5f};
    private int progressStyle = LinearProgressStyle.DEFAULT_TYPE;//进度条样式
    public int textColor = 0xff000000;//进度值字体颜色
    public float percentPadding;//进度值文字的padding
    private OnLinearProgressPreDrawListener listener;

    public LinearProgress(Context context) {
        this(context, null);
    }

    public LinearProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinearProgress);
        maxProgress = typedArray.getFloat(R.styleable.LinearProgress_maxProgress, maxProgress);//最大进度
        currentProgress = typedArray.getFloat(R.styleable.LinearProgress_currentProgress, currentProgress);//当前进度
        if (currentProgress > maxProgress)
            currentProgress = maxProgress;
        backgroundColor = typedArray.getColor(R.styleable.LinearProgress_backgroundColor, backgroundColor);//进度背景颜色
        progressColor = typedArray.getColor(R.styleable.LinearProgress_progressColor, progressColor);//进度颜色
        textColor = typedArray.getColor(R.styleable.LinearProgress_textColor, textColor);//进度颜色
        textSize = typedArray.getDimension(R.styleable.LinearProgress_textSize, textSize);//进度值字体大小
        //渐变色
        progressColors[0] = typedArray.getColor(R.styleable.LinearProgress_progressColors_start, progressColors[0]);
        progressColors[1] = typedArray.getColor(R.styleable.LinearProgress_progressColors_center, progressColors[1]);
        progressColors[2] = typedArray.getColor(R.styleable.LinearProgress_progressColors_end, progressColors[2]);

        roundRectX = (int) typedArray.getDimension(R.styleable.LinearProgress_roundRectX, roundRectX);//进度圆角X轴半径
        roundRectY = (int) typedArray.getDimension(R.styleable.LinearProgress_roundRectY, roundRectY);//进度圆角Y轴半径
        percentPadding = (int) typedArray.getDimension(R.styleable.LinearProgress_percentPadding, percentPadding);//进度圆角Y轴半径
        progressStyle = typedArray.getInt(R.styleable.LinearProgress_progressStyle, progressStyle);//进度样式(默认,渐变)

        typedArray.recycle();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        textSize = DensityUtils.dp2px(context, 14);
        defaultWidth = DensityUtils.dp2px(context, 200);
        defaultHeight = DensityUtils.dp2px(context, 4);
        percentPadding = DensityUtils.dp2px(context, 5);
        roundRectX = DensityUtils.dp2px(context, 2);
        roundRectY = DensityUtils.dp2px(context, 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        //测量进度的宽高
        paint.setTextSize(textSize);
        int[] ints = onMeasureText("100%");
        int width = onMeasureWidth(widthMode, widthSize, ints[0]);
        int height = onMeasureHeight(heightMode, heightSize, ints[1]);
        setMeasuredDimension(width, height);
    }

    /**
     * 测量宽度
     *
     * @param widthMode
     * @param widthSize
     * @return
     */
    private int onMeasureWidth(int widthMode, int widthSize, int textWidth) {
        int result = defaultWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                float width = defaultWidth + (paddingLeft + paddingRight) + textWidth + percentPadding * 2;
                result = MeasureSpec.makeMeasureSpec((int) Math.min(width, widthSize), widthMode);
                break;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param heightMode
     * @param heightSize
     * @return
     */
    private int onMeasureHeight(int heightMode, int heightSize, int textHeight) {
        int result = defaultHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                float height = defaultHeight + (paddingTop + paddingBottom) + textHeight + percentPadding * 2 + textHeight;
                result = MeasureSpec.makeMeasureSpec((int) Math.min(height, heightSize), heightMode);
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setTextSize(textSize);
        int[] maxProgressText = onMeasureText((int) maxProgress + "%");
        if ((paddingTop + paddingBottom) + maxProgressText[1] + percentPadding * 2 + maxProgressText[1] > getHeight()) {
            throw new IllegalThreadStateException("Error :Hight than minimum minHight=" + ((paddingTop + paddingBottom) + maxProgressText[1] + percentPadding * 2 + maxProgressText[1] + 1) + ", currentHeight =" + getHeight());
        }
        if (listener != null)
            listener.onPreDraw();
        //计算进度条位置
        int startLeft = (int) (paddingLeft + maxProgressText[0] / 2 + percentPadding);
        int startTop = (int) (paddingTop + percentPadding * 2 + 2 * maxProgressText[1]);
        int endRight = (int) (getWidth() - paddingRight - maxProgressText[0] / 2 - percentPadding);
        int endBottom = getHeight() - paddingBottom;
        System.out.println("------------getHeight() = " + getHeight());
        //绘制底部
        RectF rectF = new RectF(startLeft, startTop, endRight, endBottom);
        paint.setShader(null);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(rectF, roundRectX, roundRectY, paint);
        //绘制进度
        rectF = new RectF(startLeft, startTop, startLeft + (endRight - startLeft) * (currentProgress / maxProgress), endBottom);
        if (progressStyle == LinearProgressStyle.GRADIENT_TYPE)
            setShader(startLeft, startTop, endRight, endBottom);
        else if (progressStyle == LinearProgressStyle.DEFAULT_TYPE)
            paint.setColor(progressColor);
        canvas.drawRoundRect(rectF, roundRectX, roundRectY, paint);

        //绘制进度值文字
        paint.setShader(null);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        String percent = (int) currentProgress + "%";
        //绘制进度值背景
        paint.setColor(backgroundColor);
        rectF = new RectF(paddingLeft + currentProgress * (endRight - startLeft) / maxProgress, paddingTop, paddingLeft + 2 * percentPadding + maxProgressText[0] + currentProgress * (endRight - startLeft) / maxProgress, paddingTop + 2 * percentPadding + maxProgressText[1]);
        canvas.drawRoundRect(rectF, 2, 2, paint);
        //绘制倒三角
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        path.moveTo(paddingLeft + currentProgress * (endRight - startLeft) / maxProgress, paddingTop + 2 * percentPadding + maxProgressText[1]);
        path.lineTo(startLeft + currentProgress * (endRight - startLeft) / maxProgress, startTop);
        path.lineTo(paddingLeft + currentProgress * (endRight - startLeft) / maxProgress + 2 * percentPadding + maxProgressText[0], paddingTop + 2 * percentPadding + maxProgressText[1]);
        //调用close()方法，自动将三个点连接起来
        path.close();
        canvas.drawPath(path, paint);
        //绘制进度值文字
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        int[] currentText = onMeasureText(percent);
        float x = (paddingLeft + percentPadding + (maxProgressText[0] - currentText[0]) / 2 + currentProgress * (endRight - startLeft) / maxProgress);
        canvas.drawText(percent, x, paddingTop + percentPadding + maxProgressText[1], paint);
    }

    /**
     * 计算进度文字宽高
     *
     * @param progress
     * @return
     */
    private int[] onMeasureText(String progress) {
        Rect rect = new Rect();
        paint.getTextBounds(progress, 0, progress.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        return new int[]{textWidth, textHeight};
    }

    /**
     * 设置渐变
     *
     * @param startLeft
     * @param startTop
     * @param endRight
     * @param endBottom
     */
    private void setShader(int startLeft, int startTop, int endRight, int endBottom) {
        LinearGradient mShader = new LinearGradient(startLeft, startTop, endRight, endBottom, progressColors, null, Shader.TileMode.REPEAT);
        paint.setShader(mShader);
    }

    /**
     * 设置进度
     *
     * @param currentProgress
     * @return
     */
    public LinearProgress setProgress(float currentProgress) {
        if (currentProgress > maxProgress)
            this.currentProgress = maxProgress;
        else
            this.currentProgress = currentProgress;
        return this;
    }

    public float getProgress() {
        return currentProgress;
    }

    /**
     * 设置渐变颜色
     *
     * @param startColor
     * @param centerColor
     * @param endColor
     * @return
     */
    public LinearProgress setProgressColors(String startColor, String centerColor, String endColor) {
        this.progressColors[0] = Color.parseColor(startColor);
        this.progressColors[1] = Color.parseColor(centerColor);
        this.progressColors[2] = Color.parseColor(endColor);
        return this;
    }

    public int[] getProgressColors() {
        return progressColors;
    }

    /**
     * 设置总进度
     *
     * @param maxProgress
     * @return
     */
    public LinearProgress setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        return this;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    /**
     * 设置样式
     *
     * @param type
     * @return
     */
    public LinearProgress setProgressStyle(@LinearProgressStyle.Type int type) {
        this.progressStyle = type;
        return this;
    }

    public int getProgressStyle() {
        return progressStyle;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     * @return
     */
    public LinearProgress setBackgroundColor(String color) {
        this.backgroundColor = Color.parseColor(color);
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * 设置进度颜色
     *
     * @param color
     * @return
     */
    public LinearProgress setProgressColor(String color) {
        this.progressColor = Color.parseColor(color);
        return this;
    }

    public int getProgressColor() {
        return progressColor;
    }

    /**
     * 设置进度值颜色
     *
     * @param color
     * @return
     */
    public LinearProgress setTextColor(String color) {
        this.textColor = Color.parseColor(color);
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置字体大小
     *
     * @param size
     * @return
     */
    public LinearProgress setTextSize(float size) {
        this.textSize = size;
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * 圆角X轴半径
     *
     * @param rx
     * @return
     */
    public LinearProgress setRoundRectX(float rx) {
        this.roundRectX = rx;
        return this;
    }

    public float getRoundRectX() {
        return roundRectX;
    }

    /**
     * 圆角Y轴半径
     *
     * @param ry
     * @return
     */
    public LinearProgress setRoundRectY(float ry) {
        this.roundRectY = ry;
        return this;
    }

    public float getRoundRectY() {
        return roundRectY;
    }

    /**
     * 设置进度值padding
     *
     * @param percentPadding
     * @return
     */
    public LinearProgress setPercentPadding(float percentPadding) {
        this.percentPadding = percentPadding;
        return this;
    }

    public float getPercentPadding() {
        return percentPadding;
    }

    public LinearProgress setProgressWidth(int width) {
        getLayoutParams().width = width;
        requestLayout();
        return this;
    }

    public int getProgressWidth() {
        return getWidth();
    }

    public LinearProgress setProgressHeight(int height) {
        getLayoutParams().height = height;
        requestLayout();
        return this;
    }

    public int getProgressHeight() {
        return getHeight();
    }

    public void builder() {
        postInvalidate();
    }

    /**
     * 控件中图片绘制完宽高监听
     */
    public interface OnLinearProgressPreDrawListener {
        void onPreDraw();
    }

    public void setOnLinearProgressPreDrawListener(OnLinearProgressPreDrawListener listener) {
        this.listener = listener;
    }

    public void removePreDrawListener() {
        this.listener = null;
    }
}
