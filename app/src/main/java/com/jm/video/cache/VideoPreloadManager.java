package com.jm.video.cache;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by yongc on 2018/2/24.
 */

public class VideoPreloadManager{

    public static final String TAG = "VCM4Preload";

    private static VideoPreloadManager mInstance;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(8);

    private Map<String,WeakReference<VideoPreLoadRunnable>>
            mRunnableMap = new HashMap<String,WeakReference<VideoPreLoadRunnable>>();

    private Context mContext;

    public static VideoPreloadManager getInstance(Context context){
        Context applicationContext = context.getApplicationContext();
        if(null == mInstance){
            synchronized(VideoPreloadManager.class){
                if(null == mInstance){
                    mInstance = new VideoPreloadManager(applicationContext);
                }
            }
        }
        return mInstance;
    }


    private VideoPreloadManager(Context context) {
        mContext = context;
    }

    /**
     * 预加载url对应的视频文件
     * @param url
     */
    public void preloadVideo(String url){
        if(!TextUtils.isEmpty(url)){
            if(url.startsWith("http")){
                Log.i(TAG, String.format("preloadVideo,网络文件,开始下载:ulr=%s",url));
                VideoPreLoadRunnable runnable = new VideoPreLoadRunnable(url);
                mRunnableMap.put(url,new WeakReference<VideoPreLoadRunnable>(runnable));
                mExecutorService.execute(runnable);
            }else{
                Log.i(TAG, String.format("preloadVideo,本地文件:ulr=%s",url));
            }
        }
    }

    /**
     * 取消预加载url对应的视频文件
     * @param url
     */
    public void cancelPreLoadVideo(String url){
        Log.i(TAG, String.format("cancelPreLoadVideo,ulr=%s",url));
        if(mRunnableMap != null && !TextUtils.isEmpty(url)){
            WeakReference<VideoPreLoadRunnable> runnableWeakReference = mRunnableMap.get(url);
            VideoPreLoadRunnable runnable = null;
            if(runnableWeakReference != null && (runnable = runnableWeakReference.get()) != null){
                Log.i(TAG, String.format("cancelPreLoadVideo,取消网络请求,ulr=%s",url));
                runnable.setCanceled(true);
            }
        }

    }

    private static class VideoPreLoadRunnable implements Runnable {

        private String mUrl;
        private AtomicBoolean isCanceled = new AtomicBoolean(false);

        public VideoPreLoadRunnable(String url){
            this.mUrl = url;
        }

        public void setCanceled(boolean cancel) {
            isCanceled.set(cancel);
        }

        @Override
        public void run() {
            if(isCanceled.get()){//如果已经取消，则直接返回
                Log.i(TAG, String.format("cancelPreLoadVideo,取消网络请求,成功(未下载数据),url=%s",mUrl));
                return;
            }
            doRequestWithoutReturn(mUrl);
        }

        private void doRequestWithoutReturn(String getURL){

            InputStream inStream = null;
            HttpURLConnection connection = null;
            try {

                URL url = new URL(getURL);

                //                StringBuffer sBuffer = new StringBuffer();

                connection = (HttpURLConnection)url.openConnection();

                connection.setConnectTimeout(1000);

                connection.setReadTimeout(1000);

                connection.connect();

                byte[] buf = new byte[1024];

                inStream = connection.getInputStream();

                for (int n; (n = inStream.read(buf)) != -1;) {

                    //                    sBuffer.append(new String(buf, 0, n, "UTF-8"));
                    if(isCanceled.get()){
                        Log.i(TAG, String.format("cancelPreLoadVideo,取消网络请求,成功(正在下载数据),url=%s",mUrl));
                        break;
                    }
                }
                Log.i(TAG, String.format("preloadVideo,网络文件,成功,url=%s",mUrl));
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, String.format("preloadVideo,网络文件,失败,msg=%s,url=%s",e.getMessage(),mUrl));
            }finally{
                try{
                    if(inStream != null){
                        inStream.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    if(connection != null){
                        connection.disconnect();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
