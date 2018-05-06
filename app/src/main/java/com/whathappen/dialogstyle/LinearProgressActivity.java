package com.whathappen.dialogstyle;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.whathappen.progresslibrary.control.LinearProgressStyle;
import com.whathappen.progresslibrary.utils.DensityUtils;
import com.whathappen.progresslibrary.view.LinearProgress;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/6 ;
 * @Description 水平进度条
 */
public class LinearProgressActivity extends Activity {

    float progress = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            linear_progress.setProgress(progress).builder();
            if (progress < 100) {
                progress = progress + 2f;
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };
    private LinearProgress linear_progress;
    private SeekBar sb_progress;
    private SeekBar sb_width_progress;
    private SeekBar sb_rx_progress;
    private RadioGroup rg1;
    private RadioButton rb_has_gradient;
    private RadioButton rb_no_gradient;
    private int progressHeight;
    private int minRoundX = 0;
    private int maxRoundX;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_linearprogress);
        maxRoundX = DensityUtils.dp2px(getBaseContext(), 10);
        initView();
        initData();
    }

    private void initView() {
        linear_progress = (LinearProgress) findViewById(R.id.linear_progress);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rb_has_gradient = (RadioButton) findViewById(R.id.rb_has_gradient);
        rb_no_gradient = (RadioButton) findViewById(R.id.rb_no_gradient);
        sb_width_progress = (SeekBar) findViewById(R.id.sb_width_progress);
        sb_rx_progress = (SeekBar) findViewById(R.id.sb_rx_progress);
    }

    private void initData() {
        float progress = linear_progress.getProgress();
        sb_progress.setProgress((int) progress);
        int progressStyle = linear_progress.getProgressStyle();
        if (progressStyle == LinearProgressStyle.DEFAULT_TYPE) {
            rb_no_gradient.setChecked(true);
        } else if (progressStyle == LinearProgressStyle.GRADIENT_TYPE) {
            rb_has_gradient.setChecked(true);
        }
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_has_gradient:
                        linear_progress.setProgressStyle(LinearProgressStyle.GRADIENT_TYPE).builder();
                        break;
                    case R.id.rb_no_gradient:
                        linear_progress.setProgressStyle(LinearProgressStyle.DEFAULT_TYPE).builder();
                        break;
                }
            }
        });
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                linear_progress.setProgress(progress).builder();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        int height;

        linear_progress.setOnLinearProgressPreDrawListener(new LinearProgress.OnLinearProgressPreDrawListener() {
            @Override
            public void onPreDraw() {
                progressHeight = linear_progress.getProgressHeight();
                linear_progress.removePreDrawListener();
            }
        });
        sb_width_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                linear_progress.setProgressHeight(progressHeight + progress * 20 / 100).builder();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        float roundRectX = linear_progress.getRoundRectX();
        int roundXProgress = (int) ((roundRectX - minRoundX) * 100 / (maxRoundX - minRoundX));
        sb_rx_progress.setProgress(roundXProgress);
        sb_rx_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int round = progress * (maxRoundX - minRoundX) / 100 + minRoundX;
                linear_progress.setRoundRectX(round).setRoundRectY(round).builder();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
