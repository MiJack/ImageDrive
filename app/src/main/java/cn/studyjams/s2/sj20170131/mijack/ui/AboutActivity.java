package cn.studyjams.s2.sj20170131.mijack.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.util.Util;

import cn.studyjams.s2.sj20170131.mijack.BuildConfig;
import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void supportMe(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
        if (!Utils.isIntentAvailable(this, intent)) {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
        }
        startActivity(intent);
    }

    public void emailMe(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:mijackstudio@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_email_intent));
        //intent.putExtra(Intent.EXTRA_TEXT, "Hi,");
        if (Utils.isIntentAvailable(this, intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.email_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    public void seeCode(View view) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("https://github.com/MiJack/ImageDrive"));
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }
}
