package cn.mijack.imagedrive.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.entity.Folder
import cn.mijack.imagedrive.entity.Image
import cn.mijack.imagedrive.entity.Media
import cn.mijack.imagedrive.ui.ImageDisplayActivity
import cn.mijack.imagedrive.ui.ImageFolderActivity
import cn.mijack.imagedrive.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import java.io.File

/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/17
 */
class ImageAdapter(context: Context, private var bigSpanCount: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private val glide: Glide = Glide.get(context)
    private val requestManager: RequestManager = Glide.with(context)
    private var data: List<Media>? = null
    private var images: List<Image>? = null
    var showType: Int = 0
        set(showType) {
            field = showType
            this.notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        if (this.showType == SHOW_IMAGE_ONLY) {
            return ITEM_IMAGE
        }
        val media = data!![position]
        if (media is Folder) {
            return ITEM_FOLDER
        } else if (media is Image) {
            return ITEM_IMAGE
        }
        throw IllegalArgumentException()
    }

    fun getSpanSize(position: Int): Int {
        if (this.showType == SHOW_FOLDER) {
            val media = data!![position]
            return if (media is Folder) bigSpanCount else 1
        }
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        when (viewType) {
            ITEM_FOLDER -> view = inflater.inflate(R.layout.item_folder, parent, false)
            ITEM_IMAGE -> view = inflater.inflate(R.layout.item_image, parent, false)
        }
        view!!.setOnClickListener(this)
        return object : RecyclerView.ViewHolder(view!!) {

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        val root = holder.itemView
        root.setTag(R.id.layout_position, position)
        when (itemViewType) {
            ITEM_FOLDER -> {
                val folder = data!![position] as Folder
                val folderName = root.findViewById<View>(R.id.folderName) as TextView
                //                TextView folderDesc = (TextView) root.findViewById(R.id.folderDesc);
                folderName.text = File(folder.path).name
            }
            ITEM_IMAGE -> {
                val image = (if (this.showType == SHOW_FOLDER) data else images)!![position] as Image
                val imageView = root.findViewById<View>(R.id.imageView) as ImageView
                requestManager.load(File(image.path)).placeholder(R.drawable.ic_empty_picture)
                        .into(imageView)
            }
        }//                folderDesc.setText((folder.getCount() > 0 ? String.format("%d张图片", folder.getCount()) : ""));
    }

    override fun getItemCount(): Int {
        return if (this.showType == SHOW_FOLDER) Utils.size(data) else Utils.size(images)
    }

    fun getBigSpanCount(): Int {
        return bigSpanCount
    }

    fun setBigSpanCount(bigSpanCount: Int) {
        this.bigSpanCount = bigSpanCount
        this.notifyDataSetChanged()
    }

    fun setData(data: List<Media>?, images: List<Image>?) {
        assert(data != null)
        assert(images != null)
        this.data = data
        this.images = images
        this.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        val position = v.getTag(R.id.layout_position) as Int
        val itemViewType = getItemViewType(position)
        val data = if (this.showType == SHOW_FOLDER) this.data else this.images
        Log.d(TAG, "itemViewType:" + if (itemViewType == ITEM_FOLDER) "ITEM_FOLDER" else "ITEM_IMAGE")
        if (itemViewType == ITEM_FOLDER) {
            val folder = data!![position] as Folder
            val intent = Intent(v.context, ImageFolderActivity::class.java)
            intent.putExtra(ImageFolderActivity.FOLDER_PATH, folder.path)
            v.context.startActivity(intent)
        } else if (itemViewType == ITEM_IMAGE) {
            val image = data!![position] as Image
            val context = v.context
            val activityOptions = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(context as Activity,
                            Pair(v.findViewById<View>(R.id.imageView), "image")
                    )
            ImageDisplayActivity.showLocalImage(v.context, image, activityOptions.toBundle())
        }
    }

    companion object {
        val ITEM_FOLDER = 0
        val ITEM_IMAGE = 1
        val SHOW_FOLDER = 3
        val SHOW_IMAGE_ONLY = 4
        private val TAG = "ImageAdapter"
    }
}
