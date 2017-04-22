package com.mijack.studyjams.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mijack.studyjams.R;
import com.mijack.studyjams.base.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "ProfileActivity";
    private FirebaseAuth firebaseAuth;

    Toolbar toolbar;
    TextView titleInfo;
    CircleImageView circleImageView;
    Button selectAvatar;
    TextInputLayout editNickName;
    TextInputLayout editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleInfo = (TextView) findViewById(R.id.titleInfo);
        circleImageView = (CircleImageView) findViewById(R.id.circleImageView);
        selectAvatar = (Button) findViewById(R.id.selectAvatar);
        editNickName = (TextInputLayout) findViewById(R.id.editNickName);
        editEmail = (TextInputLayout) findViewById(R.id.editEmail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        editNickName.getEditText().setText(user.getDisplayName());
        editEmail.getEditText().setText(user.getEmail());
        System.out.println("photo uri:" + user.getPhotoUrl());
        if (!TextUtils.isEmpty(user.getPhotoUrl().toString())) {
            Glide.with(this).load(user.getPhotoUrl()).into(circleImageView);
        } else {
            Glide.with(this).load(R.drawable.ic_profile).into(circleImageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
