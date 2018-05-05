package com.whathappen.progresslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.whathappen.progresslibrary.R;
import com.whathappen.progresslibrary.control.CircleProgressStyle;
import com.whathappen.progresslibrary.utils.DensityUtils;

/**
 * Author： Wangw
 * Created on： 2018/4/23.
 * Email：
 * Description：
 */

public class CircleProgress extends View {

    private final Context context;
    private Paint paint;
    /**
     * 进度条默认宽高
     */
    private float defaultWidth;
    private float defaultHeight;
    /**
     * 进度条默认背景颜色
     */
    private int backgroundColor = 0xffdddddd;
    /**
     * 进度条宽度
     */
    private float strokeWidth;
    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;

    /**
     * 最大进度，当前进度
     */
    private float maxProgress = 100, currentProgress = 0, startAngle = -90;

    /**
     * 进度条进度颜色,进度值颜色
     */
    private int progressColor = 0xff1c8bfb, textColor = 0xff000000;
    /**
     * 进度值字体大小
     */
    private float textSize, dialTextSize;

    /**
     * 进度颜色
     */
    private int[] progressColors = {0xffef712d, 0xffcdd513, 0xff3cdf5f};

    /**
     * 进度条样式
     */
    private int progressType = 0;

    /**
     * 刻度宽度，刻度间隔
     */
    private float singleDialWidth, lineWidth;
    /**
     * 刻度间隔颜色
     */
    private int dialDefaultColor = 0xffdddddd;
    //文件距离圆环距离
    private float textDistance;

    /**
     * 进度值类型：默认是int类型
     */
    private int progressValueStyle = 0;

    /**
     * 进度值显示风格
     */
    public int progressValueType = 0;

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        strokeWidth = typedArray.getDimension(R.styleable.CircleProgress_strokeWidth, strokeWidth);//进度条宽度
        maxProgress = typedArray.getFloat(R.styleable.CircleProgress_maxProgress, maxProgress);//最大进度
        currentProgress = typedArray.getFloat(R.styleable.CircleProgress_currentProgress, maxProgress);//当前进度
        startAngle = typedArray.getFloat(R.styleable.CircleProgress_startAngle, startAngle);//开始角度
        backgroundColor = typedArray.getColor(R.styleable.CircleProgress_backgroundColor, backgroundColor);//进度背景颜色
        progressColor = typedArray.getColor(R.styleable.CircleProgress_progressColor, progressColor);//进度颜色
        textColor = typedArray.getColor(R.styleable.CircleProgress_textColor, textColor);//进度颜色
        textSize = typedArray.getDimension(R.styleable.CircleProgress_textSize, textSize);//进度值字体大小
        dialTextSize = typedArray.getDimension(R.styleable.CircleProgress_dialTextSize, dialTextSize);//刻度值字体大小
        //渐变色
        progressColors[0] = typedArray.getColor(R.styleable.CircleProgress_progressColors_start, progressColors[0]);
        progressColors[1] = typedArray.getColor(R.styleable.CircleProgress_progressColors_center, progressColors[1]);
        progressColors[2] = typedArray.getColor(R.styleable.CircleProgress_progressColors_end, progressColors[2]);
        //样式
        progressType = typedArray.getInt(R.styleable.CircleProgress_progressType, progressType);
        progressValueStyle = typedArray.getInt(R.styleable.CircleProgress_progressValueStyle, progressValueStyle);
        progressValueType = typedArray.getInt(R.styleable.CircleProgress_progressValueType, progressValueType);
        dialDefaultColor = typedArray.getColor(R.styleable.CircleProgress_dialDefaultColor, dialDefaultColor);//刻度默认颜色
        singleDialWidth = typedArray.getFloat(R.styleable.CircleProgress_singleDialWidth, singleDialWidth);//刻度宽度
        lineWidth = typedArray.getFloat(R.styleable.CircleProgress_lineWidth, lineWidth);//刻度间隔
        //设置刻度距离文字距离为文字大小的1/3
        textDistance = dialTextSize / 3;

