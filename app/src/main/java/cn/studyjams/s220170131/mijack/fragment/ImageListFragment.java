package cn.studyjams.s220170131.mijack.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.adapter.ImageAdapter;
import cn.studyjams.s220170131.mijack.base.BaseFragment;
import cn.studyjams.s220170131.mijack.core.MediaManager;
import cn.studyjams.s220170131.mijack.entity.Image;
import cn.studyjams.s220170131.mijack.entity.Media;

/**
 * @author Mr.Yuan
 * @date 2017/4/25
 */
public class ImageListFragment extends BaseFragment {
    RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private GridLayoutManager gridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image_list, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int span = 6;
        imageAdapter = new ImageAdapter(getActivity(), span);
        gridLayoutManager = new GridLayoutManager(getActivity(), span);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return imageAdapter.getSpanSize(position);
            }
        });
        //加载images
        List<Media> media = MediaManager.flatFolder(MediaManager.getImageFolderWithImages(getActivity(), 12));
        recyclerView.setAdapter(imageAdapter);
        List<Image> images = MediaManager.getImages(getActivity());
        imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
        imageAdapter.setData(media, images);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void showFolder() {
        imageAdapter.setShowType(ImageAdapter.SHOW_FOLDER);
    }

    public void showImages() {
        imageAdapter.setShowType(ImageAdapter.SHOW_IMAGE_ONLY);
    }
}
