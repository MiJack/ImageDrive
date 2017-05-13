package cn.studyjams.s2.sj20170131.mijack.base;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import cn.studyjams.s2.sj20170131.mijack.R;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String appName = getString(R.string.app_name);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon);
            int color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            setTaskDescription(new ActivityManager.TaskDescription(appName, icon, color));
        }
    }

//    protected void uploadImage(Image image) {
//        Intent intent = new Intent(this, FireBaseStorageService.class)
//                .setAction(FireBaseStorageService.ACTION_ADD_TASK)
//                .putExtra(FireBaseStorageService.IMAGE, image);
//        startService(intent);
//    }

}
