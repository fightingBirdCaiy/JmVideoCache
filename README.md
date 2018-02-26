# JmVideoCache

短视频本地缓存下载优化（只下载当前需要播放的视频，取消划出屏幕的视频的下载）

## 1.使用场景

类似抖音播放小视频功能:用户快速下滑recyclerview中的视频view，当上一个视频view划出屏幕的时候调用stopCacheAndShutdown方法即可取消上一个视频的下载。
从而减少网络请求竞争（网速较慢的时候优先下载当前需要播放的视频，已划出屏幕的视频需要取消下载）。

## 2.基于https://github.com/danikula/AndroidVideoCache 2.7.0版本

## 3.HttpProxyCacheServer类中增加方法stopCacheAndShutdown

```java
/**
 * 停止缓存视频文件并关闭
 * 注:自定义方法,原项目地址没有这个方法
 * added by yongc
 * @param url 原始的url，非本地代理后的url
 */
public void stopCacheAndShutdown(String url){
    synchronized (clientsLock) {
        HttpProxyCacheServerClients clients = clientsMap.get(url);
        if (clients != null) {
            clients.shutdown();
        }
    }
}
```