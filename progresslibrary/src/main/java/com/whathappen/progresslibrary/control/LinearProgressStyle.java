package com.whathappen.progresslibrary.control;

import android.support.annotation.IntDef;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/6 ;
 * @Description 水平进度样式
 */
public class LinearProgressStyle {

    public static final int DEFAULT_TYPE = 0;//默认
    public static final int GRADIENT_TYPE = 1;//渐变

    /**
     * 样式
     */
    @IntDef({DEFAULT_TYPE, GRADIENT_TYPE})
    public @interface Type {
    }
}
