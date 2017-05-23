package cn.studyjams.s2.sj20170131.mijack.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.adapter.AttributeAdapter;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.core.MediaManager;
import cn.studyjams.s2.sj20170131.mijack.entity.Attribute;
import cn.studyjams.s2.sj20170131.mijack.entity.FirebaseImage;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/26
 */
public class ImageDisplayActivity extends BaseActivity {
    public static final String IMAGE = "image";
    private static final String TAG = "ImageDisplayActivity";
    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String TYPE = "type";
    public static final String LOCAL_FILE = "localFile";
    public static final String FIREBASE_STORAGE = "firebaseStorage";
    private static final String DATABASE_REFERENCE_URL = "database_reference_url";
    private Image image;
    private ImageView imageView;
    private ImageView iconShare;
    private ImageView iconUpload;
    private ImageView iconDelete;
    private ImageView iconInfo;
    private List<ImageView> icons = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private MaterialDialog dialog;
    private StorageTask<UploadTask.TaskSnapshot> storageTask;
    private String cloudFileName;
    private String type;
    private FirebaseImage firebaseImage;
    private ImageView iconDownload;
    private FirebaseStorage firebaseStorage;
    private MaterialDialog downloadDialog;
    private DatabaseReference databaseReference;

    public static void showLocalImage(Context context, Image image, Bundle bundle) {
        Intent intent = new Intent(context, ImageDisplayActivity.class)
                .putExtra(IMAGE, image)
                .putExtra(TYPE, LOCAL_FILE);
        ActivityCompat.startActivity(context,intent,bundle);
    }

    public static void showFirebaseImage(Context context, FirebaseImage firebaseImage, String databaseReference, Bundle bundle) {
        Intent intent = new Intent(context, ImageDisplayActivity.class)
                .putExtra(DOWNLOAD_URL, firebaseImage)
                .putExtra(DATABASE_REFERENCE_URL, databaseReference)
                .putExtra(TYPE, FIREBASE_STORAGE);
        ActivityCompat.startActivity(context,intent,bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseStorage = FirebaseStorage.getInstance();
        imageView = (ImageView) findViewById(R.id.imageView);
        iconShare = (ImageView) findViewById(R.id.iconShare);
        iconUpload = (ImageView) findViewById(R.id.iconUpload);
        iconDownload = (ImageView) findViewById(R.id.iconDownload);
        iconDelete = (ImageView) findViewById(R.id.iconDelete);
        iconInfo = (ImageView) findViewById(R.id.iconInfo);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(TYPE)) {
            return;
        }
        type = intent.getStringExtra(TYPE);
        if (LOCAL_FILE.equals(type) && intent.hasExtra(IMAGE)) {
            image = intent.getParcelableExtra(IMAGE);
            Log.d(TAG, "onCreate: image:" + image.getPath());
            Glide.with(imageView.getContext())
                    .load(image.getPath())
//                    .placeholder(R.drawable.ic_picture_filled)
                    .into(imageView);
        } else if (FIREBASE_STORAGE.equals(type) && intent.hasExtra(DOWNLOAD_URL)) {
            firebaseImage = intent.getParcelableExtra(DOWNLOAD_URL);
            String databaseReferenceUrl = intent.getStringExtra(DATABASE_REFERENCE_URL);
            databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(databaseReferenceUrl);
            String url = firebaseImage.getDownloadUrl();
            Log.d(TAG, "onCreate: url:" + url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        } else {
            return;
        }
        ViewCompat.setTransitionName(imageView,"image");
        icons.add(iconShare);
        icons.add(iconUpload);
        icons.add(iconDelete);
        icons.add(iconInfo);
        icons.add(iconDownload);
        for (int i = 0; i < icons.size(); i++) {
            icons.get(i).setOnClickListener((LOCAL_FILE.equals(type)) ? this::handleLocalFile : this::handleFirebaseFile);
        }
        showIcons(true);
    }

    public void handleFirebaseFile(View v) {
        switch (v.getId()) {
            case R.id.iconShare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse(firebaseImage.getDownloadUrl()));
                intent.setType(firebaseImage.getMiniType());
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                break;
            case R.id.iconDownload:
                File file = new File(firebaseImage.getLocalPath());
                String content = file.exists() ? getString(R.string.file_exist_and_cover) : getString(R.string.download_file_to_local);
                new MaterialDialog.Builder(this)
                        .title(R.string.download)
                        .content(content)
                        .autoDismiss(false)
                        .positiveText(R.string.ok)
                        .onPositive((materialDialog, dialogAction) -> {
                            materialDialog.dismiss();
                            downloadImage();
                        })
                        .negativeText(R.string.cancel)
                        .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                        .show();
                break;
            case R.id.iconDelete:
                DialogInterface.OnClickListener dialogInterface = (DialogInterface dialog, int which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteFirebaseFile();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        //nothing
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.are_you_sure_to_delete)
                        .setPositiveButton(R.string.ok, dialogInterface)
                        .setNegativeButton(R.string.cancel, dialogInterface)
                        .create().show();
                break;
            case R.id.iconInfo:
                showFireBaseImageInfo();
                break;
        }
    }

