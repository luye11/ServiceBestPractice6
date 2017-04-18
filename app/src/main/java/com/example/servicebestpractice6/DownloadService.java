package com.example.servicebestpractice6;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;
public class DownloadService extends Service {

    private DownloadTask downloadTask;
    private String downloadUrl;

    private DownloadListener listener=new DownloadListener() {

        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Downlaod Success", 0));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Downlaod Failed", 0));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask=null;
            getNotificationManager().notify(1, getNotification("Downlaod Paused", 0));
        }

        @Override
        public void onCanceled() {
            downloadTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }
    };

    private Notification getNotification(String s, int progress) {
        Intent intent = new Intent(DownloadService.this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(s);
        if (progress >= 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    public DownloadService() {
    }

    private DownloadBinder mBinder=new DownloadBinder();

    class DownloadBinder extends Binder {

        public void startDownload(String url){
            if (downloadTask == null) {
                downloadUrl=url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading...",0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pausedDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void canceledDownload() {
            if (downloadTask != null) {
                downloadTask.canceledDownload();
            }else{
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(fileName + directory);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Download Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}
