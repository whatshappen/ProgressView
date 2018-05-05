package com.whathappen.progresslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.whathappen.progresslibrary.R;
import com.whathappen.progresslibrary.control.ImageProgressStyle;
import com.whathappen.progresslibrary.utils.DensityUtils;

import static java.lang.Math.atan;

/**
 * Author： Wangw
 * Created on： 2018/5/3.
 * Email：
 * Description：图片进度条
 */

public class ImageProgress extends View {

    private Context context;
    private int paddingRight;
    private int paddingTop;
    private int paddingLeft;
    private int paddingBottom;

    private int defaultWidth;//默认宽度
    private int defaultHeight;//默认高度
    private int roundRect;//圆角x轴半径
    private Paint paint;
    private Bitmap bitmap;
    private int strokeWidth;

    private float currentProgress = 0;//当前进度
    private float maxProgress = 100;
    private Path currentPath;//当前进度背景路径
    private Path path;//默认的进度条背景路径
    private int strokeLeft;
    private int strokeTop;
    private int strokeRight;
    private int strokeBottom;

    private int backgroundColor = 0xff000000;//进度默认背景
    private int textColor = 0xff000000;//进度值颜色
    private int progressColor = 0xff1c8bfb;//当前进度背景
    private int progressLoadingBackground = 0xff000000;//加载进度背景颜色
    private boolean hasLoadingBackground = true;//设置是否有加载背景
    private boolean hasTextHint = true;//是否有进度值提示
    private boolean hasLastText = true;//是否在最后显示进度值
    //进度条样式
    private int progressStyle = ImageProgressStyle.GRADIENT_TYPE;
    //进度值字体大小
    private float textSize;
    //进度值方向
    private int progressIndexAndOri = ImageProgressStyle.LEFT_TOP_CW;
    //渐变背景颜色
    private int[] progressColors = {0xffcdd513, 0xffff4081, 0xff3cdf5f, 0xffcdd513};
    //进度渐变色样式
    private int progressShaderType = ImageProgressStyle.SWEEP_GRADIENT_TYPE;
    private RectF rectF;//用于计算圆角的位置和弧度
    private OnImageProgressPreDrawListener listener;

    public ImageProgress(Context context) {
        this(context, null);
    }

