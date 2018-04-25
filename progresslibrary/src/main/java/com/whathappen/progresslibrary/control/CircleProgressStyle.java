package com.whathappen.progresslibrary.control;

import android.support.annotation.IntDef;

/**
 * Author： Wangw
 * Created on： 2018/4/25.
 * Email：
 * Description：圆形进度条样式
 */

public class CircleProgressStyle {

    //无渐变，无刻度（默认）
    public static final int DEFAULT_TYPE = 0;
    //有渐变，无刻度
    public static final int GRADIENT_TYPE = 1;
    //无渐变，有刻度
    public static final int DEFAULT_TYPE_DIAL = 2;
    //有渐变，有刻度
    public static final int GRADIENT_TYPE_DIAL = 3;

    /**
     * 进度风格：有无刻度以及渐变色
     */
    @IntDef({DEFAULT_TYPE, GRADIENT_TYPE, DEFAULT_TYPE_DIAL, GRADIENT_TYPE_DIAL})
    public @interface Type {

    }

    //进度值在中间，不显示刻度值（默认）
    public static final int DEFAULT_VALUE_TYPE = 0;
    //进度值在中间，显示刻度值
    public static final int DEFAULT_VALUE_TYPE_SHOW_DIAL = 1;
    //进度值在内测，不显示刻度值
    public static final int INNER_VALUE_TYPE = 2;
    //进度值在外测，不显示刻度值
    public static final int OUTER_VALUE_TYPE = 3;
    //进度值在外测，显示刻度值
    public static final int OUTER_VALUE_TYPE_SHOW_DIAL = 4;

    /**
     * 进度值的显示位置
     */
    @IntDef({DEFAULT_VALUE_TYPE, DEFAULT_VALUE_TYPE_SHOW_DIAL, INNER_VALUE_TYPE
            , OUTER_VALUE_TYPE, OUTER_VALUE_TYPE_SHOW_DIAL})
    public @interface ProgressValueStyle {

    }

    //进度值是int
    public static final int INT_TYPE = 0;
    //进度值是float
    public static final int FLOAT_TYPE = 1;

    /**
     * 进度值的数据类型
     */
    @IntDef({INT_TYPE, FLOAT_TYPE})
    public @interface ProgressValueType {

    }
}
