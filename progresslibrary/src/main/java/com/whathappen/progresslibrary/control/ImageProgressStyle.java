package com.whathappen.progresslibrary.control;

import android.support.annotation.IntDef;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/4 ;
 * @Description 图片进度样式
 */
public class ImageProgressStyle {

    public static final int DEFAULT_TYPE = 0;//默认
    public static final int GRADIENT_TYPE = 1;//渐变

    /**
     * 样式
     */
    @IntDef({DEFAULT_TYPE, GRADIENT_TYPE})
    public @interface Type {
    }

    public static final int LEFT_TOP_CW = 0;//起点:左上,顺时针
    public static final int LEFT_TOP_CCW = 1;//起点:左上,逆时针
    public static final int LEFT_BOTTOM_CW = 2;//左下,顺时针
    public static final int LEFT_BOTTOM_CCW = 3;//左下,逆时针
    public static final int RIGHT_TOP_CW = 4;//右上,顺时针
    public static final int RIGHT_TOP_CCW = 5;//右上,逆时针
    public static final int RIGHT_BOTTOM_CW = 6;//右下,顺时针
    public static final int RIGHT_BOTTOM_CCW = 7;//右下,逆时针

    /**
     * 起始位置,方向
     */
    @IntDef({LEFT_TOP_CW, LEFT_TOP_CCW, LEFT_BOTTOM_CW, LEFT_BOTTOM_CCW, RIGHT_TOP_CW, RIGHT_TOP_CCW, RIGHT_BOTTOM_CW, RIGHT_BOTTOM_CCW})
    public @interface StartIndex {
    }

    public static final int SWEEP_GRADIENT_TYPE = 0;//圆形渐变
    public static final int LINEAR_GRADIENT_TYPE = 1;//线性渐变

    @IntDef({SWEEP_GRADIENT_TYPE, LINEAR_GRADIENT_TYPE})
    public @interface ShaderType {
    }
}
