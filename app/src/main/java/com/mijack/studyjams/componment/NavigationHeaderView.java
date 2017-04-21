package com.mijack.studyjams.componment;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;

import com.mijack.studyjams.R;
import com.mijack.studyjams.ui.LoginActivity;
import com.mijack.studyjams.ui.MainActivity;

/**
 * @author Mr.Yuan
 * @date 2017/4/18
 */
public class NavigationHeaderView implements View.OnClickListener {
    ImageView profileView;
    Activity activity;

    public NavigationHeaderView(Activity activity, NavigationView navigationView) {
        this.activity = activity;
        if (navigationView.getHeaderCount() == 0) {
            throw new IllegalArgumentException("NavigationView has headView");
        }
        profileView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        profileView.setOnClickListener(this);
    }

    public void loadLoginInfo() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                //
                if (!isLogin()) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                } else {

                }
                break;
        }

    }

    public boolean isLogin() {
        return false;
    }
}
