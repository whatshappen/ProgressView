package com.whathappen.dialogstyle;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.whathappen.progresslibrary.control.ImageProgressStyle;
import com.whathappen.progresslibrary.utils.DensityUtils;
import com.whathappen.progresslibrary.view.ImageProgress;

/**
 * Author： Wangw
 * Created on： 2018/5/3.
 * Email：
 * Description：
 */

public class ImageProgressActivity extends Activity implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private ImageProgress image_progress;
    float progress = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            image_progress.setProgress(progress).builder();
            if (progress < 100) {
                progress = progress + 2f;
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };
    private SeekBar sb_progress;
    private SeekBar sb_strokeWidth;
    private SeekBar sb_roundRect;
    private RadioGroup rg1_style;
    private RadioGroup rg1_index_ori;
    private RadioButton rb_linear_gradient;
    private RadioButton rb_sweep_gradient;
    private RadioButton rb_no_gradient;
    private CheckBox cb_hasLoadingBackground;
    private CheckBox cb_hasText;
    private CheckBox cb_hasLastText;
    private float minStrokeWidth;
    private float maxStrokeWidth;
    private float minRoundRect;
    private float maxRoundRect;
    private RadioButton rb_left_top_cw;
    private RadioButton rb_left_top_ccw;
    private RadioButton rb_left_bottom_cw;
    private RadioButton rb_left_bottom_ccw;
    private RadioButton rb_right_top_cw;
    private RadioButton rb_right_top_ccw;
    private RadioButton rb_right_bottom_cw;
    private RadioButton rb_right_bottom_ccw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_imageprogress);
        initView();
        initData();
    }

    private void initView() {
        image_progress = (ImageProgress) findViewById(R.id.image_progress);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        sb_strokeWidth = (SeekBar) findViewById(R.id.sb_strokeWidth);
        sb_roundRect = (SeekBar) findViewById(R.id.sb_roundRect);
        rg1_style = (RadioGroup) findViewById(R.id.rg1_style);
        rg1_index_ori = (RadioGroup) findViewById(R.id.rg1_index_ori);
        rb_linear_gradient = (RadioButton) findViewById(R.id.rb_linear_gradient);
        rb_sweep_gradient = (RadioButton) findViewById(R.id.rb_sweep_gradient);
        rb_no_gradient = (RadioButton) findViewById(R.id.rb_no_gradient);
        cb_hasLoadingBackground = (CheckBox) findViewById(R.id.cb_hasLoadingBackground);
        cb_hasText = (CheckBox) findViewById(R.id.cb_hasText);
        cb_hasLastText = (CheckBox) findViewById(R.id.cb_hasLastText);
        sb_progress.setOnSeekBarChangeListener(this);
        sb_strokeWidth.setOnSeekBarChangeListener(this);
        sb_roundRect.setOnSeekBarChangeListener(this);
        rg1_style.setOnCheckedChangeListener(this);
        rg1_index_ori.setOnCheckedChangeListener(this);
        //位置,方向
        rb_left_top_cw = (RadioButton) findViewById(R.id.rb_left_top_cw);
        rb_left_top_ccw = (RadioButton) findViewById(R.id.rb_left_top_ccw);
        rb_left_bottom_cw = (RadioButton) findViewById(R.id.rb_left_bottom_cw);
        rb_left_bottom_ccw = (RadioButton) findViewById(R.id.rb_left_bottom_ccw);
        rb_right_top_cw = (RadioButton) findViewById(R.id.rb_right_top_cw);
        rb_right_top_ccw = (RadioButton) findViewById(R.id.rb_right_top_ccw);
        rb_right_bottom_cw = (RadioButton) findViewById(R.id.rb_right_bottom_cw);
        rb_right_bottom_ccw = (RadioButton) findViewById(R.id.rb_right_bottom_ccw);

    }

    private void initData() {
        minStrokeWidth = DensityUtils.dp2px(this, 0.5f);
        maxStrokeWidth = DensityUtils.dp2px(this, 8f);
        float progress = image_progress.getProgress();
        float maxProgress = image_progress.getMaxProgress();
        int currentProgress = (int) (progress * 100 / maxProgress);
        sb_progress.setProgress(currentProgress);//设置当前进度
        //设置渐变样式
        int progressStyle = image_progress.getProgressStyle();
        if (progressStyle == ImageProgressStyle.DEFAULT_TYPE) {
            rb_no_gradient.setChecked(true);
        } else if (progressStyle == ImageProgressStyle.GRADIENT_TYPE) {
            int progressShaderType = image_progress.getProgressShaderType();
            if (progressShaderType == ImageProgressStyle.LINEAR_GRADIENT_TYPE) {
                rb_linear_gradient.setChecked(true);
            } else if (progressShaderType == ImageProgressStyle.SWEEP_GRADIENT_TYPE) {
                rb_sweep_gradient.setChecked(true);
            }
        }
        //是否显示进度值
        boolean hasTextHint = image_progress.getHasTextHint();
        if (hasTextHint) {
            cb_hasText.setChecked(true);
            cb_hasText.setText("是");
        } else {
            cb_hasText.setChecked(false);
            cb_hasText.setText("否");
        }
        cb_hasText.setOnCheckedChangeListener(this);
        //是否有加载背景
        boolean hasLoadingBackground = image_progress.getHasLoadingBackground();
        if (hasLoadingBackground) {
            cb_hasLoadingBackground.setChecked(true);
            cb_hasLoadingBackground.setText("是");
        } else {
            cb_hasLoadingBackground.setChecked(false);
            cb_hasLoadingBackground.setText("否");
        }
        cb_hasLoadingBackground.setOnCheckedChangeListener(this);
        //当进度为100%是,是否显示进度值
        boolean hasLastText = image_progress.getHasLastText();
        if (hasLastText) {
            cb_hasLastText.setChecked(true);
            cb_hasLastText.setText("是");
        } else {
            cb_hasLastText.setChecked(false);
            cb_hasLastText.setText("否");
        }
        cb_hasLastText.setOnCheckedChangeListener(this);
        //进度条宽度
        final float strokeWidth = image_progress.getStrokeWidth();
        int strokeWidthProgress = (int) ((strokeWidth - minStrokeWidth) * 100 / (maxStrokeWidth - minStrokeWidth));
        sb_strokeWidth.setProgress(strokeWidthProgress);
        //圆角
        image_progress.setOnImageProgressPreDrawListener(new ImageProgress.OnImageProgressPreDrawListener() {
            @Override
            public void onPreDraw(int width, int height) {
                minRoundRect = 0;
                maxRoundRect = Math.min(width, height);
                float roundRect = image_progress.getRoundRect();
                int roundRectProgress = (int) ((roundRect - minRoundRect) * 100 / (maxRoundRect - minRoundRect));
                sb_roundRect.setProgress(roundRectProgress);
                image_progress.removePreDrawListener();//移除监听
            }
        });
        //设置起点位置和方向
        int progressIndexAndOri = image_progress.getProgressIndexAndOri();
        switch (progressIndexAndOri) {
            case ImageProgressStyle.LEFT_TOP_CW:
                rb_left_top_cw.setChecked(true);
                break;
            case ImageProgressStyle.LEFT_TOP_CCW:
                rb_left_top_ccw.setChecked(true);
                break;
            case ImageProgressStyle.LEFT_BOTTOM_CW:
                rb_left_bottom_cw.setChecked(true);
                break;
            case ImageProgressStyle.LEFT_BOTTOM_CCW:
                rb_left_bottom_ccw.setChecked(true);
                break;
            case ImageProgressStyle.RIGHT_TOP_CW:
                rb_right_top_cw.setChecked(true);
                break;
            case ImageProgressStyle.RIGHT_TOP_CCW:
                rb_right_top_ccw.setChecked(true);
                break;
            case ImageProgressStyle.RIGHT_BOTTOM_CW:
                rb_right_bottom_cw.setChecked(true);
                break;
            case ImageProgressStyle.RIGHT_BOTTOM_CCW:
                rb_right_bottom_ccw.setChecked(true);
                break;
        }
    }

    //CheckBox
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_hasText:
                cb_hasText.setText(isChecked ? "是" : "否");
                image_progress.setHasTextHint(isChecked).builder();
                break;
            case R.id.cb_hasLoadingBackground:
                cb_hasLoadingBackground.setText(isChecked ? "是" : "否");
                image_progress.setHasLoadingBackground(isChecked).builder();
                break;
            case R.id.cb_hasLastText:
                cb_hasLastText.setText(isChecked ? "是" : "否");
                image_progress.setHasLastText(isChecked).builder();
                break;
        }
    }

    //RadioGroup
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (group.getId() == R.id.rg1_style) {//设置渐变样式
            if (checkedId == R.id.rb_no_gradient) {
                image_progress.setProgressStyle(ImageProgressStyle.DEFAULT_TYPE).builder();
            } else if (checkedId == R.id.rb_linear_gradient) {
                image_progress.setProgressShaderType(ImageProgressStyle.LINEAR_GRADIENT_TYPE).
                        setProgressStyle(ImageProgressStyle.GRADIENT_TYPE).builder();
            } else if (checkedId == R.id.rb_sweep_gradient) {
                image_progress.setProgressShaderType(ImageProgressStyle.SWEEP_GRADIENT_TYPE).
                        setProgressStyle(ImageProgressStyle.GRADIENT_TYPE).builder();
            }
        } else if (group.getId() == R.id.rg1_index_ori) {
            //TODO
            switch (checkedId) {
                case R.id.rb_left_top_cw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.LEFT_TOP_CW).builder();
                    break;
                case R.id.rb_left_top_ccw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.LEFT_TOP_CCW).builder();
                    break;
                case R.id.rb_left_bottom_cw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.LEFT_BOTTOM_CW).builder();
                    break;
                case R.id.rb_left_bottom_ccw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.LEFT_BOTTOM_CCW).builder();
                    break;
                case R.id.rb_right_top_cw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.RIGHT_TOP_CW).builder();
                    break;
                case R.id.rb_right_top_ccw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.RIGHT_TOP_CCW).builder();
                    break;
                case R.id.rb_right_bottom_cw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.RIGHT_BOTTOM_CW).builder();
                    break;
                case R.id.rb_right_bottom_ccw:
                    image_progress.setProgressIndexAndOri(ImageProgressStyle.RIGHT_BOTTOM_CCW).builder();
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_progress://设置进度
                image_progress.setProgress(progress).builder();
                break;
            case R.id.sb_strokeWidth://进度条宽度
                int strokeWidth = (int) (progress * (maxStrokeWidth - minStrokeWidth) / 100 + minStrokeWidth);
                image_progress.setStrokeWidth(strokeWidth).builder();
                break;
            case R.id.sb_roundRect://圆角
                float roundRect = progress * (maxRoundRect - minRoundRect) / 100 + minRoundRect;
                image_progress.setRoundRect(roundRect).builder();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
