package cn.mijack.imagedrive.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity

/**
 * @author admin
 * @date 2017/8/26
 */

class SplashActivity : BaseActivity() {
    private val CODE_REQUEST_STORAGE_PERMISSION: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var decorView = window.decorView
        var option: Int = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.systemUiVisibility = option
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //无权限
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CODE_REQUEST_STORAGE_PERMISSION)
        } else {
            startMainActivity()
        }
    }

    fun startMainActivity() {
        Handler().postDelayed({
            var intent: Intent = Intent(SplashActivity@ this,  MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000L)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CODE_REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMainActivity()
            } else {
                //提示开启权限
                Toast.makeText(this, R.string.permission_tip, Toast.LENGTH_SHORT).show()
            }
        }
    }
}