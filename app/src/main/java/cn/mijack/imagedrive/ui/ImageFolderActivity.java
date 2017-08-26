package cn.mijack.imagedrive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.File;

import cn.mijack.imagedrive.R;
import cn.mijack.imagedrive.base.BaseActivity;
import cn.mijack.imagedrive.fragment.ImageListFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/26
 */
public class ImageFolderActivity extends BaseActivity {
    public static final String FOLDER_PATH = "folder_path";
    private ImageListFragment imageListFragment;
    private String folderPath;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(FOLDER_PATH)) {
            return;
        }
        folderPath = intent.getStringExtra(FOLDER_PATH);
        if (!new File(folderPath).exists()){

            return;
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(new File(folderPath).getName());
        imageListFragment = new ImageListFragment();
        Bundle args = new Bundle();
        args.putString(ImageListFragment.FOLDER_PATH, folderPath);
        imageListFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, imageListFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
