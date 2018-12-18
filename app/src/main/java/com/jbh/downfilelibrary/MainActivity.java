package com.jbh.downfilelibrary;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * 库的作者 地址
 * github地址:
 * https://github.com/lingochamp/FileDownloader
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*1  初始化*/
        FileDownloader.setup(this);
        /*单任务下载*/
        downFile1();
        /*多任务下载*/
        downFile2();
    }




    /*单任务下载*/
    private void downFile1() {
        /*2  开始下载*/
        BaseDownloadTask down = FileDownloader.getImpl().create("")
                .setPath("", true)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                //.setTag()
                .setListener(new FileDownloadLargeFileListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                });
        down.start();
    }



    /*多任务下载*/
    private void downFile2() {
        /*下载监听*/
        FileDownloadSampleListener listener=new FileDownloadSampleListener(){

            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","pending taskId:"+task.getId()+",fileName:"+task.getFilename()+",soFarBytes:"+soFarBytes+",totalBytes:"+totalBytes+",percent:"+soFarBytes*1.0/totalBytes);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","progress taskId:"+task.getId()+",fileName:"+task.getFilename()+",soFarBytes:"+soFarBytes+",totalBytes:"+totalBytes+",percent:"+soFarBytes*1.0/totalBytes+",speed:"+task.getSpeed());
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","blockComplete taskId:"+task.getId()+",filePath:"+task.getPath()+",fileName:"+task.getFilename()+",speed:"+task.getSpeed()+",isReuse:"+task.reuse());
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","completed taskId:"+task.getId()+",isReuse:"+task.reuse());
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","paused taskId:"+task.getId()+",soFarBytes:"+soFarBytes+",totalBytes:"+totalBytes+",percent:"+soFarBytes*1.0/totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","error taskId:"+task.getId()+",e:"+e.getLocalizedMessage());
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if(task.getListener() != this){
                    return;
                }
                Log.d("feifei","warn taskId:"+task.getId());
            }
        };

        //(1) 创建 FileDownloadQueueSet
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(listener);
        //(2) 创建Task 队列
        final List<BaseDownloadTask> tasks = new ArrayList<>();

        /*下载任务*/
        String fileUrl1 = "";
        String fileUrl2 = "";
        String mSaveFolder=Environment.getExternalStorageDirectory().getAbsolutePath();
        BaseDownloadTask task1 = FileDownloader.getImpl().create(fileUrl1).setPath(mSaveFolder,true);
        tasks.add(task1);
        BaseDownloadTask task2 = FileDownloader.getImpl().create(fileUrl2).setPath(mSaveFolder,true);
        tasks.add(task2);

        //(3) 设置参数
        // 每个任务的进度 无回调
        //queueSet.disableCallbackProgressTimes();
        // do not want each task's download progress's callback,we just consider which task will completed.
        queueSet.setCallbackProgressTimes(100);
        queueSet.setCallbackProgressMinInterval(100);
        //失败 重试次数
        queueSet.setAutoRetryTimes(3);
        //避免掉帧
        FileDownloader.enableAvoidDropFrame();
        //(4)串行下载
        queueSet.downloadSequentially(tasks);
        //(5)任务启动
        queueSet.start();
    }
}
