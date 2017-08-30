package cn.mijack.imagedrive.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.adapter.FirebaseStorageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

/**
 * @author admin
 * @date 2017/8/30
 */
class ImageDriverFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ValueEventListener {
    lateinit var recyclerView: RecyclerView;
    lateinit var swipeRefreshLayout: SwipeRefreshLayout;
    lateinit var storageImageAdapter: FirebaseStorageAdapter;
    private var TAG = "ImageDriverFragment";
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater!!.inflate(R.layout.fragment_image_driver, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view != null) {
            recyclerView = view.findViewById(R.id.recyclerView)
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setOnRefreshListener(this)
            var layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.layoutManager = layoutManager
            var currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                loadData(currentUser)
            }
        }
    }

    //
    fun loadData(currentUser: FirebaseUser) {
        var reference = FirebaseDatabase.getInstance().getReference("images").child("users").child(currentUser.uid)
        Log.d(TAG, "loadData: " + reference.toString())
        var query = reference.orderByKey()
        query.keepSynced(true)
        query.addValueEventListener(this)
        storageImageAdapter = FirebaseStorageAdapter(query)
        recyclerView.adapter = storageImageAdapter
    }

    override fun onRefresh() {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            loadData(currentUser)
        } else {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onCancelled(p0: DatabaseError?) {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onDataChange(p0: DataSnapshot?) {
        swipeRefreshLayout.isRefreshing = false
    }
}
