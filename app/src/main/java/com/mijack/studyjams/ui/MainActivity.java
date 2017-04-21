package com.mijack.studyjams.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mijack.studyjams.R;
import com.mijack.studyjams.adapter.ImageAdapter;
import com.mijack.studyjams.base.BaseActivity;
import com.mijack.studyjams.componment.NavigationHeaderView;
import com.mijack.studyjams.core.MediaManager;
import com.mijack.studyjams.entity.Image;
import com.mijack.studyjams.entity.Media;

import java.util.List;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class MainActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOGIN = 1;
    Toolbar toolbar;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    ImageAdapter imageAdapter;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationHeaderView headerView;
    NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        headerView = new NavigationHeaderView(this, navigationView);
        headerView.loadLoginInfo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.content_drawer_open, R.string.content_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        int span = 6;
        imageAdapter = new ImageAdapter(this, span);
        gridLayoutManager = new GridLayoutManager(this, span);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return imageAdapter.getSpanSize(position);
            }
        });
        //加载images
        List<Media> media = MediaManager.flatFolder(MediaManager.getImageFolderWithImages(this, 12));
        recyclerView.setAdapter(imageAdapter);
        List<Image> images = MediaManager.getImages(this);
        imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
        imageAdapter.setData(media, images);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionShowFolder:
                imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
                break;
            case R.id.actionShowImages:
                imageAdapter.setShowType(ImageAdapter.SHOW_IMAGE_ONLY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
