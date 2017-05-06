package cn.studyjams.s2.sj20170131.mijack.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import cn.studyjams.s2.sj20170131.mijack.entity.Image;
import cn.studyjams.s2.sj20170131.mijack.firebase.FireBaseStorageService;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class BaseActivity extends AppCompatActivity {

    protected void uploadImage(Image image) {
        Intent intent = new Intent(this, FireBaseStorageService.class)
                .setAction(FireBaseStorageService.ACTION_ADD_TASK)
                .putExtra(FireBaseStorageService.IMAGE, image);
        startService(intent);
    }

}
