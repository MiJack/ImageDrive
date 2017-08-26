package cn.mijack.imagedrive.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cn.mijack.imagedrive.BuildConfig
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.util.Utils

/**
 * @author admin
 * @date 2017/8/26
 */
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun supportMe(view: View) {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        if (!Utils.isIntentAvailable(this, intent)) {
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        }
        startActivity(intent)
    }


    fun emailMe(view: View) {
        var intent = Intent()
        intent.action = Intent.ACTION_SENDTO
        intent.data = Uri.parse("mailto:mijackstudio@gmail.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_intent))
        if (Utils.Companion.isIntentAvailable(this, intent)) {
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.email_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    fun seeCode(view: View) {
        var intent = Intent()
        intent.data = Uri.parse("https://github.com/MiJack/ImageDrive")
        intent.action = Intent.ACTION_VIEW
        startActivity(intent)
    }
}