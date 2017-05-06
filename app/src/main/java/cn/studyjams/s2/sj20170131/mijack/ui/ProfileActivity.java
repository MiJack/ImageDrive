package cn.studyjams.s2.sj20170131.mijack.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.util.TextHelper;
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
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleInfo = (TextView) findViewById(R.id.titleInfo);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                editNickName.setEnabled(false);
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(TextHelper.getText(editNickName))
                        .build();
                firebaseAuth.getCurrentUser().updateProfile(request)
                        .addOnFailureListener(this, result -> Snackbar.make(coordinatorLayout, "修改失败", Snackbar.LENGTH_SHORT).show())
                        .addOnSuccessListener(this, result -> Snackbar.make(coordinatorLayout, "修改成功", Snackbar.LENGTH_SHORT).show())
                        .addOnCompleteListener(this, result -> editNickName.setEnabled(true));
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
