package com.mijack.studyjams;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class ImageDriveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
