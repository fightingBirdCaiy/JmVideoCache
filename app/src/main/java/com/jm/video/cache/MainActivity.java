package com.jm.video.cache;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String TAG = "ProxyCacheDemo";

    //    private static final String VIDEO_ORIGIN_URL = "https://raw.githubusercontent.com/danikula/AndroidVideoCache/master/files/origin1.mp4";
    private static final String VIDEO_ORIGIN_URL = "http://183.6.222.173/69754DA04863B730407B36287/03000C02005A2A768A9706082D30032E3F156C-3FD0-E2AA-A238-21170B6E84D3.mp4?ccode=03020101&duration=136&expire=18000&psid=a56c8541a657810e8e3fbf91cde9160c&ups_client_netip=6e58f7e5&ups_ts=1519631090&ups_userid=&utid=v6gaE9qVW3wCAXgohSaRkCJS&vid=XMzIxNTQ2Mjg0MA%3D%3D&vkey=Aa75c27d1c41d53b255f5053cc941bf85"
            + "";
    //    private static final String VIDEO_ORIGIN_URL = "http://jmvideo2.jumei.com/MQ_E_E/MTUxODIyNjYxMTQwMw_E_E/MzEwMjc2MA_E_E/L2hvbWUvd3d3L2xvZ3MvdmlkZW8vZmlsZV84OTkyMTMtNjVjYTg4M2NlMmFmN2NjYTBkZGViNzBmODU1YzIxZDQvdmlkZW8ubXA0_default.mp4";

    private Button mStartButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mStartButton = (Button)findViewById(R.id.start_btn);
        mCancelButton = (Button)findViewById(R.id.cancel_btn);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = VideoCacheManager.getInstance(MainActivity.this).getProxyUrl(VIDEO_ORIGIN_URL);
                Log.i(TAG,String.format("开始加载视频，url=%s",url));
                VideoPreloadManager.getInstance(MainActivity.this).preloadVideo(url);
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLoadMethod1();
//                stopLoadMethod2();

            }
        });
    }

    private void stopLoadMethod2() {
        //停止方法2
        String url = VideoCacheManager.getInstance(MainActivity.this).getProxyUrl(VIDEO_ORIGIN_URL);
        VideoPreloadManager.getInstance(MainActivity.this).cancelPreLoadVideo(url);
    }

    private void stopLoadMethod1() {
        //停止方法1
        VideoCacheManager.getInstance(MainActivity.this).stopCacheAndShutdown(VIDEO_ORIGIN_URL);
    }
}
