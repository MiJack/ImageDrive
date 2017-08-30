package cn.mijack.imagedrive.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.fragment.ImageListFragment
import java.io.File


/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/26
 */
class ImageFolderActivity : BaseActivity() {
    private var imageListFragment: ImageListFragment? = null
    private var folderPath: String? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_folder)
        val intent = intent
        if (intent == null || !intent.hasExtra(FOLDER_PATH)) {
            return
        }
        folderPath = intent.getStringExtra(FOLDER_PATH)
        if (!File(folderPath!!).exists()) {

            return
        }
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setTitle(File(folderPath!!).name)
        imageListFragment = ImageListFragment()
        val args = Bundle()
        args.putString(ImageListFragment.FOLDER_PATH, folderPath)
        imageListFragment!!.arguments = args
        supportFragmentManager.beginTransaction()
                .add(R.id.frameLayout, imageListFragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val FOLDER_PATH = "folder_path"
    }
}
