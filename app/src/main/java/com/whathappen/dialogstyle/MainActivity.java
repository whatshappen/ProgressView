package com.whathappen.dialogstyle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.whathappen.progresslibrary.view.CircleProgress;

public class MainActivity extends AppCompatActivity {
    float progress = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            circleProgress.setProgress(progress).builder();
            if (progress < 100) {
                progress = progress + 1.5f;
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };
    private CircleProgress circleProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleProgress = (CircleProgress) findViewById(R.id.circle_progress);
//        circleProgress.setDialDefaultColor("#dddddd").setTextSize(20).setDialTextSize(15).setDialWidth(1, 0.5f).builder();
        handler.sendEmptyMessageDelayed(0, 100);
    }
}
