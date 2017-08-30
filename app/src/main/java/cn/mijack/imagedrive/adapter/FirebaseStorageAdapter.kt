package cn.mijack.imagedrive.adapter

import android.app.Activity
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.entity.FirebaseImage
import cn.mijack.imagedrive.ui.ImageDisplayActivity
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * @author admin
 * @date 2017/8/30
 */

class FirebaseStorageAdapter(ref: Query) : FirebaseRecyclerAdapter<FirebaseImage, FirebaseStorageAdapter.StorageHolder>(FirebaseImage::class.java, R.layout.item_driver, StorageHolder::class.java, ref) {

    override fun populateViewHolder(storageHolder: StorageHolder, firebaseImage: FirebaseImage, position: Int) {
        val reference = getRef(position)
        storageHolder.loadImage(firebaseImage, reference)
    }


    class StorageHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val image: ImageView = itemView.findViewById<View>(R.id.image) as ImageView
        private lateinit var firebaseImage: FirebaseImage
        private var reference: DatabaseReference? = null

        init {
            image.setOnClickListener(this)
        }

        fun loadImage(firebaseImage: FirebaseImage, reference: DatabaseReference) {
            this.firebaseImage = firebaseImage
            this.reference = reference
            val url = firebaseImage.downloadUrl
            Log.d(TAG, "loadImage: url" + url)
            Glide.with(itemView.context).load(url).into(image)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.image) {
                val activityOptions = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(v.context as Activity, v, "image")
                ImageDisplayActivity.showFirebaseImage(v.context, firebaseImage, reference!!.toString(), activityOptions.toBundle())
            }
        }

        companion object {
            private val TAG = "StorageHolder"
        }
    }

    companion object {
        val TAG = "FirebaseStorageAdapter"
    }
}
