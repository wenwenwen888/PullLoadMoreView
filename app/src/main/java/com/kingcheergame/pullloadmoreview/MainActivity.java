package com.kingcheergame.pullloadmoreview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kingcheergame.library.PullLoadMoreView;

public class MainActivity extends AppCompatActivity {

    private PullLoadMoreView pullLoadMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullLoadMoreView = findViewById(R.id.pullLoadMoreView);

        //添加头部布局
        pullLoadMoreView.addHeadView(R.layout.top_layout);
        //添加监听open/close
        pullLoadMoreView.setViewStateListener(new PullLoadMoreView.ViewStateListener() {
            @Override
            public void onViewState(PullLoadMoreView.VIewState viewState) {
                if (viewState == PullLoadMoreView.VIewState.OPEN) {
                    Toast.makeText(MainActivity.this, "Open", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
