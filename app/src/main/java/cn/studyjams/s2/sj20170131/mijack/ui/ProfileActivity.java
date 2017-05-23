package cn.studyjams.s2.sj20170131.mijack.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.util.TextHelper;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";
    private static final int REQUEST_PICK_IMAGE = 1;
    private FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private TextView titleInfo;
    private CircleImageView circleImageView;
    private Button selectAvatar;
    private TextInputLayout editNickName;
    private TextInputLayout editEmail;
    private CoordinatorLayout coordinatorLayout;
    private MaterialDialog dialog;

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
        ViewCompat.setTransitionName(circleImageView, "profile");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        editNickName.getEditText().setText(user.getDisplayName());
        editEmail.getEditText().setText(user.getEmail());
        System.out.println("photo uri:" + user.getPhotoUrl());
        if (!Utils.isEmpty(user.getPhotoUrl())) {
            Glide.with(this).load(user.getPhotoUrl()).into(circleImageView);
        } else {
            circleImageView.setImageResource(R.drawable.ic_empty_profile);
        }

        selectAvatar.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
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
                        .addOnFailureListener(this, result -> Snackbar.make(coordinatorLayout, R.string.settings_failure, Snackbar.LENGTH_SHORT).show())
                        .addOnSuccessListener(this, result -> Snackbar.make(coordinatorLayout, R.string.settings_success, Snackbar.LENGTH_SHORT).show())
                        .addOnCompleteListener(this, result -> editNickName.setEnabled(true));
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.selectAvatar || v.getId() == R.id.circleImageView) {
            gotoPickImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            //todo
            Uri imageUri = data.getData();
            dialog = new MaterialDialog.Builder(this).title(R.string.setting_avatar)
                    .progress(true, 100).build();
            dialog.show();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String uid = user.getUid();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReference().child("avatars").child(uid);
            reference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl)
                        .build();
                firebaseAuth1.getCurrentUser().updateProfile(request).addOnSuccessListener(aVoid -> {
                    Snackbar.make(coordinatorLayout, R.string.setting_avatar_success, Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    Glide.with(ProfileActivity.this).load(firebaseUser.getPhotoUrl()).into(circleImageView);
                }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.setting_avatar_failure, Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.upload_avatar_failure, Toast.LENGTH_SHORT).show());
        }
    }

    private void gotoPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }
}
