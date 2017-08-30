package cn.mijack.imagedrive.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.adapter.ImageAdapter
import cn.mijack.imagedrive.base.BaseFragment
import cn.mijack.imagedrive.core.MediaManager

/**
 * @author admin
 * @date 2017/8/30
 */
class ImageListFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        val SHOW_ALL_FOLDERS = 1
        val SHOW_IMAGE_IN_FOLDER = 2
        val SHOW_ALL_IMAGES = 3
        val FOLDER_PATH = "folderPath"
    }

    private var type: Int = SHOW_ALL_FOLDERS
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var folderPath: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_image_list, container, false)
        recyclerView = root.findViewById(R.id.recyclerView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        var span = 6
        imageAdapter = ImageAdapter(activity, span)
        imageAdapter.showType = ImageAdapter.SHOW_FOLDER
        gridLayoutManager = GridLayoutManager(activity, span)
        gridLayoutManager.spanSizeLookup = MySpanSizeLookup()
        onRefresh()
        recyclerView.layoutManager = gridLayoutManager
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    inner class MySpanSizeLookup : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = imageAdapter.getSpanSize(position)
    }

    fun showFolder() {
        type = SHOW_ALL_FOLDERS;
        imageAdapter.showType = ImageAdapter.SHOW_FOLDER
    }

    fun showImages() {
        type = SHOW_ALL_IMAGES
        imageAdapter.showType = ImageAdapter.SHOW_IMAGE_ONLY
    }

    //
    override fun onRefresh() {
        if (arguments != null && arguments.containsKey(FOLDER_PATH)) {
            showFolderImages(arguments.getString(FOLDER_PATH))
        } else {
            setImageData()
        }
    }

    fun setImageData() {
        swipeRefreshLayout.isRefreshing = true
        var media = MediaManager.flatFolder(MediaManager.getImagesFromFolder(getActivity(), 12))
        recyclerView.adapter = imageAdapter
        var images = MediaManager.getImages(activity)
        imageAdapter.setData(media, images)
        swipeRefreshLayout.isRefreshing = false
    }

    fun showFolderImages(folderPath: String) {
        swipeRefreshLayout.isRefreshing = true
        this.folderPath = folderPath
        var images = MediaManager.getImagesInFolder(activity, folderPath)
        imageAdapter.showType = ImageAdapter.SHOW_IMAGE_ONLY
        imageAdapter.setData(null, images)
        recyclerView.adapter = imageAdapter
        swipeRefreshLayout.isRefreshing = false
    }

}