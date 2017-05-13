package cn.studyjams.s2.sj20170131.mijack.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.adapter.ExitAttributeAdapter;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.core.MediaManager;
import cn.studyjams.s2.sj20170131.mijack.database.DataBaseContentProvider;
import cn.studyjams.s2.sj20170131.mijack.database.DatabaseSQLiteOpenHelper;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;
import cn.studyjams.s2.sj20170131.mijack.entity.UploadStatus;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/26
 */
public class ImageDisplayActivity extends BaseActivity implements View.OnClickListener, OnProgressListener<UploadTask.TaskSnapshot>,
        OnSuccessListener<UploadTask.TaskSnapshot>, OnFailureListener, OnPausedListener<UploadTask.TaskSnapshot> {
    Uri uri = Uri.parse("content://" + DataBaseContentProvider.AUTHORITIES + "/" + DatabaseSQLiteOpenHelper.TABLE_NAME);
    public static final String IMAGE = "image";
    private static final String TAG = "ImageDisplayActivity";
    private Image image;
    private ImageView imageView;
    private ImageView iconShare;
    private ImageView iconUpload;
    private ImageView iconDelete;
    private ImageView iconInfo;
    private ImageView[] icons = new ImageView[4];
    private CoordinatorLayout coordinatorLayout;
    private ExifInterface exifInterface;
    private MaterialDialog dialog;
    private StorageTask<UploadTask.TaskSnapshot> storageTask;
    private String cloudFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(IMAGE)) {
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        image = intent.getParcelableExtra(IMAGE);
        Log.d(TAG, "onCreate: image:" + image.getPath());
        exifInterface = MediaManager.getExifInterface(image.getPath());
        imageView = (ImageView) findViewById(R.id.imageView);
        iconShare = (ImageView) findViewById(R.id.iconShare);
        iconUpload = (ImageView) findViewById(R.id.iconUpload);
        iconDelete = (ImageView) findViewById(R.id.iconDelete);
        iconInfo = (ImageView) findViewById(R.id.iconInfo);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        icons[0] = iconShare;
        icons[1] = iconUpload;
        icons[2] = iconDelete;
        icons[3] = iconInfo;
        iconShare.setOnClickListener(this);
        iconUpload.setOnClickListener(this);
        iconDelete.setOnClickListener(this);
        iconInfo.setOnClickListener(this);
        showIcons(true);
        Glide.with(imageView.getContext())
                .load(image.getPath())
                .placeholder(R.drawable.ic_picture_filled)
                .into(imageView);
    }

    private void showIcons(boolean show) {
        for (int i = 0; i < icons.length; i++) {
            icons[i].setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iconShare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse(image.getPath()));
                intent.setType(image.getMiniType());
                startActivity(Intent.createChooser(intent, "Share image using"));
                break;
            case R.id.iconUpload:
                new MaterialDialog.Builder(this)
                        .title("同步")
                        .content("同步该文件到云端？")
                        .autoDismiss(false)
                        .positiveText("确定")
                        .onPositive((materialDialog, dialogAction) -> {
                            uploadImage(image);
                            materialDialog.dismiss();
                        })
                        .negativeText("取消")
                        .onNegative((materialDialog, dialogAction) -> materialDialog.dismiss())
                        .show();
                break;
            case R.id.iconDelete:
                DialogInterface.OnClickListener dialogInterface = (DialogInterface dialog, int which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
//                        MediaManager.deleteFile(image.getPath());
//                        finish();
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        //nothing
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle("删除")
                        .setMessage("你确定要删除吗？")
                        .setPositiveButton("确定", dialogInterface)
                        .setNegativeButton("取消", dialogInterface)
                        .create().show();
                break;
            case R.id.iconInfo:
                BottomSheetDialog dialog = new BottomSheetDialog(this);
                View view = LayoutInflater.from(this).inflate(R.layout.layout_image_info, null);
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                List<String> list = new ArrayList<>();
                for (int i = 0; i < EXIF_INTERFACE_ATTRIBUTES.length; i++) {
                    String attribute = EXIF_INTERFACE_ATTRIBUTES[i];
                    String attributeValue = exifInterface.getAttribute(attribute);
                    if (TextUtils.isEmpty(attributeValue)) {
                        continue;
                    }
                    list.add(attribute + ":" + attributeValue);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new ExitAttributeAdapter(list));
                dialog.setContentView(view);
                dialog.show();
                break;
        }
    }

    private void uploadImage(Image image) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            new MaterialDialog.Builder(this)
                    .title("请登录").content("请登录后再上传文件")
                    .cancelable(false)
                    .negativeText("确定")
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
        StorageMetadata.Builder builder = new StorageMetadata.Builder()
                .setCustomMetadata("localPath", image.getPath())
                .setCustomMetadata("device", Build.DEVICE)
                .setCustomMetadata("deviceId", Build.ID);
        dialog = new MaterialDialog.Builder(this)
                .title("upload")
                .progress(false, 100, true)
                .negativeText("cancel")
                .onNegative((materialDialog, dialogAction) -> {
                    if (storageTask != null) {
                        storageTask.cancel();
                    }
                }).build();
        dialog.show();
        storageTask = reference.child(cloudFileName)
                .putFile(Uri.fromFile(file), builder.build())
                .addOnProgressListener(this, this)
                .addOnSuccessListener(this, this)
                .addOnFailureListener(this, this)
                .addOnPausedListener(this, this);
        ContentResolver contentResolver = getContentResolver();
        //查询是否存在
        Cursor cursor = contentResolver.query(uri,
                new String[]{"count(*) as count "},
                DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName}, null);
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, UploadStatus.start.name());
            contentResolver.delete(uri, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                    , new String[]{cloudFileName});
        }    //添加到数据库
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_FILE_EXTENSION_NAME, fileExtensionName);
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_NAME, image.getName());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_PATH, image.getPath());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_SIZE, image.getSize());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_WIDTH, image.getWidth());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_HEIGHT, image.getHeight());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME, cloudFileName);
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, UploadStatus.start.name());
        contentResolver.insert(uri, values);

    }

    public static final String[] EXIF_INTERFACE_ATTRIBUTES = new String[]{
            ExifInterface.TAG_ARTIST,//artist
            ExifInterface.TAG_COMPRESSION,//compression
            ExifInterface.TAG_COPYRIGHT,//copy right
            ExifInterface.TAG_DATETIME,//date time
            ExifInterface.TAG_IMAGE_DESCRIPTION,//description
            ExifInterface.TAG_X_RESOLUTION,//height
            ExifInterface.TAG_Y_RESOLUTION,//width
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_MAKE,//make
            ExifInterface.TAG_MODEL,//model
            ExifInterface.TAG_ORIENTATION,//
            ExifInterface.TAG_EXIF_VERSION,//exif version
            ExifInterface.TAG_WHITE_BALANCE,//white balance
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_EXPOSURE_PROGRAM,
            ExifInterface.TAG_ISO_SPEED_RATINGS,
            ExifInterface.TAG_EXPOSURE_MODE,
            ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
            ExifInterface.TAG_DNG_VERSION,
            ExifInterface.TAG_FOCAL_LENGTH
//            Image Description 图像描述、来源. 指生成图像的工具
//　　Artist作者 有些相机可以输入使用者的名字
//　　Make 生产者 指产品生产厂家
//　　Model 型号 指设备型号
//　　Orientation方向 有的相机支持，有的不支持
//　　XResolution/YResolution X/Y方向分辨率 本栏目已有专门条目解释此问题。
//　　ResolutionUnit分辨率单位 一般为PPI
//　　Software软件 显示固件Firmware版本
//　　DateTime日期和时间
//　　YCbCrPositioning 色相定位
//　　ExifOffsetExif信息位置，定义Exif在信息在文件中的写入，有些软件不显示。
//　　ExposureTime 曝光时间 即快门速度
//　　FNumber光圈系数
//　　ExposureProgram曝光程序 指程序式自动曝光的设置，各相机不同,可能是Sutter Priority（快门优先）、Aperture Priority（快门优先）等等。
//　　ISO speed ratings感光度
//　　ExifVersionExif版本
//　　DateTimeOriginal创建时间
//　　DateTimeDigitized数字化时间
//　　ComponentsConfiguration图像构造（多指色彩组合方案）
//　　CompressedBitsPerPixel(BPP)压缩时每像素色彩位 指压缩程度
//　　ExposureBiasValue曝光补偿。
//　　MaxApertureValue最大光圈
//　　MeteringMode测光方式， 平均式测光、中央重点测光、点测光等。
//　　Lightsource光源 指白平衡设置
//　　Flash是否使用闪光灯。
//　　FocalLength焦距，一般显示镜头物理焦距，有些软件可以定义一个系数，从而显示相当于35mm相机的焦距 MakerNote(User Comment)作者标记、说明、记录
//　　FlashPixVersionFlashPix版本 （个别机型支持）
//　　ColorSpace色域、色彩空间
//　　ExifImageWidth(Pixel X Dimension)图像宽度 指横向像素数
//　　ExifImageLength(Pixel Y Dimension)图像高度 指纵向像素数
//　　Interoperability IFD通用性扩展项定义指针 和TIFF文件相关，具体含义不详
//　　FileSource源文件 Compression压缩比。
    };

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        long totalByteCount = taskSnapshot.getTotalByteCount();
        long bytesTransferred = taskSnapshot.getBytesTransferred();
        dialog.setProgress((int) (100 * bytesTransferred / totalByteCount));
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (dialog != null) {
            dialog.dismiss();
        }
        Snackbar.make(coordinatorLayout, "上传失败", Snackbar.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, UploadStatus.failure.name());
        getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        if (dialog != null) {
            dialog.dismiss();
        }
        Snackbar.make(coordinatorLayout, "上传成功", Snackbar.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, UploadStatus.success.name());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_URL, taskSnapshot.getDownloadUrl().toString());
        getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});

    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
        if (dialog != null) {
            dialog.dismiss();
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, UploadStatus.pause.name());
        getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});
    }
}
