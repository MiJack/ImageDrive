package com.mijack.studyjams.componment;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.mijack.studyjams.R;
import com.mijack.studyjams.ui.AccountActivity;
import com.mijack.studyjams.ui.MainActivity;
import com.mijack.studyjams.ui.ProfileActivity;

/**
 * @author Mr.Yuan
 * @date 2017/4/18
 */
public class NavigationHeaderView implements View.OnClickListener {
    ImageView profileView;
    Activity activity;
    FirebaseAuth firebaseAuth;

    public NavigationHeaderView(Activity activity, NavigationView navigationView) {
        firebaseAuth = FirebaseAuth.getInstance();
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
                    Intent intent = new Intent(activity, AccountActivity.class);
                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                } else {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                }
                break;
        }

    }

    public boolean isLogin() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
