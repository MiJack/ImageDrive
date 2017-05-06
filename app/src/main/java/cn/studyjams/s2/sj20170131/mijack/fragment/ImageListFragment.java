package cn.studyjams.s2.sj20170131.mijack.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.adapter.ImageAdapter;
import cn.studyjams.s2.sj20170131.mijack.base.BaseFragment;
import cn.studyjams.s2.sj20170131.mijack.core.MediaManager;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;
import cn.studyjams.s2.sj20170131.mijack.entity.Media;

/**
 * @author Mr.Yuan
 * @date 2017/4/25
 */
public class ImageListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int SHOW_ALL_FOLDERS = 1;
    public static final int SHOW_IMAGE_IN_FOLDER = 2;
    public static final int SHOW_ALL_IMAGES = 3;
    public static final String FOLDER_PATH = "folderPath";
    private int type = SHOW_ALL_FOLDERS;
    RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String folderPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_list, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int span = 6;
        imageAdapter = new ImageAdapter(getActivity(), span);
        imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
        gridLayoutManager = new GridLayoutManager(getActivity(), span);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return imageAdapter.getSpanSize(position);
            }
        });
        //加载images
        onRefresh();
        recyclerView.setLayoutManager(gridLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void showFolder() {
        type = SHOW_ALL_FOLDERS;
        imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
    }

    public void showImages() {
        type = SHOW_ALL_IMAGES;
        imageAdapter.setShowType(ImageAdapter.SHOW_IMAGE_ONLY);
    }

    @Override
    public void onRefresh() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(FOLDER_PATH)) {
            showFolderImages(arguments.getString(FOLDER_PATH));
        } else {
            setImageData();
        }
    }

    private void setImageData() {
        swipeRefreshLayout.setRefreshing(true);
        List<Media> media = MediaManager.flatFolder(MediaManager.getImageFolderWithImages(getActivity(), 12));
        recyclerView.setAdapter(imageAdapter);
        List<Image> images = MediaManager.getImages(getActivity());
        imageAdapter.setData(media, images);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showFolderImages(String folderPath) {
        swipeRefreshLayout.setRefreshing(true);
        this.folderPath = folderPath;
        List<Image> images = MediaManager.getImagesInFolder(getActivity(), folderPath);
        imageAdapter.setShowType(ImageAdapter.SHOW_IMAGE_ONLY);
        imageAdapter.setData(null, images);
        recyclerView.setAdapter(imageAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
