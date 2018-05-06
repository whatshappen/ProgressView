package com.whathappen.dialogstyle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.whathappen.dialogstyle.adapter.MainRVAdapter;
import com.whathappen.dialogstyle.dao.ItemBean;
import com.whathappen.progresslibrary.view.LinearProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * @author created by Wangw ;
 * @version 1.0
 * @data created time at 2018/5/5 ;
 * @Description
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        List<ItemBean> itemBeen = new ArrayList<>();
        itemBeen.add(new ItemBean(R.mipmap.icon_num_1, R.mipmap.icon_progress_circle, "Circle Progress"));
        itemBeen.add(new ItemBean(R.mipmap.icon_num_2, R.mipmap.icon_progress_image, "Image Progress"));
        itemBeen.add(new ItemBean(R.mipmap.icon_num_3, R.mipmap.icon_progress_linear, "Linear Progress"));
        MainRVAdapter mainRVAdapter = new MainRVAdapter(getBaseContext(), itemBeen);
        recyclerView.setAdapter(mainRVAdapter);
        mainRVAdapter.setOnItemClickListener(new MainRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, CirCleProgressActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, ImageProgressActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, LinearProgressActivity.class));
                        break;
                }
            }
        });
    }

}
