package cn.mijack.imagedrive.base

import android.app.ActivityManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import cn.mijack.imagedrive.R

/**
 * @author admin
 * @date 2017/8/26
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var appName = getString(R.string.app_name)
            var icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon)
            var color = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            setTaskDescription(ActivityManager.TaskDescription(appName, icon, color))
        }

    }
}