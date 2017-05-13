package cn.studyjams.s2.sj20170131.mijack.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.adapter.FirebaseStorageAdapter;
import cn.studyjams.s2.sj20170131.mijack.base.BaseFragment;
import cn.studyjams.s2.sj20170131.mijack.database.DataBaseContentProvider;
import cn.studyjams.s2.sj20170131.mijack.database.DatabaseSQLiteOpenHelper;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class ImageDriverFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    FirebaseStorageAdapter storageImageAdapter;

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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        storageImageAdapter = new FirebaseStorageAdapter(getActivity(), null, true);
        recyclerView.setAdapter(storageImageAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Uri.parse("content://" + DataBaseContentProvider.AUTHORITIES + "/" + DatabaseSQLiteOpenHelper.TABLE_NAME),
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (storageImageAdapter == null) {
            storageImageAdapter = new FirebaseStorageAdapter(getActivity(), data, true);
            recyclerView.setAdapter(storageImageAdapter);
//            mShowList.setAdapter(mShowsAdapter);
        }
        storageImageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        storageImageAdapter.swapCursor(null);
    }
}
