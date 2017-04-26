package cn.studyjams.s220170131.mijack.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.base.BaseActivity;
import cn.studyjams.s220170131.mijack.core.MediaManager;
import cn.studyjams.s220170131.mijack.entity.Image;

/**
 * @author Mr.Yuan
 * @date 2017/4/26
 */
public class ImageDisplayActivity extends BaseActivity implements View.OnClickListener {
    public static final String IMAGE = "image";
    private static final String TAG = "ImageDisplayActivity";
    private Image image;
    ImageView imageView;
    ImageView iconShare;
    ImageView iconUpload;
    ImageView iconDelete;
    ImageView iconInfo;
    private ImageView[] icons = new ImageView[4];
    ConstraintLayout contentPanel;
    private ExifInterface exifInterface;

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
        contentPanel = (ConstraintLayout) findViewById(R.id.contentPanel);
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
                Snackbar.make(contentPanel, "未实现", Snackbar.LENGTH_SHORT).show();
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
                TextView textView = (TextView) view.findViewById(R.id.textView3);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < EXIF_INTERFACE_ATTRIBUTES.length; i++) {
                    String attribute = EXIF_INTERFACE_ATTRIBUTES[i];
                    String attributeValue = exifInterface.getAttribute(attribute);
                    if (TextUtils.isEmpty(attributeValue)) {
                        continue;
                    }
                    sb.append(attribute).append(":").append(attributeValue).append("\n");
                }
                textView.setText(sb.toString());
                dialog.setContentView(view);
                dialog.show();
                break;
        }
    }

    public static final String[] EXIF_INTERFACE_ATTRIBUTES = new String[]{
            ExifInterface.TAG_ARTIST,
            ExifInterface.TAG_BITS_PER_SAMPLE,
            ExifInterface.TAG_COMPRESSION,
            ExifInterface.TAG_COPYRIGHT,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_IMAGE_DESCRIPTION,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_IMAGE_WIDTH,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT,
            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION,
            ExifInterface.TAG_PLANAR_CONFIGURATION,
            ExifInterface.TAG_PRIMARY_CHROMATICITIES,
            ExifInterface.TAG_REFERENCE_BLACK_WHITE,
            ExifInterface.TAG_RESOLUTION_UNIT,
            ExifInterface.TAG_ROWS_PER_STRIP,
            ExifInterface.TAG_SAMPLES_PER_PIXEL,
            ExifInterface.TAG_SOFTWARE,
            ExifInterface.TAG_STRIP_BYTE_COUNTS,
            ExifInterface.TAG_STRIP_OFFSETS,
            ExifInterface.TAG_TRANSFER_FUNCTION,
            ExifInterface.TAG_WHITE_POINT,
            ExifInterface.TAG_X_RESOLUTION,
            ExifInterface.TAG_Y_CB_CR_COEFFICIENTS,
            ExifInterface.TAG_Y_CB_CR_POSITIONING,
            ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING,
            ExifInterface.TAG_Y_RESOLUTION,
            ExifInterface.TAG_APERTURE_VALUE,
            ExifInterface.TAG_BRIGHTNESS_VALUE,
            ExifInterface.TAG_CFA_PATTERN,
            ExifInterface.TAG_COLOR_SPACE,
            ExifInterface.TAG_COMPONENTS_CONFIGURATION,
            ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL,
            ExifInterface.TAG_CONTRAST,
            ExifInterface.TAG_CUSTOM_RENDERED,
            ExifInterface.TAG_DATETIME_DIGITIZED,
            ExifInterface.TAG_DATETIME_ORIGINAL,
            ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION,
            ExifInterface.TAG_DIGITAL_ZOOM_RATIO,
            ExifInterface.TAG_EXIF_VERSION,
            ExifInterface.TAG_EXPOSURE_BIAS_VALUE,
            ExifInterface.TAG_EXPOSURE_INDEX,
            ExifInterface.TAG_EXPOSURE_MODE,
            ExifInterface.TAG_EXPOSURE_PROGRAM,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_F_NUMBER,
            ExifInterface.TAG_FILE_SOURCE,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FLASH_ENERGY,
            ExifInterface.TAG_FLASHPIX_VERSION,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM,
            ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT,
            ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION,
            ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION,
            ExifInterface.TAG_GAIN_CONTROL,
            ExifInterface.TAG_ISO_SPEED_RATINGS,
            ExifInterface.TAG_IMAGE_UNIQUE_ID,
            ExifInterface.TAG_LIGHT_SOURCE,
            ExifInterface.TAG_MAKER_NOTE,
            ExifInterface.TAG_MAX_APERTURE_VALUE,
            ExifInterface.TAG_METERING_MODE,
            ExifInterface.TAG_NEW_SUBFILE_TYPE,
            ExifInterface.TAG_OECF,
            ExifInterface.TAG_PIXEL_X_DIMENSION,
            ExifInterface.TAG_PIXEL_Y_DIMENSION,
            ExifInterface.TAG_RELATED_SOUND_FILE,
            ExifInterface.TAG_SATURATION,
            ExifInterface.TAG_SCENE_CAPTURE_TYPE,
            ExifInterface.TAG_SCENE_TYPE,
            ExifInterface.TAG_SENSING_METHOD,
            ExifInterface.TAG_SHARPNESS,
            ExifInterface.TAG_SHUTTER_SPEED_VALUE,
            ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE,
            ExifInterface.TAG_SPECTRAL_SENSITIVITY,
            ExifInterface.TAG_SUBFILE_TYPE,
            ExifInterface.TAG_SUBSEC_TIME, ExifInterface.TAG_SUBSEC_TIME_DIGITIZED, ExifInterface.TAG_SUBSEC_TIME_ORIGINAL,
            ExifInterface.TAG_SUBJECT_AREA, ExifInterface.TAG_SUBJECT_DISTANCE, ExifInterface.TAG_SUBJECT_DISTANCE_RANGE, ExifInterface.TAG_SUBJECT_LOCATION,
            ExifInterface.TAG_USER_COMMENT, ExifInterface.TAG_WHITE_BALANCE,
    };


}
