package cn.mijack.imagedrive

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp

/**
 * @author admin
 * @date 2017/8/26
 */
class ImageDriveApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        FirebaseApp.initializeApp(this)
    }
}