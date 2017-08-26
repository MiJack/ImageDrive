package cn.mijack.imagedrive.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import cn.mijack.imagedrive.R;
import cn.mijack.imagedrive.base.BaseActivity;
import cn.mijack.imagedrive.componment.NavigationHeaderView;
import cn.mijack.imagedrive.fragment.BackUpFragment;
import cn.mijack.imagedrive.fragment.ImageDriverFragment;
import cn.mijack.imagedrive.fragment.ImageListFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_CODE_LOGIN = 1;
    public static final int REQUEST_CODE_PROFILE = 2;

    private static final int IMAGE_LIST_FRAGMENT = 1;
    private static final int IMAGE_DRIVER_FRAGMENT = 2;
    private static final int BACKUP_FRAGMENT = 3;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationHeaderView headerView;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private ImageListFragment imageListFragment;
    private ImageDriverFragment imageDriverFragment;
    private Fragment currentFragment = null;
    private BackUpFragment backUpFragment;
    private AlertDialog dialog;

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
        switchFragment(IMAGE_LIST_FRAGMENT);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void switchFragment(int fragmentCode) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        switch (fragmentCode) {
            case IMAGE_LIST_FRAGMENT:
                setTitle(getString(R.string.local));
                if (imageListFragment == null) {
                    imageListFragment = new ImageListFragment();
                    transaction.add(R.id.frameLayout, imageListFragment);
                } else {
                    transaction.show(imageListFragment);
                }
                currentFragment = imageListFragment;
                break;
            case IMAGE_DRIVER_FRAGMENT:
                setTitle(getString(R.string.driver));
                if (imageDriverFragment == null) {
                    imageDriverFragment = new ImageDriverFragment();
                    transaction.add(R.id.frameLayout, imageDriverFragment);
                } else {
                    transaction.show(imageDriverFragment);
                }
                currentFragment = imageDriverFragment;
                break;
            case BACKUP_FRAGMENT:
                if (backUpFragment == null) {
                    backUpFragment = new BackUpFragment();
                    transaction.add(R.id.frameLayout, backUpFragment);
                } else {
                    transaction.show(backUpFragment);
                }
                currentFragment = backUpFragment;
                break;
        }
        transaction.commit();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.setGroupVisible(R.id.actionShow, currentFragment instanceof ImageListFragment);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                if (resultCode == LoginActivity.RESULT_LOGIN) {
                    headerView.loadLoginInfo();
                    return;
                }
                if (resultCode == LoginActivity.RESULT_NEW_ACCOUNT) {
                    headerView.loadLoginInfo();
                }
                break;
            case REQUEST_CODE_PROFILE:
                headerView.loadLoginInfo();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionShowFolder:
                imageListFragment.showFolder();
                break;
            case R.id.actionShowImages:
                imageListFragment.showImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionProfile:
                drawerLayout.closeDrawer(Gravity.LEFT);
                headerView.startProfileActivity();

                return true;
            case R.id.actionDriver:
                drawerLayout.closeDrawer(Gravity.LEFT);
                switchFragment(IMAGE_DRIVER_FRAGMENT);
                return true;
            case R.id.actionLocal:
                drawerLayout.closeDrawer(Gravity.LEFT);
                switchFragment(IMAGE_LIST_FRAGMENT);
                return true;
            case R.id.actionBackUp:
                drawerLayout.closeDrawer(Gravity.LEFT);
                switchFragment(BACKUP_FRAGMENT);
                return true;
            case R.id.actionAbout:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.actionSettings:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.actionLogout:
                drawerLayout.closeDrawer(Gravity.LEFT);
                if (dialog == null) {
                    DialogInterface.OnClickListener listener =
                            (DialogInterface dialog, int which) -> {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    firebaseAuth.signOut();
                                    headerView.loadLoginInfo();
                                }
                            };
                    dialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.sign_out)
                            .setIcon(R.drawable.ic_logout)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, listener)
                            .setNegativeButton(R.string.cancel, listener)
                            .setMessage(R.string.sign_out_message)
                            .create();
                }
                dialog.show();
                return true;
        }
        return false;
    }
}