        typedArray.recycle();
    }

    //初始化
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        textSize = DensityUtils.dp2px(context, 15);
        dialTextSize = DensityUtils.dp2px(context, 12);
        defaultWidth = DensityUtils.dp2px(context, 150);
        defaultHeight = DensityUtils.dp2px(context, 150);
        strokeWidth = DensityUtils.dp2px(context, 5);
        singleDialWidth = DensityUtils.dp2px(context, 1);
        lineWidth = DensityUtils.dp2px(context, 0.5f);
        textDistance = DensityUtils.dp2px(context, 4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = (int) onMeasureWidth(widthSize, widthMode);
        int height = (int) onMeasureHeight(heightSize, heightMode);
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        //进度值显示在外层
        if (progressValueStyle == CircleProgressStyle.OUTER_VALUE_TYPE ||
                progressValueStyle == CircleProgressStyle.OUTER_VALUE_TYPE_SHOW_DIAL) {
            //计算文字宽高
            paint.setTextSize(dialTextSize);
            int[] ints = onMeasureText("0%");
            //计算文字需要的最小padding
            int distance = (int) (textDistance + strokeWidth / 2 + ints[1] + 10);//+10px
            System.out.println("--------- distance =" + distance + ",paddingLeft = " + paddingLeft);
            paddingLeft = paddingLeft < distance ? distance : paddingLeft;
            paddingTop = paddingTop < distance ? distance : paddingTop;
            paddingRight = paddingRight < distance ? distance : paddingRight;
            paddingBottom = paddingBottom < distance ? distance : paddingBottom;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 测量width
     *
     * @param widthSize
     * @param widthMode
     * @return
     */
    private float onMeasureWidth(int widthSize, int widthMode) {
        float result = defaultWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                float width = defaultWidth + (paddingLeft + paddingRight);
                result = MeasureSpec.makeMeasureSpec((int) Math.min(width, widthSize), widthMode);
                break;
        }
        return result;
    }

    /**
     * 测量Height
     *
     * @param heightSize
     * @param heightMode
     * @return
     */
    private float onMeasureHeight(int heightSize, int heightMode) {
        float result = defaultHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                float height = defaultHeight + (paddingBottom + paddingTop);
                result = MeasureSpec.makeMeasureSpec((int) Math.min(height, heightSize), heightMode);
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算圆心和半径
        float x = (getWidth() - (paddingLeft + paddingRight)) / 2;
        float y = (getHeight() - (paddingBottom + paddingTop)) / 2;
        float centerX = paddingLeft + x;
        float centerY = paddingTop + y;
        float radius = Math.min(x, y) - strokeWidth / 2;
        paint.setColor(backgroundColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        //绘制圆
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawCircle(centerX, centerY, radius, paint);

        //绘制进度
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float progressAngle = currentProgress > maxProgress ? 360 : 360 * currentProgress / maxProgress;
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        /*
        *设置样式
        */
        if (progressType == CircleProgressStyle.DEFAULT_TYPE || progressType == CircleProgressStyle.DEFAULT_TYPE_DIAL) {
            paint.setShader(null);
            paint.setColor(progressColor);
            if (progressType == CircleProgressStyle.DEFAULT_TYPE) {
                canvas.drawArc(rectF, startAngle, progressAngle, false, paint);
            } else if (progressType == CircleProgressStyle.DEFAULT_TYPE_DIAL) {
                canvasDial(centerX, centerY, progressAngle, canvas, rectF, false);
            }
        } else if (progressType == CircleProgressStyle.GRADIENT_TYPE || progressType == CircleProgressStyle.GRADIENT_TYPE_DIAL) {
            setSweepGradient(centerX, centerY);
            if (progressType == CircleProgressStyle.GRADIENT_TYPE) {
                canvas.drawArc(rectF, startAngle, progressAngle, false, paint);
            } else if (progressType == CircleProgressStyle.GRADIENT_TYPE_DIAL) {
                canvasDial(centerX, centerY, progressAngle, canvas, rectF, true);
            }
        }

        //绘制文字
        paint.setShader(null);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(dialTextSize);

        //是否显示刻度值
        if (progressValueStyle == CircleProgressStyle.DEFAULT_VALUE_TYPE_SHOW_DIAL ||
                progressValueStyle == CircleProgressStyle.OUTER_VALUE_TYPE_SHOW_DIAL) {
            for (int i = 1; i <= 10; i++) {
                Rect rect = new Rect();
                String text = i + "";
                paint.getTextBounds(text, 0, text.length(), rect);
                int dialWidth = rect.width();
                int dialHeight = rect.height();
                canvas.save();//保存布局
                canvas.rotate((360 / 10) * i, centerX, centerY);
                canvas.drawText(i * 10 + "", centerX - dialWidth / 2, centerY - radius + strokeWidth / 2 + textDistance + dialHeight, paint);
                canvas.restore();//回复上一个保存状态
            }
        }
        //计算文字宽高
        String strProgress = progressValueType == CircleProgressStyle.INT_TYPE ? (int) currentProgress + "%" : currentProgress + "%";
        paint.setTextSize(textSize);
        int[] ints = onMeasureText(strProgress);
        //在圆心
        if (progressValueStyle == CircleProgressStyle.DEFAULT_VALUE_TYPE
                || progressValueStyle == CircleProgressStyle.DEFAULT_VALUE_TYPE_SHOW_DIAL) {
            //绘制文字
            canvas.drawText(strProgress, centerX - ints[0] / 2, centerY + ints[1] / 2, paint);
        } else if (progressValueStyle == CircleProgressStyle.INNER_VALUE_TYPE) {//内
            canvas.save();
            canvas.rotate(360 * currentProgress / maxProgress, centerX, centerY);
            canvas.drawText(strProgress, centerX - ints[0] / 2, centerY - radius + strokeWidth / 2 + textDistance + ints[1], paint);
            canvas.restore();
        } else if (progressValueStyle == CircleProgressStyle.OUTER_VALUE_TYPE
                || progressValueStyle == CircleProgressStyle.OUTER_VALUE_TYPE_SHOW_DIAL) {
            canvas.save();
            canvas.rotate(360 * currentProgress / maxProgress, centerX, centerY);
            canvas.drawText(strProgress, centerX - ints[0] / 2, centerY - radius - strokeWidth / 2 - textDistance, paint);
            canvas.restore();
        }

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
     * 绘制刻度
     *
     * @param progressAngle
     * @param canvas
     * @param rectF
     */
    private void canvasDial(float centerX, float centerY, float progressAngle, Canvas canvas, RectF rectF, boolean hasGradient) {
        //绘制总个数的刻度
        float maxCount = 360 / (singleDialWidth + lineWidth);
        paint.setShader(null);//去掉渐变
        paint.setColor(dialDefaultColor);
        calculateDial(maxCount, canvas, rectF);
        //绘制当前进度刻度数
        float count = progressAngle / (singleDialWidth + lineWidth);
        if (hasGradient)
            setSweepGradient(centerX, centerY);//设置渐变
        else
            paint.setColor(progressColor);//设置默认颜色
        calculateDial(count, canvas, rectF);
    }

    /**
     * 计算绘制刻度
     *
     * @param dialCount
     * @param canvas
     * @param rectF
     */
    private void calculateDial(float dialCount, Canvas canvas, RectF rectF) {
        if (dialCount <= 0) return;
        for (float i = 0; i < dialCount; i += 0.1) {
            if (i % 1 == 0) {
                canvas.drawArc(rectF, startAngle + i * (singleDialWidth + lineWidth), singleDialWidth, false, paint);
            } else {
                canvas.drawArc(rectF, startAngle + ((int) (i / 1)) * (singleDialWidth + lineWidth), singleDialWidth * (i % 1), false, paint);
            }
        }
    }

    /**
     * 设置渐变色
     */
    private void setSweepGradient(float centerX, float centerY) {
        //设置渐变色
        SweepGradient sweepGradient = new SweepGradient(centerX, centerY, progressColors, null);
        //设置初始角度
        Matrix matrix = new Matrix();
        matrix.setRotate(startAngle, centerX, centerY);
        sweepGradient.setLocalMatrix(matrix);
        paint.setShader(sweepGradient);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public CircleProgress setProgress(float progress) {
        if (progress > maxProgress)
            this.currentProgress = maxProgress;
        else
            this.currentProgress = progress;
        return this;
    }

    public float getProgress() {
        return currentProgress;
    }

    /**
     * 设置总进度
     *
     * @param maxProgress
     * @return
     */
    public CircleProgress setMaxProgress(int maxProgress) {
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
    public CircleProgress setProgressStyle(@CircleProgressStyle.Type int type) {
        this.progressType = type;
        return this;
    }

    public int getProgressStyle() {
        return progressType;
    }

    /**
     * 设置进度条宽度
     *
     * @param strokeWidth
     * @return
     */
    public CircleProgress setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }


    /**
     * 设置开始角度
     *
     * @param startAngle
     * @return
     */
    public CircleProgress setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    public float getStartAngle() {
        return startAngle;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     * @return
     */
    public CircleProgress setBackgroundColor(String color) {
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
    public CircleProgress setProgressColor(String color) {
        this.progressColor = Color.parseColor(color);
        return this;
    }

    public int getProgressColor() {
        return progressColor;
    }

    /**
     * 设置渐变颜色
     *
     * @param startColor
     * @param centerColor
     * @param endColor
     * @return
     */
    public CircleProgress setProgressColors(String startColor, String centerColor, String endColor) {
        this.progressColors[0] = Color.parseColor(startColor);
        this.progressColors[1] = Color.parseColor(centerColor);
        this.progressColors[2] = Color.parseColor(endColor);
        return this;
    }

    public int[] getProgressColors() {
        return progressColors;
    }

    /**
     * 设置进度值颜色
     *
     * @param color
     * @return
     */
    public CircleProgress setTextColor(String color) {
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
    public CircleProgress setTextSize(float size) {
        this.textSize = size;
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置刻度默认颜色
     *
     * @param color
     * @return
     */
    public CircleProgress setDialDefaultColor(String color) {
        this.dialDefaultColor = Color.parseColor(color);
        return this;
    }

    public int getDialDefaultColor() {
        return dialDefaultColor;
    }

    /**
     * 设置刻度宽度和刻度间隔
     *
     * @param singleDialWidth
     * @param lineWidth
     * @return
     */
    public CircleProgress setDialWidth(float singleDialWidth, float lineWidth) {
        this.singleDialWidth = singleDialWidth;
        this.lineWidth = lineWidth;
        return this;
    }

    public float[] getDialWidth() {
        return new float[]{singleDialWidth, lineWidth};
    }

    /**
     * 设置进度值样式
     *
     * @param progressValueStyle
     * @return
     */
    public CircleProgress setProgressValueStyle(@CircleProgressStyle.ProgressValueStyle int progressValueStyle) {
        this.progressValueStyle = progressValueStyle;
        return this;
    }

    public int getProgressValueStyle() {
        return progressValueStyle;
    }

    /**
     * 设置进度值int/float
     *
     * @param progressValueType
     * @return
     */
    public CircleProgress setProgressValueType(@CircleProgressStyle.ProgressValueType int progressValueType) {
        this.progressValueType = progressValueType;
        return this;
    }

    public int getProgressValueType() {
        return progressValueType;
    }

    public CircleProgress setDialTextSize(int size) {
        this.dialTextSize = size;
        return this;
    }

    public float getDialTextSize() {
        return dialTextSize;
    }

    //重绘
    public void builder() {
        postInvalidate();
    }
}
