package cn.mijack.imagedrive.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.util.TextHelper
import cn.mijack.imagedrive.util.Utils
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : BaseActivity(), View.OnClickListener {
    private var firebaseAuth: FirebaseAuth? = null

    private var toolbar: Toolbar? = null
    private var titleInfo: TextView? = null
    private var circleImageView: CircleImageView? = null
    private var selectAvatar: Button? = null
    private var editNickName: TextInputLayout? = null
    private var editEmail: TextInputLayout? = null
    private var coordinatorLayout: CoordinatorLayout? = null
    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        titleInfo = findViewById<TextView>(R.id.titleInfo)
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        circleImageView = findViewById<CircleImageView>(R.id.circleImageView)
        selectAvatar = findViewById<Button>(R.id.selectAvatar)
        editNickName = findViewById<TextInputLayout>(R.id.editNickName)
        editEmail = findViewById<TextInputLayout>(R.id.editEmail)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        ViewCompat.setTransitionName(circleImageView, "profile")
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.currentUser
        editNickName!!.editText!!.setText(user!!.displayName)
        editEmail!!.editText!!.setText(user.email)
        println("photo uri:" + user.photoUrl!!)
        if (!Utils.isEmpty(user.photoUrl!!)) {
            Glide.with(this).load(user.photoUrl).into(circleImageView!!)
        } else {
            circleImageView!!.setImageResource(R.drawable.ic_empty_profile)
        }

        selectAvatar!!.setOnClickListener(this)
        circleImageView!!.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSave -> {
                editNickName!!.isEnabled = false
                val request = UserProfileChangeRequest.Builder()
                        .setDisplayName(TextHelper.getText(editNickName!!))
                        .build()
                firebaseAuth!!.currentUser!!.updateProfile(request)
                        .addOnFailureListener(this) { result -> Snackbar.make(coordinatorLayout!!, R.string.settings_failure, Snackbar.LENGTH_SHORT).show() }
                        .addOnSuccessListener(this) { result -> Snackbar.make(coordinatorLayout!!, R.string.settings_success, Snackbar.LENGTH_SHORT).show() }
                        .addOnCompleteListener(this) { result -> editNickName!!.isEnabled = true }
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.selectAvatar || v.id == R.id.circleImageView) {
            gotoPickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            //todo
            val imageUri = data.data
            dialog = MaterialDialog.Builder(this).title(R.string.setting_avatar)
                    .progress(true, 100).build()
            dialog!!.show()
            val firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth.currentUser
            val uid = user!!.uid
            val firebaseStorage = FirebaseStorage.getInstance()
            val reference = firebaseStorage.reference.child("avatars").child(uid)
            reference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
                val downloadUrl = taskSnapshot.downloadUrl
                val firebaseAuth1 = FirebaseAuth.getInstance()
                val request = UserProfileChangeRequest.Builder().setPhotoUri(downloadUrl)
                        .build()
                firebaseAuth1.currentUser!!.updateProfile(request).addOnSuccessListener { aVoid ->
                    Snackbar.make(coordinatorLayout!!, R.string.setting_avatar_success, Toast.LENGTH_SHORT).show()
                    dialog!!.cancel()
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    Glide.with(this@ProfileActivity).load(firebaseUser!!.photoUrl).into(circleImageView!!)
                }.addOnFailureListener { e -> Snackbar.make(coordinatorLayout!!, R.string.setting_avatar_failure, Toast.LENGTH_SHORT).show() }
            }.addOnFailureListener { e -> Snackbar.make(coordinatorLayout!!, R.string.upload_avatar_failure, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun gotoPickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    companion object {

        private val TAG = "ProfileActivity"
        private val REQUEST_PICK_IMAGE = 1
    }
}