    public ImageProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageProgress);
        strokeWidth = (int) typedArray.getDimension(R.styleable.ImageProgress_strokeWidth, strokeWidth);//进度条宽度
        maxProgress = typedArray.getFloat(R.styleable.ImageProgress_maxProgress, maxProgress);//最大进度
        currentProgress = typedArray.getFloat(R.styleable.ImageProgress_currentProgress, currentProgress);//当前进度
        backgroundColor = typedArray.getColor(R.styleable.ImageProgress_backgroundColor, backgroundColor);//进度背景颜色
        progressColor = typedArray.getColor(R.styleable.ImageProgress_progressColor, progressColor);//进度颜色
        textColor = typedArray.getColor(R.styleable.ImageProgress_textColor, textColor);//进度颜色
        textSize = typedArray.getDimension(R.styleable.ImageProgress_textSize, textSize);//进度值字体大小
        //渐变色
        progressColors[0] = typedArray.getColor(R.styleable.ImageProgress_progressColors_start, progressColors[0]);
        progressColors[1] = typedArray.getColor(R.styleable.ImageProgress_progressColors_center, progressColors[1]);
        progressColors[2] = typedArray.getColor(R.styleable.ImageProgress_progressColors_end, progressColors[2]);
        progressColors[3] = progressColors[0];
        roundRect = (int) typedArray.getDimension(R.styleable.ImageProgress_roundRect, roundRect);//进度值字体大小
        //进度条图片上层背景颜色
        progressLoadingBackground = typedArray.getColor(R.styleable.ImageProgress_progressLoadingBackground, progressLoadingBackground);
        //是否有加载背景
        hasLoadingBackground = typedArray.getBoolean(R.styleable.ImageProgress_hasLoadingBackground, hasLoadingBackground);
        //进度为100%的时候是否显示进度值
        hasLastText = typedArray.getBoolean(R.styleable.ImageProgress_hasLastText, hasLastText);
        //是否有进度值 提示
        hasTextHint = typedArray.getBoolean(R.styleable.ImageProgress_hasTextHint, hasTextHint);
        progressStyle = typedArray.getInt(R.styleable.ImageProgress_progressStyle, progressStyle);//进度样式(默认,渐变)
        progressIndexAndOri = typedArray.getInt(R.styleable.ImageProgress_progressIndexAndOri, progressIndexAndOri);//进度方向和位置
        progressShaderType = typedArray.getInt(R.styleable.ImageProgress_progressShaderType, progressShaderType);//渐变样式
        Drawable backgroundIcon = typedArray.getDrawable(R.styleable.ImageProgress_backgroundIcon);
        if (backgroundIcon != null) {
            BitmapDrawable bd = (BitmapDrawable) backgroundIcon;
            bitmap = bd.getBitmap();
        }
        typedArray.recycle();
    }

    //初始化
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo_filter_image_empty);
        path = new Path();
        currentPath = new Path();
        defaultWidth = DensityUtils.dp2px(context, 150);
        defaultHeight = DensityUtils.dp2px(context, 150);
        roundRect = DensityUtils.dp2px(context, 10);
        strokeWidth = DensityUtils.dp2px(context, 4);
        textSize = DensityUtils.dp2px(context, 14);
        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        int width = onMeasureWidth(widthMode, widthSize);
        int height = onMeasureHeight(heightMode, heightSize);
        setMeasuredDimension(width, height);
    }

    //测量宽度
    private int onMeasureWidth(int widthMode, int widthSize) {
        int result = defaultWidth;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                int width = defaultWidth + (paddingLeft + paddingRight);
                result = MeasureSpec.makeMeasureSpec(Math.min(width, widthSize), widthMode);
                break;
        }
        return result;
    }

    //测量高度
    private int onMeasureHeight(int heightMode, int heightSize) {
        int result = defaultHeight;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                result = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                float height = defaultHeight + (paddingTop + paddingBottom);
                result = MeasureSpec.makeMeasureSpec((int) Math.min(height, heightSize), heightMode);
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //重置path
        currentPath.reset();
        path.reset();

        //计算中心位置
        float x = (getWidth() - (paddingLeft + paddingRight)) / 2;
        float y = (getHeight() - (paddingBottom + paddingTop)) / 2;
        float centerX = paddingLeft + x;
        float centerY = paddingTop + y;

        //计算位置
        strokeLeft = paddingLeft;
        strokeTop = paddingTop;
        strokeRight = getWidth() - paddingRight;
        strokeBottom = getHeight() - paddingBottom;
        //绘制图片
        canvas.drawBitmap(bitmap, null, new Rect(strokeLeft + strokeWidth / 2, strokeTop + strokeWidth / 2, strokeRight - strokeWidth / 2, strokeBottom - strokeWidth / 2), paint);

        //圆角半径不能超过Math.min(width / 2, height / 2)
        if (roundRect > Math.min(getWidth() / 2 - strokeWidth / 2, getHeight() / 2 - strokeWidth / 2)) {
            throw new IllegalThreadStateException("Error :Corner radius greater than minimum (radius>Math.min(width/2,height/2). radius=" + roundRect + ",Math.min(width/2, height/2)=" + Math.min((strokeRight - strokeLeft) / 2, (strokeBottom - strokeTop) / 2));
        }



        paint.setStrokeWidth(strokeWidth);
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.STROKE);//描边
        paint.setShader(null);
        /*
         * 绘制默认进度背景：绘制矩形边框(顺时针)
         */
        strokeLeft = strokeLeft + 1;//加1像素偏移
        strokeTop = strokeTop + 1;
        strokeRight = strokeRight - 1;
        strokeBottom = strokeBottom - 1;
        if (listener != null) {
            listener.onPreDraw((strokeRight - strokeLeft) / 2, (strokeBottom - strokeTop) / 2);
        }
        RectF rect = new RectF(strokeLeft, strokeTop, strokeRight, strokeBottom);
        path.addRoundRect(rect, roundRect, roundRect, Path.Direction.CW);
        //path.addRoundRect(rect, roundRect, roundRect, Path.Direction.CCW);
        canvas.drawPath(path, paint);

        int progressLineHeight = strokeBottom - strokeTop - 2 * roundRect+2;//进度条纵向长度,将减去的像素偏差加回来
        int progressLineWidth = strokeRight - strokeTop - 2 * roundRect+2;//进度条横向长度
        int circumferenceMax = (int) (2 * Math.PI * roundRect);//周长
        int maxLength = progressLineHeight * 2 + progressLineWidth * 2 + circumferenceMax;//总进度对应的最大长度
        float angle = (float) (180 / (Math.PI * roundRect));
        int currentLength = (int) ((currentProgress * maxLength) / maxProgress);//当前进度对应的长度
        Log.e("ImageProgressTag", "currentLength=" + currentLength + ",maxLength =" + maxLength);

        //绘制当前进度,并计算旋转渐变角度
        float tanAngle = (float) Math.toDegrees(atan((x - roundRect) / y));//利用tan求出旋转角度变量
        float rotateAngle = 0;
        switch (progressIndexAndOri) {//设置渐变色的旋转角度
            case ImageProgressStyle.LEFT_TOP_CW:
                pathLeftTopCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = 270 - tanAngle;
                break;
            case ImageProgressStyle.LEFT_TOP_CCW:
                pathLeftTopCCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = 180 + tanAngle;
                break;
            case ImageProgressStyle.LEFT_BOTTOM_CW:
                pathLeftBottomCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = 180 - tanAngle;
                break;
            case ImageProgressStyle.LEFT_BOTTOM_CCW:
                pathLeftBottomCCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = 90 + tanAngle;
                break;
            case ImageProgressStyle.RIGHT_TOP_CW:
                pathRightTopCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = -tanAngle;
                break;
            case ImageProgressStyle.RIGHT_TOP_CCW:
                pathRightTopCCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = -(90 - tanAngle);
                break;
            case ImageProgressStyle.RIGHT_BOTTOM_CW:
                pathRightBottomCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = 90 - tanAngle;
                break;
            case ImageProgressStyle.RIGHT_BOTTOM_CCW:
                pathRightBottomCCWRound(progressLineHeight, progressLineWidth, maxLength, circumferenceMax, angle, currentLength);
                rotateAngle = tanAngle;
                break;
        }
        //设置是否渐变
        if (progressStyle == ImageProgressStyle.DEFAULT_TYPE) {
            paint.setColor(progressColor);
        } else if (progressStyle == ImageProgressStyle.GRADIENT_TYPE) {
            setShader(centerX, centerY, rotateAngle);
        }
        canvas.drawPath(currentPath, paint);
        //绘制加载背景
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        if (hasLoadingBackground && currentProgress < 100) {
            RectF rectLoading = new RectF(strokeLeft + strokeWidth / 2, strokeTop + strokeWidth / 2, strokeRight - strokeWidth / 2, strokeBottom - strokeWidth / 2);
            paint.setColor(0x33ffffff & progressLoadingBackground);//设置图片加载动画的背景
            canvas.drawRoundRect(rectLoading, centerX, centerY, paint);
        }
        if (hasTextHint) {
            //绘制文字
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            String strProgress = (int) currentProgress + "%";
            int[] ints = onMeasureText(strProgress);//计算文字宽高
            if (currentProgress < 100 || hasLastText)
                canvas.drawText(strProgress, centerX - ints[0] / 2, centerY + ints[1] / 2, paint);
        }
    }

    /**
     * 设置渐变色
     *
     * @param centerX     控件中心X位置
     * @param centerY     控件中心Y位置
     * @param rotateAngle 旋转角度
     */
    private void setShader(float centerX, float centerY, float rotateAngle) {
        Shader mShader;
        if (progressShaderType == ImageProgressStyle.LINEAR_GRADIENT_TYPE) {
            mShader = new LinearGradient(0, 0, 100, 200, progressColors, null, Shader.TileMode.REPEAT);
        } else {
            mShader = new SweepGradient(centerX, centerY, progressColors, null);
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(rotateAngle, centerX, centerY);
        mShader.setLocalMatrix(matrix);
        paint.setShader(mShader);
    }

    /**
     * 圆角,左上，顺时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathLeftTopCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeLeft + roundRect, strokeTop);
        if (currentLength >= progressLineWidth) {
            currentPath.lineTo(strokeRight - roundRect, strokeTop);
            //RectF rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
            rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
            if (currentLength <= progressLineWidth + circumferenceMax / 4) {
                int circumference = currentLength - progressLineWidth;
                currentPath.arcTo(rectF, 270, angle * circumference);
            } else {
                currentPath.arcTo(rectF, 270, 90);
                if (currentLength >= progressLineWidth + circumferenceMax / 4 + progressLineHeight) {
                    currentPath.lineTo(strokeRight, strokeBottom - roundRect);
//                    rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                    rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                    if (currentLength <= progressLineWidth + 2 * circumferenceMax / 4 + progressLineHeight) {
                        int circumference = (currentLength - (progressLineWidth + circumferenceMax / 4 + progressLineHeight));
                        currentPath.arcTo(rectF, 0, angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 0, 90);
                        if (currentLength >= progressLineWidth * 2 + progressLineHeight + 2 * circumferenceMax / 4) {
                            currentPath.lineTo(strokeLeft + roundRect, strokeBottom);
//                            rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                            rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                            if (currentLength <= progressLineWidth * 2 + progressLineHeight + 3 * circumferenceMax / 4) {
                                int circumference = (currentLength - (progressLineWidth * 2 + 2 * circumferenceMax / 4 + progressLineHeight));
                                currentPath.arcTo(rectF, 90, angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 90, 90);
                                if (currentLength >= progressLineWidth * 2 + 2 * progressLineHeight + 3 * circumferenceMax / 4) {
//                                    rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                                    rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 180, 90);
                                        currentPath.close();
                                    } else {
                                        int circumference = (currentLength - (progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2));
                                        currentPath.arcTo(rectF, 180, angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeLeft, strokeBottom - roundRect - (currentLength - progressLineWidth * 2 - progressLineHeight - (float) (3 * circumferenceMax / 4)));
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineWidth - progressLineHeight - (float) (2 * circumferenceMax / 4)), strokeBottom);
                        }
                    }
                } else {
                    currentPath.lineTo(strokeRight, strokeTop + roundRect + (float) (currentLength - progressLineWidth - (circumferenceMax / 4)));
                }
            }
        } else {
            currentPath.lineTo(strokeLeft + currentLength + roundRect, strokeTop);
        }
    }

    /**
     * 圆角,左上，逆时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathLeftTopCCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeLeft, strokeTop + roundRect);
        if (currentLength >= progressLineHeight) {
            currentPath.lineTo(strokeLeft, strokeBottom - roundRect);
//            RectF rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
            rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
            if (currentLength <= progressLineHeight + circumferenceMax / 4) {
                int circumference = currentLength - progressLineHeight;
                currentPath.arcTo(rectF, 180, -angle * circumference);
            } else {
                currentPath.arcTo(rectF, 180, -90);
                if (currentLength >= progressLineHeight + progressLineWidth + circumferenceMax / 4) {
                    currentPath.lineTo(strokeRight - roundRect, strokeBottom);
//                    rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                    rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                    if (currentLength <= progressLineHeight + progressLineWidth + 2 * circumferenceMax / 4) {
                        int circumference = currentLength - progressLineHeight - circumferenceMax / 4 - progressLineWidth;
                        currentPath.arcTo(rectF, 90, -angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 90, -90);
                        if (currentLength >= progressLineHeight * 2 + progressLineWidth + 2 * circumferenceMax / 4) {
                            currentPath.lineTo(strokeRight, strokeTop + roundRect);
//                            rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                            rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                            if (currentLength <= progressLineHeight * 2 + progressLineWidth + 3 * circumferenceMax / 4) {
                                int circumference = currentLength - progressLineHeight * 2 - progressLineWidth - 2 * circumferenceMax / 4;
                                currentPath.arcTo(rectF, 0, -angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 0, -90);
                                if (currentLength >= progressLineHeight * 2 + progressLineWidth * 2 + 3 * circumferenceMax / 4) {
//                                    rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                                    rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 270, -90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - (progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2);
                                        currentPath.arcTo(rectF, 270, -angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineHeight * 2 - progressLineWidth - 3 * circumferenceMax / 4), strokeTop);
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeRight, strokeBottom - roundRect - (currentLength - progressLineHeight - progressLineWidth - 2 * circumferenceMax / 4));
                        }
                    }
                } else {
                    currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineHeight - circumferenceMax / 4), strokeBottom);
                }
            }
        } else {
            currentPath.lineTo(strokeLeft, strokeTop + roundRect + currentLength);
        }
    }

    /**
     * 圆角,左下，顺时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathLeftBottomCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeLeft, strokeBottom - roundRect);
        if (currentLength >= progressLineHeight) {
            currentPath.lineTo(strokeLeft, strokeTop + roundRect);
//            RectF rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
            rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
            if (currentLength <= progressLineHeight + circumferenceMax / 4) {
                int circumference = currentLength - progressLineHeight;
                currentPath.arcTo(rectF, 180, angle * circumference);
            } else {
                currentPath.arcTo(rectF, 180, 90);
                if (currentLength >= progressLineHeight + progressLineWidth + circumferenceMax / 4) {
                    currentPath.lineTo(strokeRight - roundRect, strokeTop);
//                    rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                    rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                    if (currentLength <= progressLineHeight + progressLineWidth + 2 * circumferenceMax / 4) {
                        int circumference = currentLength - progressLineHeight - progressLineWidth - circumferenceMax / 4;
                        currentPath.arcTo(rectF, 270, angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 270, 90);
                        if (currentLength >= progressLineHeight * 2 + progressLineWidth + 2 * circumferenceMax / 4) {
                            currentPath.lineTo(strokeRight, strokeBottom - roundRect);
//                            rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                            rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                            if (currentLength <= progressLineHeight * 2 + progressLineWidth + 3 * circumferenceMax / 4) {
                                int circumference = currentLength - progressLineHeight * 2 - progressLineWidth - 2 * circumferenceMax / 4;
                                currentPath.arcTo(rectF, 0, angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 0, 90);
                                if (currentLength >= progressLineHeight * 2 + progressLineWidth * 2 + 3 * circumferenceMax / 4) {
                                    currentPath.lineTo(strokeLeft + roundRect, strokeBottom);
//                                    rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                                    rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 90, 90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - (progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2);
                                        currentPath.arcTo(rectF, 90, angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineHeight * 2 - progressLineWidth - 3 * circumferenceMax / 4), strokeBottom);
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeRight, strokeTop + roundRect + (currentLength - progressLineHeight - progressLineWidth - 2 * circumferenceMax / 4));
                        }
                    }
                } else {
                    currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineHeight - circumferenceMax / 4), strokeTop);
                }
            }
        } else {
            currentPath.lineTo(strokeLeft, strokeBottom - roundRect - currentLength);
        }
    }

    /**
     * 圆角，左下，逆时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathLeftBottomCCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeLeft + roundRect, strokeBottom);
        if (currentLength >= progressLineWidth) {
            currentPath.lineTo(strokeRight - roundRect, strokeBottom);
//            RectF rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
            rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
            if (currentLength <= progressLineWidth + circumferenceMax / 4) {
                int circumference = currentLength - progressLineWidth;
                currentPath.arcTo(rectF, 90, -angle * circumference);
            } else {
                currentPath.arcTo(rectF, 90, -90);
                if (currentLength >= progressLineWidth + circumferenceMax / 4 + progressLineHeight) {
                    currentPath.lineTo(strokeRight, strokeTop + roundRect);
//                    rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                    rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                    if (currentLength <= progressLineWidth + 2 * circumferenceMax / 4 + progressLineHeight) {
                        int circumference = currentLength - progressLineWidth - circumferenceMax / 4 - progressLineHeight;
                        currentPath.arcTo(rectF, 0, -angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 0, -90);
                        if (currentLength >= progressLineWidth * 2 + 2 * circumferenceMax / 4 + progressLineHeight) {
                            currentPath.lineTo(strokeLeft + roundRect, strokeTop);
//                            rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                            rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                            if (currentLength <= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight) {
                                int circumference = currentLength - progressLineWidth * 2 - 2 * circumferenceMax / 4 - progressLineHeight;
                                currentPath.arcTo(rectF, 270, -angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 270, -90);
                                if (currentLength >= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2) {
                                    currentPath.lineTo(strokeLeft, strokeBottom - roundRect);
//                                    rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                                    rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 180, -90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight * 2;
                                        currentPath.arcTo(rectF, 180, -angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeLeft, strokeTop + roundRect + currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight);
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineWidth - 2 * circumferenceMax / 4 - progressLineHeight), strokeTop);
                        }
                    }
                } else {
                    currentPath.lineTo(strokeRight, strokeBottom - roundRect - (currentLength - progressLineWidth - circumferenceMax / 4));
                }
            }

        } else {
            currentPath.lineTo(strokeLeft + currentLength + roundRect, strokeBottom);
        }
    }

    /**
     * 圆角，右上，顺时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathRightTopCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeRight, strokeTop + roundRect);
        if (currentLength >= progressLineHeight) {
            currentPath.lineTo(strokeRight, strokeBottom - roundRect);
//            RectF rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
            rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
            if (currentLength <= progressLineHeight + circumferenceMax / 4) {
                int circumference = currentLength - progressLineHeight;
                currentPath.arcTo(rectF, 0, angle * circumference);
            } else {
                currentPath.arcTo(rectF, 0, 90);
                if (currentLength >= progressLineHeight + circumferenceMax / 4 + progressLineWidth) {
                    currentPath.lineTo(strokeLeft + roundRect, strokeBottom);
//                    rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                    rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                    if (currentLength <= progressLineHeight + 2 * circumferenceMax / 4 + progressLineWidth) {
                        int circumference = currentLength - progressLineWidth - circumferenceMax / 4 - progressLineHeight;
                        currentPath.arcTo(rectF, 90, angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 90, 90);
                        if (currentLength >= progressLineHeight * 2 + 2 * circumferenceMax / 4 + progressLineWidth) {
                            currentPath.lineTo(strokeLeft, strokeTop + roundRect);
//                            rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                            rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                            if (currentLength <= progressLineHeight * 2 + 3 * circumferenceMax / 4 + progressLineWidth) {
                                int circumference = currentLength - progressLineWidth - 2 * circumferenceMax / 4 - 2 * progressLineHeight;
                                currentPath.arcTo(rectF, 180, angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 180, 90);
                                if (currentLength >= progressLineHeight * 2 + 3 * circumferenceMax / 4 + progressLineWidth * 2) {
                                    currentPath.lineTo(strokeRight - roundRect, strokeTop);
//                                    rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                                    rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 270, 90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - 2 * progressLineHeight;
                                        currentPath.arcTo(rectF, 270, angle * circumference);
                                    }

                                } else {
                                    currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineHeight * 2 - 3 * circumferenceMax / 4 - progressLineWidth), strokeTop);
                                }
                            }

                        } else {
                            currentPath.lineTo(strokeLeft, strokeBottom - roundRect - (currentLength - progressLineHeight - 2 * circumferenceMax / 4 - progressLineWidth));
                        }
                    }
                } else {
                    currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineHeight - circumferenceMax / 4), strokeBottom);
                }
            }

        } else {
            currentPath.lineTo(strokeRight, strokeTop + roundRect + currentLength);
        }
    }

    /**
     * 圆角，右上，逆时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathRightTopCCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeRight - roundRect, strokeTop);
        if (currentLength >= progressLineWidth) {
            currentPath.lineTo(strokeLeft + roundRect, strokeTop);
//            RectF rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
            rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
            if (currentLength <= progressLineWidth + circumferenceMax / 4) {
                int circumference = currentLength - progressLineWidth;
                currentPath.arcTo(rectF, 270, -angle * circumference);
            } else {
                currentPath.arcTo(rectF, 270, -90);
                if (currentLength >= progressLineWidth + circumferenceMax / 4 + progressLineHeight) {
                    currentPath.lineTo(strokeLeft, strokeBottom - roundRect);
//                    rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                    rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                    if (currentLength <= progressLineWidth + 2 * circumferenceMax / 4 + progressLineHeight) {
                        int circumference = currentLength - progressLineWidth - circumferenceMax / 4 - progressLineHeight;
                        currentPath.arcTo(rectF, 180, -angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 180, -90);
                        if (currentLength >= progressLineWidth * 2 + 2 * circumferenceMax / 4 + progressLineHeight) {
                            currentPath.lineTo(strokeRight - roundRect, strokeBottom);
//                            rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                            rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                            if (currentLength <= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight) {
                                int circumference = currentLength - progressLineWidth * 2 - 2 * circumferenceMax / 4 - progressLineHeight;
                                currentPath.arcTo(rectF, 90, -angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 90, -90);
                                if (currentLength >= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2) {
                                    currentPath.lineTo(strokeRight, strokeTop + roundRect);
//                                    rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                                    rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 0, -90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight * 2;
                                        currentPath.arcTo(rectF, 0, -angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeRight, strokeBottom - roundRect - (currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight));
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineWidth - 2 * circumferenceMax / 4 - progressLineHeight), strokeBottom);
                        }
                    }
                } else {
                    currentPath.lineTo(strokeLeft, strokeTop + roundRect + (currentLength - progressLineWidth - circumferenceMax / 4));
                }
            }
        } else {
            currentPath.lineTo(strokeRight - roundRect - currentLength, strokeTop);
        }
    }

    /**
     * 圆角，右下,顺时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathRightBottomCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeRight - roundRect, strokeBottom);
        if (currentLength >= progressLineWidth) {
            currentPath.lineTo(strokeLeft + roundRect, strokeBottom);
//            RectF rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
            rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
            if (currentLength <= progressLineWidth + circumferenceMax / 4) {
                int circumference = currentLength - progressLineWidth;
                currentPath.arcTo(rectF, 90, angle * circumference);
            } else {
                currentPath.arcTo(rectF, 90, 90);
                if (currentLength >= progressLineWidth + circumferenceMax / 4 + progressLineHeight) {
                    currentPath.lineTo(strokeLeft, strokeTop + roundRect);
//                    rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                    rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                    if (currentLength <= progressLineWidth + 2 * circumferenceMax / 4 + progressLineHeight) {
                        int circumference = currentLength - progressLineWidth - circumferenceMax / 4 - progressLineHeight;
                        currentPath.arcTo(rectF, 180, angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 180, 90);
                        if (currentLength >= progressLineWidth * 2 + 2 * circumferenceMax / 4 + progressLineHeight) {
                            currentPath.lineTo(strokeRight - roundRect, strokeTop);
//                            rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                            rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
                            if (currentLength <= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight) {
                                int circumference = currentLength - progressLineWidth * 2 - 2 * circumferenceMax / 4 - progressLineHeight;
                                currentPath.arcTo(rectF, 270, angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 270, 90);
                                if (currentLength >= progressLineWidth * 2 + 3 * circumferenceMax / 4 + progressLineHeight * 2) {
                                    currentPath.lineTo(strokeRight, strokeBottom - roundRect);
//                                    rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                                    rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                                    if (currentLength > maxLength) {
                                        currentPath.arcTo(rectF, 0, 90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight * 2;
                                        currentPath.arcTo(rectF, 0, angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeRight, strokeTop + roundRect + (currentLength - progressLineWidth * 2 - 3 * circumferenceMax / 4 - progressLineHeight));
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineWidth - 2 * circumferenceMax / 4 - progressLineHeight), strokeTop);
                        }
                    }
                } else {
                    currentPath.lineTo(strokeLeft, strokeBottom - roundRect - (currentLength - progressLineWidth - circumferenceMax / 4));
                }
            }
        } else {
            currentPath.lineTo(strokeRight - roundRect - currentLength, strokeBottom);
        }
    }

    /**
     * 圆角，右下,逆时针
     *
     * @param progressLineHeight 进度数值高度
     * @param progressLineWidth  进度水平宽度
     * @param maxLength          进度最大长度
     * @param circumferenceMax   圆角周长
     * @param angle              角度计算参数
     * @param currentLength      当前长度
     */
    private void pathRightBottomCCWRound(int progressLineHeight, int progressLineWidth, int maxLength, int circumferenceMax, float angle, int currentLength) {
        currentPath.moveTo(strokeRight, strokeBottom - roundRect);
        if (currentLength >= progressLineHeight) {
            currentPath.lineTo(strokeRight, strokeTop + roundRect);
//            RectF rectF = new RectF(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
            rectF.set(strokeRight - 2 * roundRect, strokeTop, strokeRight, strokeTop + 2 * roundRect);
            if (currentLength <= progressLineHeight + circumferenceMax / 4) {
                int circumference = currentLength - progressLineHeight;
                currentPath.arcTo(rectF, 0, -angle * circumference);
            } else {
                currentPath.arcTo(rectF, 0, -90);
                if (currentLength >= progressLineHeight + circumferenceMax / 4 + progressLineWidth) {
                    currentPath.lineTo(strokeLeft + roundRect, strokeTop);
//                    rectF = new RectF(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                    rectF.set(strokeLeft, strokeTop, strokeLeft + 2 * roundRect, strokeTop + 2 * roundRect);
                    if (currentLength <= progressLineHeight + 2 * circumferenceMax / 4 + progressLineWidth) {
                        int circumference = currentLength - progressLineHeight - circumferenceMax / 4 - progressLineWidth;
                        currentPath.arcTo(rectF, 270, -angle * circumference);
                    } else {
                        currentPath.arcTo(rectF, 270, -90);
                        if (currentLength >= progressLineHeight * 2 + 2 * circumferenceMax / 4 + progressLineWidth) {
                            currentPath.lineTo(strokeLeft, strokeBottom - roundRect);
//                            rectF = new RectF(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                            rectF.set(strokeLeft, strokeBottom - 2 * roundRect, strokeLeft + 2 * roundRect, strokeBottom);
                            if (currentLength <= progressLineHeight * 2 + 3 * circumferenceMax / 4 + progressLineWidth) {
                                int circumference = currentLength - progressLineHeight * 2 - 2 * circumferenceMax / 4 - progressLineWidth;
                                currentPath.arcTo(rectF, 180, -angle * circumference);
                            } else {
                                currentPath.arcTo(rectF, 180, -90);
                                if (currentLength >= progressLineHeight * 2 + 3 * circumferenceMax / 4 + 2 * progressLineWidth) {
                                    currentPath.lineTo(strokeRight - roundRect, strokeBottom);
//                                    rectF = new RectF(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                                    rectF.set(strokeRight - 2 * roundRect, strokeBottom - 2 * roundRect, strokeRight, strokeBottom);
                                    if (currentLength >= maxLength) {
                                        currentPath.arcTo(rectF, 90, -90);
                                        currentPath.close();
                                    } else {
                                        int circumference = currentLength - progressLineHeight * 2 - 3 * circumferenceMax / 4 - progressLineWidth * 2;
                                        currentPath.arcTo(rectF, 90, -angle * circumference);
                                    }
                                } else {
                                    currentPath.lineTo(strokeLeft + roundRect + (currentLength - progressLineHeight * 2 - 3 * circumferenceMax / 4 - progressLineWidth), strokeBottom);
                                }
                            }
                        } else {
                            currentPath.lineTo(strokeLeft, strokeTop + roundRect + (currentLength - progressLineHeight - 2 * circumferenceMax / 4 - progressLineWidth));
                        }
                    }
                } else {
                    currentPath.lineTo(strokeRight - roundRect - (currentLength - progressLineHeight - circumferenceMax / 4), strokeTop);
                }
            }

        } else {
            currentPath.lineTo(strokeRight, strokeBottom - roundRect - currentLength);
        }
    }

    /**
     * 计算进度文字宽高
     *
     * @param progress 当前进度
     * @return 返回宽高
     */
    private int[] onMeasureText(String progress) {
        Rect rect = new Rect();
        paint.getTextBounds(progress, 0, progress.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        return new int[]{textWidth, textHeight};
    }

    /**
     * 设置渐变颜色
     *
     * @param startColor
     * @param centerColor
     * @param endColor
     * @return
     */
    public ImageProgress setProgressColors(String startColor, String centerColor, String endColor) {
        this.progressColors[0] = Color.parseColor(startColor);
        this.progressColors[1] = Color.parseColor(centerColor);
        this.progressColors[2] = Color.parseColor(endColor);
        this.progressColors[3] = Color.parseColor(startColor);
        return this;
    }

    public int[] getProgressColors() {
        return new int[]{progressColors[0], progressColors[1], progressColors[2]};
    }

    public ImageProgress setProgress(float progress) {
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
    public ImageProgress setMaxProgress(int maxProgress) {
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
    public ImageProgress setProgressStyle(@ImageProgressStyle.Type int type) {
        this.progressStyle = type;
        return this;
    }

    public int getProgressStyle() {
        return progressStyle;
    }

    /**
     * 设置方向和起点
     *
     * @param indexAndOri
     * @return
     */
    public ImageProgress setProgressIndexAndOri(@ImageProgressStyle.StartIndex int indexAndOri) {
        this.progressIndexAndOri = indexAndOri;
        return this;
    }

    public int getProgressIndexAndOri() {
        return progressIndexAndOri;
    }

    /**
     * 设置进度条宽度
     *
     * @param strokeWidth
     * @return
     */
    public ImageProgress setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置背景颜色
     *
     * @param color
     * @return
     */
    public ImageProgress setBackgroundColor(String color) {
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
    public ImageProgress setProgressColor(String color) {
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
    public ImageProgress setTextColor(String color) {
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
    public ImageProgress setTextSize(float size) {
        this.textSize = size;
        return this;
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置圆角半径
     *
     * @param size
     * @return
     */
    public ImageProgress setRoundRect(float size) {
        this.roundRect = (int) size;
        return this;
    }

    public float getRoundRect() {
        return roundRect;
    }

    /**
     * 设置加载背景颜色
     *
     * @param color
     * @return
     */
    public ImageProgress setProgressLoadingBackground(String color) {
        this.progressLoadingBackground = Color.parseColor(color);
        return this;
    }

    public int getProgressLoadingBackground() {
        return progressLoadingBackground;
    }

    /**
     * 设置是否显示加载背景
     *
     * @param hasLoadingBackground
     * @return
     */
    public ImageProgress setHasLoadingBackground(boolean hasLoadingBackground) {
        this.hasLoadingBackground = hasLoadingBackground;
        return this;
    }

    public boolean getHasLoadingBackground() {
        return hasLoadingBackground;
    }

    /**
     * 设置当进度为100%时,是否显示进度值
     *
     * @param hasLastText
     * @return
     */
    public ImageProgress setHasLastText(boolean hasLastText) {
        this.hasLastText = hasLastText;
        return this;
    }

    public boolean getHasLastText() {
        return hasLastText;
    }

    /**
     * 设置渐变样式
     *
     * @param shaderType
     * @return
     */
    public ImageProgress setProgressShaderType(@ImageProgressStyle.ShaderType int shaderType) {
        this.progressShaderType = shaderType;
        return this;
    }

    public int getProgressShaderType() {
        return progressShaderType;
    }

    /**
     * 是否有进度值提示
     *
     * @param hasTextHint
     * @return
     */
    public ImageProgress setHasTextHint(boolean hasTextHint) {
        this.hasTextHint = hasTextHint;
        return this;
    }

    public boolean getHasTextHint() {
        return hasTextHint;
    }

    public void builder() {
        postInvalidate();
    }

    /**
     * 控件中图片绘制完宽高监听
     */
    public interface OnImageProgressPreDrawListener {
        void onPreDraw(int width, int height);
    }

    public void setOnImageProgressPreDrawListener(OnImageProgressPreDrawListener listener) {
        this.listener = listener;
    }

    public void removePreDrawListener() {
        this.listener = null;
    }
}
