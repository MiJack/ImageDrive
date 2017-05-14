package cn.studyjams.s2.sj20170131.mijack.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.adapter.FirebaseStorageAdapter;
import cn.studyjams.s2.sj20170131.mijack.base.BaseFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class ImageDriverFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, ValueEventListener {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseStorageAdapter storageImageAdapter;
    private static final String TAG = "ImageDriverFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_driver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadData(currentUser);
        }
    }

    private void loadData(FirebaseUser currentUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("images").child("users").child(currentUser.getUid());
        Log.d(TAG, "loadData: "+reference.toString());
        Query query = reference.orderByKey();
        query.keepSynced(true);
        query.addValueEventListener(this);
        storageImageAdapter = new FirebaseStorageAdapter(query);
        recyclerView.setAdapter(storageImageAdapter);
    }

    @Override
    public void onRefresh() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadData(currentUser);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        swipeRefreshLayout.setRefreshing(false);
    }
}
