package cn.mijack.imagedrive.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.componment.NavigationHeaderView
import cn.mijack.imagedrive.fragment.BackUpFragment
import cn.mijack.imagedrive.fragment.ImageDriverFragment
import cn.mijack.imagedrive.fragment.ImageListFragment
import com.google.firebase.auth.FirebaseAuth

/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/16
 */
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var headerView: NavigationHeaderView? = null
    private lateinit var navigationView: NavigationView
    private var firebaseAuth: FirebaseAuth? = null
    private var imageListFragment: ImageListFragment? = null
    private var imageDriverFragment: ImageDriverFragment? = null
    private var currentFragment: Fragment? = null
    private var backUpFragment: BackUpFragment? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        firebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        navigationView = findViewById<NavigationView>(R.id.navigationView)
        headerView = NavigationHeaderView(this, navigationView)
        headerView!!.loadLoginInfo()
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.content_drawer_open, R.string.content_drawer_close)
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        switchFragment(IMAGE_LIST_FRAGMENT)
        navigationView!!.setNavigationItemSelectedListener({ this.onNavigationItemSelected(it) })
    }

    private fun switchFragment(fragmentCode: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        when (fragmentCode) {
            IMAGE_LIST_FRAGMENT -> {
                title = getString(R.string.local)
                if (imageListFragment == null) {
                    imageListFragment = ImageListFragment()
                    transaction.add(R.id.frameLayout, imageListFragment)
                } else {
                    transaction.show(imageListFragment)
                }
                currentFragment = imageListFragment
            }
            IMAGE_DRIVER_FRAGMENT -> {
                title = getString(R.string.driver)
                if (imageDriverFragment == null) {
                    imageDriverFragment = ImageDriverFragment()
                    transaction.add(R.id.frameLayout, imageDriverFragment)
                } else {
                    transaction.show(imageDriverFragment)
                }
                currentFragment = imageDriverFragment
            }
            BACKUP_FRAGMENT -> {
                if (backUpFragment == null) {
                    backUpFragment = BackUpFragment()
                    transaction.add(R.id.frameLayout, backUpFragment)
                } else {
                    transaction.show(backUpFragment)
                }
                currentFragment = backUpFragment
            }
        }
        transaction.commit()
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.setGroupVisible(R.id.actionShow, currentFragment is ImageListFragment)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_LOGIN -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    return
                }
                if (resultCode == LoginActivity.RESULT_LOGIN) {
                    headerView!!.loadLoginInfo()
                    return
                }
                if (resultCode == LoginActivity.RESULT_NEW_ACCOUNT) {
                    headerView!!.loadLoginInfo()
                }
            }
            REQUEST_CODE_PROFILE -> headerView!!.loadLoginInfo()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionShowFolder -> imageListFragment!!.showFolder()
            R.id.actionShowImages -> imageListFragment!!.showImages()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionProfile -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                headerView!!.startProfileActivity()

                return true
            }
            R.id.actionDriver -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                switchFragment(IMAGE_DRIVER_FRAGMENT)
                return true
            }
            R.id.actionLocal -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                switchFragment(IMAGE_LIST_FRAGMENT)
                return true
            }
            R.id.actionBackUp -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                switchFragment(BACKUP_FRAGMENT)
                return true
            }
            R.id.actionAbout -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.actionSettings -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.actionLogout -> {
                drawerLayout!!.closeDrawer(Gravity.LEFT)
                if (dialog == null) {
                    val listener = { dialog: DialogInterface, which: Int ->
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            firebaseAuth!!.signOut()
                            headerView!!.loadLoginInfo()
                        }
                    }
                    dialog = AlertDialog.Builder(this)
                            .setTitle(R.string.sign_out)
                            .setIcon(R.drawable.ic_logout)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, listener)
                            .setNegativeButton(R.string.cancel, listener)
                            .setMessage(R.string.sign_out_message)
                            .create()
                }
                dialog!!.show()
                return true
            }
        }
        return false
    }

    companion object {
        val REQUEST_CODE_LOGIN = 1
        val REQUEST_CODE_PROFILE = 2

        private val IMAGE_LIST_FRAGMENT = 1
        private val IMAGE_DRIVER_FRAGMENT = 2
        private val BACKUP_FRAGMENT = 3
    }
}
