package com.jm.video.cache;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * Created by yongc on 2018/2/24.
 */

public class VideoCacheManager implements CacheListener {

    private static final String TAG = "VCM";

    private static VideoCacheManager mInstance;

    private HttpProxyCacheServer mCacheServer;

    public static VideoCacheManager getInstance(Context context){
        Context applicationContext = context.getApplicationContext();
        if(null == mInstance){
            synchronized(VideoCacheManager.class){
                if(null == mInstance){
                    mInstance = new VideoCacheManager(applicationContext);
                }
            }
        }
        return mInstance;
    }

    private VideoCacheManager(Context context){
        File cacheDir = getCacheDirectory(context);
        HttpProxyCacheServer.Builder builder = new HttpProxyCacheServer.Builder(context)
                .maxCacheFilesCount(20).cacheDirectory(cacheDir);
        mCacheServer = builder.build();
    }

    private File getCacheDirectory(Context context) {
        File cacheParentDir = getCacheParentDirectory(context);
        File cacheDir = new File(cacheParentDir,"video_cache");
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private File getCacheParentDirectory(Context context) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Log.i(TAG,"Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        String pathPrix = Environment.getExternalStorageDirectory() + "/";
        File file = new File(pathPrix + "caiy");
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public String getProxyUrl(String url) {
        String result = mCacheServer.getProxyUrl(url);
        boolean isCached = mCacheServer.isCached(url);
        String msg = String.format("getProxyUrl:isCached=%b,url=%s,proxyUrl=%s",isCached,url,result);
        Log.i(TAG,msg);
        return result;
    }

    public void registerCacheListener(String url){
        mCacheServer.registerCacheListener(this,url);
    }

    public void unregisterCacheListener(){
        mCacheServer.unregisterCacheListener(this);
    }

    /**
     * 停止缓存视频文件并关闭
     * 自定义方法,原项目地址没有这个方法
     * @param url 服务器返回的原始url
     */
    public void stopCacheAndShutdown(String url){
        mCacheServer.stopCacheAndShutdown(url);//自定义方法,原项目地址没有这个方法
    }

    public void shutdown(){
        mCacheServer.shutdown();
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if(cacheFile != null && percentsAvailable == 100) {
            String msg = String.format("onCacheAvailable:cacheFile=%s,url=%s,percentsAvailable=%d",
                    cacheFile.getAbsolutePath(),url,percentsAvailable);
            Log.i(TAG,msg);
        }
    }
}
