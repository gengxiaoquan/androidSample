package com.example.myapplication;

import android.util.Log;
import android.view.Choreographer;
import java.util.concurrent.TimeUnit;


public class PerformanceMonitor implements Choreographer.FrameCallback {

    public static final String TAG = "PerformanceMonitor";
    public static PerformanceMonitor sInstance;
    public static boolean DEBUG = true;
    public static boolean DROP = true;
    public static boolean FLAG = false;
    private long mFrameCount = 0;
    private long mFrameTime = 0;
    private long mGridScrollStartTimeStamp = 0;
    private long dropFrameAll = 0;
    private static final int SKIPPED_FRAME_WARNING_LIMIT = 1;

    PerformanceMonitor() {
    }

    public synchronized static PerformanceMonitor getInstance() {
        if (sInstance == null) {
            sInstance = new PerformanceMonitor();
        }
        return sInstance;
    }

    //帧率相关
    public void ScrollStart() {   //纵向，触发这两个。同上
        Choreographer.getInstance().postFrameCallback(this);      //请求vsync信号, Choreographer周期性的在UI重绘时候触发
        //调用顺序：postFrameCallback-----》postFrameCallbackDelayed----》postCallbackDelayedInternal----》scheduleFrameLocked---》doFrame
    }


    //总帧数/时间 算法。较稳定。会超过60，故限制。首选方法。
    @Override
    public void doFrame(long frameTimeNanos) { //当前帧的时间
        mFrameCount++;   //帧数加1
        //Log.i("PerformanceMonitor", "mFrameCount_is: " + mFrameCount );
        if (mGridScrollStartTimeStamp == 0 && mFrameTime == 0){
            mGridScrollStartTimeStamp = frameTimeNanos;
            mFrameTime = frameTimeNanos;
        }
        else {
            FLAG = true;
        }

        if (isFinishedWithSample(frameTimeNanos) && FLAG) //每800ms收集一次
        {
            long totalTime = frameTimeNanos - mGridScrollStartTimeStamp; //ns
            float frameRate = (float) (mFrameCount * 1000000000.0 / totalTime);
            if (frameRate > 60.0) {
                frameRate = 60;
            }
            mFrameCount = 0;
            mGridScrollStartTimeStamp = frameTimeNanos;
            Log.i("PerformanceMonitor", "frame_rate_is: " + frameRate + " when grid scroll");
        }

        if (DROP && FLAG) {
            int droppedCount = droppedCount(mFrameTime, frameTimeNanos, 16.6f);
            if (droppedCount >= SKIPPED_FRAME_WARNING_LIMIT)
                Log.i("PerformanceMonitor", "drop_frame_num_is: " + droppedCount + " when grid scroll");
        }

        mFrameTime = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }

    /*
    //（理想帧数-掉帧）*mul 算法。不稳定（猜测，对于低端设备，帧间间隔不稳定，此方法结果会偏低且波动大。最大为60）
    @Override
    public void doFrame(long frameTimeNanos) { //当前帧的时间
        if (mGridScrollStartTimeStamp == 0 && mFrameTime == 0){
            mGridScrollStartTimeStamp = frameTimeNanos;
            mFrameTime = frameTimeNanos;
        }
        else {
            FLAG = true;
        }

        if (isFinishedWithSample(frameTimeNanos) && FLAG) //每700ms收集一次
        {
            long totalTime = frameTimeNanos - mGridScrollStartTimeStamp; //ns
            long optimalFrameNum= Math.round(totalTime/16600000.0);
            mFrameCount = optimalFrameNum - dropFrameAll;
            float frameRate = (float) (mFrameCount * 60.0 / optimalFrameNum);
            mGridScrollStartTimeStamp = frameTimeNanos;
            dropFrameAll = 0;
            Log.i("PerformanceMonitor", "frame_rate_is: " + frameRate + " when grid scroll");
        }

        if (DROP && FLAG) {
            int droppedCount = droppedCount(mFrameTime, frameTimeNanos, 16.6f);
            dropFrameAll = dropFrameAll + droppedCount;
            if (droppedCount >= SKIPPED_FRAME_WARNING_LIMIT)
                Log.i("PerformanceMonitor", "drop_frame_num_is: " + droppedCount + " when grid scroll");
        }
        mFrameTime = frameTimeNanos;

        Choreographer.getInstance().postFrameCallback(this);
    }

     */


    public boolean isFinishedWithSample(long frameTimeNanos)
    {
        return frameTimeNanos-mGridScrollStartTimeStamp > 700000000;   //700ms左右
    }

    //Vsync信号延时/16ms，有多少个，就算跳几帧。
    //Vsync信号到了后，重绘并不一定会立刻执行，因为UI线程可能被阻塞再某个地方，比如在Touch事件中，触发了重绘，之后继续执行了一个耗时操作，这个时候，必然会导致Vsync信号被延时执行
    public static int droppedCount(long start, long end, float devRefreshRate){ //计算掉帧数  当前刷新时间和上一次的刷新时间，然后进行除法处理.刷新率16.6ms
        int count = 0;
        long diffNs = end - start; //一个帧的时间

        long diffMs = TimeUnit.MILLISECONDS.convert(diffNs, TimeUnit.NANOSECONDS);
        long dev = Math.round(devRefreshRate);
        if (diffMs > dev) { //一次帧大于17ms
            long droppedCount = (diffMs / dev);
            count = (int) droppedCount; //帧间掉帧数
        }
        return count;
    }
}