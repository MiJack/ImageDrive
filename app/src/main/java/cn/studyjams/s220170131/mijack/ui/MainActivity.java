package cn.studyjams.s220170131.mijack.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.adapter.ImageAdapter;
import cn.studyjams.s220170131.mijack.base.BaseActivity;
import cn.studyjams.s220170131.mijack.componment.NavigationHeaderView;
import cn.studyjams.s220170131.mijack.core.MediaManager;
import cn.studyjams.s220170131.mijack.entity.Image;
import cn.studyjams.s220170131.mijack.entity.Media;
import cn.studyjams.s220170131.mijack.fragment.ImageListFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_CODE_LOGIN = 1;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationHeaderView headerView;
    NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    ImageListFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        headerView = new NavigationHeaderView(this, navigationView);
        headerView.loadLoginInfo();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.content_drawer_open, R.string.content_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        fragment = new ImageListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, fragment)
                .commit();
        navigationView.setNavigationItemSelectedListener(this);
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
                fragment.showFolder();
                break;
            case R.id.actionShowImages:
                fragment.showImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLogout:
                DialogInterface.OnClickListener listener =
                        (DialogInterface dialog, int which) -> {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                firebaseAuth.signOut();
                                headerView.loadLoginInfo();
                            }
                        };
                new AlertDialog.Builder(this)
                        .setTitle("Sign out")
                        .setIcon(R.drawable.ic_logout)
                        .setCancelable(false)
                        .setPositiveButton("确定", listener)
                        .setNegativeButton("取消", listener)
                        .setMessage("你确定要退出吗？")
                        .create().show();
                break;
        }
        return false;
    }
}
