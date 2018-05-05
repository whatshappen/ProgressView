package com.whathappen.dialogstyle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/5 ;
 * @Description
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout ll_progress_circle = findViewById(R.id.ll_progress_circle);
        LinearLayout ll_progress_image = findViewById(R.id.ll_progress_image);
        ll_progress_circle.setOnClickListener(this);
        ll_progress_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_progress_circle:
                startActivity(new Intent(this, CirCleProgressActivity.class));
                break;
            case R.id.ll_progress_image:
                startActivity(new Intent(this, ImageProgressActivity.class));
                break;
        }
    }
}
