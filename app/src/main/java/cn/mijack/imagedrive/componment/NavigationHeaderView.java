package cn.mijack.imagedrive.componment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.mijack.imagedrive.R;
import cn.mijack.imagedrive.ui.LoginActivity;
import cn.mijack.imagedrive.ui.MainActivity;
import cn.mijack.imagedrive.ui.ProfileActivity;
import cn.mijack.imagedrive.util.Utils;

import static cn.mijack.imagedrive.ui.MainActivity.REQUEST_CODE_PROFILE;

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
        if (currentUser == null || Utils.Companion.isEmpty(currentUser.getPhotoUrl())) {
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
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN);
                } else {
                    startProfileActivity();
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

    public void startProfileActivity() {
        Intent intent = new Intent(activity, ProfileActivity.class);
        ActivityOptionsCompat activityOptions =ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair<>(profileView,"profile"));

        ActivityCompat.startActivityForResult(activity,intent,REQUEST_CODE_PROFILE,activityOptions.toBundle());
    }
}
