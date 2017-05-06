package cn.studyjams.s2.sj20170131.mijack;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class ImageDriveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Stetho.initializeWithDefaults(this);
    }
}