    private void deleteFirebaseFile() {
        StorageReference reference = firebaseStorage.getReference().child(firebaseImage.getFsUrl());
        reference.delete().addOnFailureListener(e -> Toast.makeText(ImageDisplayActivity.this, R.string.delete_failure, Toast.LENGTH_SHORT).show()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ImageDisplayActivity.this, R.string.delete_seccess, Toast.LENGTH_SHORT).show();
                databaseReference.removeValue();
                finish();
            }
        });
    }

    private void showFireBaseImageInfo() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_image_info, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        AttributeAdapter adapter = new AttributeAdapter();
        List<Attribute<String>> list = new ArrayList<>();
        list.add(new Attribute<String>(getString(R.string.file_name), firebaseImage.getName()));
        list.add(new Attribute<String>(getString(R.string.upload_device), firebaseImage.getDevice()));
        list.add(new Attribute<String>(getString(R.string.upload_device_id), firebaseImage.getDeviceId()));
        list.add(new Attribute<String>(getString(R.string.download_link), firebaseImage.getDownloadUrl()));
        list.add(new Attribute<String>(getString(R.string.resolution), firebaseImage.getWidth() + "*" + firebaseImage.getHeight()));
        list.add(new Attribute<String>(getString(R.string.local_path_before), firebaseImage.getLocalPath()));
        list.add(new Attribute<String>(getString(R.string.create_time), Utils.formatTime(firebaseImage.getDateTaken())));
        list.add(new Attribute<String>(getString(R.string.upload_time), Utils.formatTime(firebaseImage.getUploadTime())));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setList(list);
        recyclerView.setAdapter(adapter);
        dialog.setContentView(view);
        dialog.show();
    }

    private void showIcons(boolean show) {
        for (int i = 0; i < icons.size(); i++) {
            ImageView imageView = icons.get(i);
            switch (imageView.getId()) {
                case R.id.iconDownload:
                    imageView.setVisibility((show && FIREBASE_STORAGE.equals(type)) ? View.VISIBLE : View.GONE);
                    break;
                case R.id.iconUpload:
                    imageView.setVisibility((show && LOCAL_FILE.equals(type)) ? View.VISIBLE : View.GONE);
                    break;
                default:
                    imageView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void handleLocalFile(View v) {
        switch (v.getId()) {
            case R.id.iconShare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse(image.getPath()));
                intent.setType(image.getMiniType());
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
                break;
            case R.id.iconDownload:
                new MaterialDialog.Builder(this)
                        .title(R.string.download)
                        .content(R.string.download_file_to_local)
                        .autoDismiss(false)
                        .positiveText(R.string.ok)
                        .onPositive((materialDialog, dialogAction) -> {
                            materialDialog.dismiss();
                            downloadImage();
                        })
                        .negativeText(R.string.cancel)
                        .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                        .show();
                break;
            case R.id.iconUpload:
                new MaterialDialog.Builder(this)
                        .title(R.string.upload)
                        .content(R.string.upload_file)
                        .autoDismiss(false)
                        .positiveText(R.string.ok)
                        .onPositive((materialDialog, dialogAction) -> {
                            uploadImage(image);
                            materialDialog.dismiss();
                        })
                        .negativeText(R.string.cancel)
                        .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                        .show();
                break;
            case R.id.iconDelete:
                DialogInterface.OnClickListener dialogInterface = (DialogInterface dialog, int which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (MediaManager.deleteFile(image.getPath())) {
                            Toast.makeText(this, R.string.delete_seccess, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, R.string.delete_failure, Toast.LENGTH_SHORT).show();
                        }
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        //nothing
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.are_you_sure_to_delete)
                        .setPositiveButton(R.string.ok, dialogInterface)
                        .setNegativeButton(R.string.cancel, dialogInterface)
                        .create().show();
                break;
            case R.id.iconInfo:
                BottomSheetDialog dialog = new BottomSheetDialog(this);
                View view = LayoutInflater.from(this).inflate(R.layout.layout_image_info, null);
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                AttributeAdapter adapter = new AttributeAdapter();
                List<Attribute<String>> list = new ArrayList<>();
                list.add(new Attribute(getString(R.string.file_name), image.getName()));
                list.add(new Attribute(getString(R.string.resolution), image.getWidth() + "*" + image.getHeight()));
                list.add(new Attribute(getString(R.string.local_path), image.getPath()));
                list.add(new Attribute(getString(R.string.file_size), image.getSize() + "KB"));
                list.add(new Attribute(getString(R.string.create_time), Utils.formatTime(image.getDateTaken())));
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter.setList(list);
                recyclerView.setAdapter(adapter);
                dialog.setContentView(view);
                dialog.show();
                break;
        }
    }

    private void downloadImage() {
        String localPath = firebaseImage.getLocalPath();
        StorageReference reference = firebaseStorage.getReference().child(firebaseImage.getFsUrl());
        if (downloadDialog == null) {
            downloadDialog = new MaterialDialog.Builder(this)
                    .title(R.string.download)
                    .cancelable(false)
                    .progress(false, 100, true)
                    .build();
        }
        downloadDialog.show();
        reference.getFile(new File(localPath))
                .addOnProgressListener(taskSnapshot -> downloadDialog.setProgress((int) (taskSnapshot.getBytesTransferred() * 100 / taskSnapshot.getTotalByteCount())))
                .addOnSuccessListener(taskSnapshot -> {
                    downloadDialog.cancel();
                    Snackbar.make(coordinatorLayout, R.string.download_file_success, Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    downloadDialog.cancel();
                    Snackbar.make(coordinatorLayout, R.string.download_file_failure, Snackbar.LENGTH_SHORT).show();
                });
    }

    private void uploadImage(Image image) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            new MaterialDialog.Builder(this)
                    .title(R.string.login_please).content(R.string.upload_after_login)
                    .cancelable(false)
                    .negativeText(R.string.ok)
                    .onNegative((materialDialog, dialogAction) -> {
                        materialDialog.dismiss();
                    }).build().show();
            return;
        }
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        File file = new File(image.getPath());
        String md5 = Utils.fileMD5(file);
        String fileExtensionName = Utils.fileExtensionName(file);
        String device = Build.DEVICE;
        cloudFileName = Utils.base64Encode(device + "-" + image.getPath() + "-" + md5) + fileExtensionName;
        System.out.println(cloudFileName);
        StorageReference reference = firebaseStorage.getReference()
                .child("image").child(firebaseAuth.getCurrentUser().getUid());
        dialog = new MaterialDialog.Builder(this)
                .title(R.string.upload)
                .cancelable(false)
                .progress(false, 100, true)
                .negativeText(R.string.cancel)
                .onNegative((materialDialog, dialogAction) -> {
                    if (storageTask != null) {
                        storageTask.cancel();
                    }
                }).build();
        dialog.show();
        storageTask = reference.child(cloudFileName)
                .putFile(Uri.fromFile(file))
                .addOnProgressListener(this,
                        taskSnapshot -> {
                            long totalByteCount = taskSnapshot.getTotalByteCount();
                            long bytesTransferred = taskSnapshot.getBytesTransferred();
                            dialog.setProgress((int) (100 * bytesTransferred / totalByteCount));
                        }
                )
                .addOnSuccessListener(this, taskSnapshot -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Snackbar.make(coordinatorLayout, R.string.upload_success, Snackbar.LENGTH_SHORT).show();
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    Pattern pattern = Pattern.compile("^image/([^/]+)(?:/.*)$");
                    StorageMetadata metadata = taskSnapshot.getMetadata();
                    String fsUrl = metadata.getPath();
                    Matcher matcher = pattern.matcher(metadata.getPath());
                    if (matcher.matches()) {
                        String uid = matcher.group(1);
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference reference1 = firebaseDatabase.getReference("images").child("users").child(uid);
                        FirebaseImage fsImage = new FirebaseImage(image, downloadUrl, fsUrl);
                        DatabaseReference push = reference1.push();
                        push.updateChildren(fsImage.toMap());
                    }

                })
                .addOnFailureListener(this, e -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Snackbar.make(coordinatorLayout, R.string.upload_failure, Snackbar.LENGTH_SHORT).show();
                })
                .addOnPausedListener(this, taskSnapshot -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                });
    }

}
