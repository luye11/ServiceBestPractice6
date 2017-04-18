package com.example.servicebestpractice6;

/**
 * Created by Administrator on 2017/4/17.
 */

public interface DownloadListener {

    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();

}
