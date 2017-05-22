package cn.studyjams.s2.sj20170131.mijack.componment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.ui.AccountActivity;
import cn.studyjams.s2.sj20170131.mijack.ui.MainActivity;
import cn.studyjams.s2.sj20170131.mijack.ui.ProfileActivity;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/18
 */
public class NavigationHeaderView implements View.OnClickListener, FirebaseAuth.AuthStateListener {
    ImageView profileView;
    MainActivity activity;
    FirebaseAuth firebaseAuth;
    private NavigationView navigationView;
    TextView nickName;
    TextView email;

    public NavigationHeaderView(MainActivity activity, NavigationView navigationView) {
        this.navigationView = navigationView;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
        this.activity = activity;
        if (navigationView.getHeaderCount() == 0) {
            throw new IllegalArgumentException("NavigationView has headView");
        }
        View headerView = navigationView.getHeaderView(0);
        profileView = (ImageView) headerView.findViewById(R.id.profile_image);
        nickName = (TextView) headerView.findViewById(R.id.nickName);
        email = (TextView) headerView.findViewById(R.id.email);
        profileView.setOnClickListener(this);
    }

    public void loadLoginInfo() {
        Menu menu = navigationView.getMenu();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        menu.findItem(R.id.actionLogout).setVisible(currentUser != null);
        menu.findItem(R.id.actionProfile).setVisible(currentUser != null);
        menu.findItem(R.id.actionDriver).setVisible(currentUser != null);
        if (currentUser == null) {
            email.setText("");
            nickName.setText("");
        } else {
            email.setText(currentUser.getEmail());
            nickName.setText(currentUser.getDisplayName());
        }
        if (currentUser == null || Utils.isEmpty(currentUser.getPhotoUrl())) {
            profileView.setImageResource(R.drawable.ic_empty_profile);
        } else {
            Glide.with(profileView.getContext()).load(currentUser.getPhotoUrl()).into(profileView);
        }
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
                    activity.startProfileActivity();
                }
                break;
        }

    }

    public boolean isLogin() {
        return firebaseAuth.getCurrentUser() != null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        loadLoginInfo();
    }
}
