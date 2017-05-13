package cn.studyjams.s2.sj20170131.mijack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;

/**
 * @author Mr.Yuan
 * @date 2017/5/13
 */
public class UploadImageDialog extends BaseActivity {
    public static final String IMAGE = "image";

    ProgressBar progressBar;
    TextView content;
    Button button2;
    Button button3;
    Image image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upload_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        content = (TextView) findViewById(R.id.content);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(IMAGE)) {
            finish();
            return;
        }
        image = intent.getParcelableExtra(IMAGE);
        progressBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        button2.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            //upload
            upload();
        });
        button3.setOnClickListener(v -> finish());
    }

    private void upload() {
        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }

}
