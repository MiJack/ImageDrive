package cn.mijack.imagedrive.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.adapter.AttributeAdapter
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.core.MediaManager
import cn.mijack.imagedrive.entity.Attribute
import cn.mijack.imagedrive.entity.FirebaseImage
import cn.mijack.imagedrive.entity.Image
import cn.mijack.imagedrive.util.Utils
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.ArrayList
import java.util.regex.Pattern


/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/26
 */
class ImageDisplayActivity : BaseActivity() {
    private var image: Image? = null
    private var imageView: ImageView? = null
    private var iconShare: ImageView? = null
    private var iconUpload: ImageView? = null
    private var iconDelete: ImageView? = null
    private var iconInfo: ImageView? = null
    private val icons = ArrayList<ImageView>()
    private var coordinatorLayout: CoordinatorLayout? = null
    private var dialog: MaterialDialog? = null
    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private var cloudFileName: String? = null
    private var type: String? = null
    private var firebaseImage: FirebaseImage? = null
    private var iconDownload: ImageView? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var downloadDialog: MaterialDialog? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_display)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        firebaseStorage = FirebaseStorage.getInstance()
        imageView = findViewById<View>(R.id.imageView) as ImageView
        iconShare = findViewById<View>(R.id.iconShare) as ImageView
        iconUpload = findViewById<View>(R.id.iconUpload) as ImageView
        iconDownload = findViewById<View>(R.id.iconDownload) as ImageView
        iconDelete = findViewById<View>(R.id.iconDelete) as ImageView
        iconInfo = findViewById<View>(R.id.iconInfo) as ImageView
        coordinatorLayout = findViewById<View>(R.id.coordinatorLayout) as CoordinatorLayout
        val intent = intent
        if (intent == null || !intent.hasExtra(TYPE)) {
            return
        }
        type = intent.getStringExtra(TYPE)
        if (LOCAL_FILE == type && intent.hasExtra(IMAGE)) {
            image = intent.getParcelableExtra<Image>(IMAGE)
            Log.d(TAG, "onCreate: image:" + image!!.path)
            Glide.with(imageView!!.context)
                    .load(image!!.path)
                    //                    .placeholder(R.drawable.ic_picture_filled)
                    .into(imageView!!)
        } else if (FIREBASE_STORAGE == type && intent.hasExtra(DOWNLOAD_URL)) {
            firebaseImage = intent.getParcelableExtra<FirebaseImage>(DOWNLOAD_URL)
            val databaseReferenceUrl = intent.getStringExtra(DATABASE_REFERENCE_URL)
            databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(databaseReferenceUrl)
            val url = firebaseImage!!.downloadUrl
            Log.d(TAG, "onCreate: url:" + url)
            Glide.with(imageView!!.context)
                    .load(url)
                    .into(imageView!!)
        } else {
            return
        }
        ViewCompat.setTransitionName(imageView, "image")
        icons.add(iconShare!!)
        icons.add(iconUpload!!)
        icons.add(iconDelete!!)
        icons.add(iconInfo!!)
        icons.add(iconDownload!!)
        for (i in icons.indices) {
            icons[i].setOnClickListener(if (LOCAL_FILE == type) View.OnClickListener { this.handleLocalFile(it) } else View.OnClickListener { this.handleFirebaseFile(it) })
        }
        showIcons(true)
    }

    fun handleFirebaseFile(v: View) {
        when (v.id) {
            R.id.iconShare -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse(firebaseImage!!.downloadUrl)
                intent.type = firebaseImage!!.miniType
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
            }
            R.id.iconDownload -> {
                val file = File(firebaseImage!!.localPath)
                val content = if (file.exists()) getString(R.string.file_exist_and_cover) else getString(R.string.download_file_to_local)
                MaterialDialog.Builder(this)
                        .title(R.string.download)
                        .content(content)
                        .autoDismiss(false)
                        .positiveText(R.string.ok)
                        .onPositive { materialDialog, dialogAction ->
                            materialDialog.dismiss()
                            downloadImage()
                        }
                        .negativeText(R.string.cancel)
                        .onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }
                        .show()
            }
            R.id.iconDelete -> {
                val dialogInterface = { dialog: DialogInterface, which: Int ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteFirebaseFile()
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        //nothing
                    }
                }
                AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.are_you_sure_to_delete)
                        .setPositiveButton(R.string.ok, dialogInterface)
                        .setNegativeButton(R.string.cancel, dialogInterface)
                        .create().show()
            }
            R.id.iconInfo -> showFireBaseImageInfo()
        }
    }

    private fun deleteFirebaseFile() {
        val reference = firebaseStorage!!.reference.child(firebaseImage!!.fsUrl)
        reference.delete().addOnFailureListener { e -> Toast.makeText(this@ImageDisplayActivity, R.string.delete_failure, Toast.LENGTH_SHORT).show() }.addOnSuccessListener {
            Toast.makeText(this@ImageDisplayActivity, R.string.delete_seccess, Toast.LENGTH_SHORT).show()
            databaseReference!!.removeValue()
            finish()
        }
    }

    private fun showFireBaseImageInfo() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_image_info, null)
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        val adapter = AttributeAdapter<String>()
        val list = ArrayList<Attribute<String>>()
        list.add(Attribute(getString(R.string.file_name), firebaseImage!!.name))
        list.add(Attribute(getString(R.string.upload_device), firebaseImage!!.device))
        list.add(Attribute(getString(R.string.upload_device_id), firebaseImage!!.deviceId))
        list.add(Attribute(getString(R.string.download_link), firebaseImage!!.downloadUrl))
        list.add(Attribute(getString(R.string.resolution), firebaseImage!!.width.toString() + "*" + firebaseImage!!.height))
        list.add(Attribute(getString(R.string.local_path_before), firebaseImage!!.localPath))
        list.add(Attribute(getString(R.string.create_time), Utils.formatTime(firebaseImage!!.dateTaken)))
        list.add(Attribute(getString(R.string.upload_time), Utils.formatTime(firebaseImage!!.uploadTime)))
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setList(list)
        recyclerView.adapter = adapter
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showIcons(show: Boolean) {
        for (i in icons.indices) {
            val imageView = icons[i]
            when (imageView.id) {
                R.id.iconDownload -> imageView.visibility = if (show && FIREBASE_STORAGE == type) View.VISIBLE else View.GONE
                R.id.iconUpload -> imageView.visibility = if (show && LOCAL_FILE == type) View.VISIBLE else View.GONE
                else -> imageView.visibility = if (show) View.VISIBLE else View.GONE
            }
        }
    }

    fun handleLocalFile(v: View) {
        when (v.id) {
            R.id.iconShare -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.data = Uri.parse(image!!.path)
                intent.type = image!!.miniType
                startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
            }
            R.id.iconDownload -> MaterialDialog.Builder(this)
                    .title(R.string.download)
                    .content(R.string.download_file_to_local)
                    .autoDismiss(false)
                    .positiveText(R.string.ok)
                    .onPositive { materialDialog, dialogAction ->
                        materialDialog.dismiss()
                        downloadImage()
                    }
                    .negativeText(R.string.cancel)
                    .onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }
                    .show()
            R.id.iconUpload -> MaterialDialog.Builder(this)
                    .title(R.string.upload)
                    .content(R.string.upload_file)
                    .autoDismiss(false)
                    .positiveText(R.string.ok)
                    .onPositive { materialDialog, dialogAction ->
                        uploadImage(image!!)
                        materialDialog.dismiss()
                    }
                    .negativeText(R.string.cancel)
                    .onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }
                    .show()
            R.id.iconDelete -> {
                val dialogInterface = { dialog: DialogInterface, which: Int ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        if (MediaManager.deleteFile(image!!.path)) {
                            Toast.makeText(this, R.string.delete_seccess, Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, R.string.delete_failure, Toast.LENGTH_SHORT).show()
                        }
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        //nothing
                    }
                }
                AlertDialog.Builder(this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.are_you_sure_to_delete)
                        .setPositiveButton(R.string.ok, dialogInterface)
                        .setNegativeButton(R.string.cancel, dialogInterface)
                        .create().show()
            }
            R.id.iconInfo -> {
                val dialog = BottomSheetDialog(this)
                val view = LayoutInflater.from(this).inflate(R.layout.layout_image_info, null)
                val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
                val adapter = AttributeAdapter<String>()
                val list = ArrayList<Attribute<String>>()
                list.add(Attribute(getString(R.string.file_name), image!!.name!!))
                list.add(Attribute(getString(R.string.resolution), image!!.width.toString() + "*" + image!!.height))
                list.add(Attribute(getString(R.string.local_path), image!!.path))
                list.add(Attribute(getString(R.string.file_size), image!!.size.toString() + "KB"))
                list.add(Attribute(getString(R.string.create_time), Utils.formatTime(image!!.dateTaken)))
                recyclerView.layoutManager = LinearLayoutManager(this)
                adapter.setList(list)
                recyclerView.adapter = adapter
                dialog.setContentView(view)
                dialog.show()
            }
        }
    }

    private fun downloadImage() {
        val localPath = firebaseImage!!.localPath
        val reference = firebaseStorage!!.reference.child(firebaseImage!!.fsUrl)
        if (downloadDialog == null) {
            downloadDialog = MaterialDialog.Builder(this)
                    .title(R.string.download)
                    .cancelable(false)
                    .progress(false, 100, true)
                    .build()
        }
        downloadDialog!!.show()
        reference.getFile(File(localPath))
                .addOnProgressListener { taskSnapshot -> downloadDialog!!.setProgress((taskSnapshot.bytesTransferred * 100 / taskSnapshot.totalByteCount).toInt()) }
                .addOnSuccessListener { taskSnapshot ->
                    downloadDialog!!.cancel()
                    Snackbar.make(coordinatorLayout!!, R.string.download_file_success, Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    downloadDialog!!.cancel()
                    Snackbar.make(coordinatorLayout!!, R.string.download_file_failure, Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun uploadImage(image: Image) {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            MaterialDialog.Builder(this)
                    .title(R.string.login_please).content(R.string.upload_after_login)
                    .cancelable(false)
                    .negativeText(R.string.ok)
                    .onNegative { materialDialog, dialogAction -> materialDialog.dismiss() }.build().show()
            return
        }
        val firebaseStorage = FirebaseStorage.getInstance()
        val file = File(image.path)
        val md5 = Utils.fileMD5(file)
        val fileExtensionName = Utils.fileExtensionName(file)
        val device = Build.DEVICE
        cloudFileName = Utils.base64Encode(device + "-" + image.path + "-" + md5)!! + fileExtensionName!!
        println(cloudFileName)
        val reference = firebaseStorage.reference
                .child("image").child(firebaseAuth.currentUser!!.uid)
        dialog = MaterialDialog.Builder(this)
                .title(R.string.upload)
                .cancelable(false)
                .progress(false, 100, true)
                .negativeText(R.string.cancel)
                .onNegative { materialDialog, dialogAction ->
                    if (storageTask != null) {
                        storageTask!!.cancel()
                    }
                }.build()
        dialog!!.show()
        storageTask = reference.child(cloudFileName!!)
                .putFile(Uri.fromFile(file))
                .addOnProgressListener(this
                ) { taskSnapshot ->
                    val totalByteCount = taskSnapshot.totalByteCount
                    val bytesTransferred = taskSnapshot.bytesTransferred
                    dialog!!.setProgress((100 * bytesTransferred / totalByteCount).toInt())
                }
                .addOnSuccessListener(this) { taskSnapshot ->
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    Snackbar.make(coordinatorLayout!!, R.string.upload_success, Snackbar.LENGTH_SHORT).show()
                    val downloadUrl = taskSnapshot.downloadUrl!!.toString()
                    val pattern = Pattern.compile("^image/([^/]+)(?:/.*)$")
                    val metadata = taskSnapshot.metadata
                    val fsUrl = metadata!!.path
                    val matcher = pattern.matcher(metadata.path)
                    if (matcher.matches()) {
                        val uid = matcher.group(1)
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val reference1 = firebaseDatabase.getReference("images").child("users").child(uid)
                        val fsImage = FirebaseImage(image, downloadUrl, fsUrl)
                        val push = reference1.push()
                        push.updateChildren(fsImage.toMap())
                    }

                }
                .addOnFailureListener(this) { e ->
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    Snackbar.make(coordinatorLayout!!, R.string.upload_failure, Snackbar.LENGTH_SHORT).show()
                }
                .addOnPausedListener(this) { taskSnapshot ->
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                }
    }

    companion object {
        val IMAGE = "image"
        private val TAG = "ImageDisplayActivity"
        val DOWNLOAD_URL = "downloadUrl"
        val TYPE = "type"
        val LOCAL_FILE = "localFile"
        val FIREBASE_STORAGE = "firebaseStorage"
        private val DATABASE_REFERENCE_URL = "database_reference_url"

        fun showLocalImage(context: Context, image: Image, bundle: Bundle) {
            val intent = Intent(context, ImageDisplayActivity::class.java)
                    .putExtra(IMAGE, image)
                    .putExtra(TYPE, LOCAL_FILE)
            ActivityCompat.startActivity(context, intent, bundle)
        }

        fun showFirebaseImage(context: Context, firebaseImage: FirebaseImage, databaseReference: String, bundle: Bundle) {
            val intent = Intent(context, ImageDisplayActivity::class.java)
                    .putExtra(DOWNLOAD_URL, firebaseImage)
                    .putExtra(DATABASE_REFERENCE_URL, databaseReference)
                    .putExtra(TYPE, FIREBASE_STORAGE)
            ActivityCompat.startActivity(context, intent, bundle)
        }
    }

}
