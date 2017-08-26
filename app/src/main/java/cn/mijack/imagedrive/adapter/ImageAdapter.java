package cn.mijack.imagedrive.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import cn.mijack.imagedrive.R;
import cn.mijack.imagedrive.entity.Image;
import cn.mijack.imagedrive.entity.Media;
import cn.mijack.imagedrive.ui.ImageDisplayActivity;
import cn.mijack.imagedrive.ui.ImageFolderActivity;
import cn.mijack.imagedrive.util.Utils;

import java.io.File;
import java.util.List;

/**
 * @author Mr.Yuan
 * @date 2017/4/17
 */
public class ImageAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    public static final int ITEM_FOLDER = 0;
    public static final int ITEM_IMAGE = 1;
    public static final int SHOW_FOLDER = 3;
    public static final int SHOW_IMAGE_ONLY = 4;
    private static final String TAG = "ImageAdapter";
    private Glide glide;
    private RequestManager requestManager;
    private List<Media> data;
    private List<Image> images;
    private int showType;
    private int bigSpanCount;

    public ImageAdapter(Context context, int bigSpanCount) {
        this.bigSpanCount = bigSpanCount;
        glide = Glide.get(context);
        requestManager = Glide.with(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (showType == SHOW_IMAGE_ONLY) {
            return ITEM_IMAGE;
        }
        Media media = data.get(position);
        if (media instanceof Folder) {
            return ITEM_FOLDER;
        } else if (media instanceof Image) {
            return ITEM_IMAGE;
        }
        throw new IllegalArgumentException();
    }

    public int getSpanSize(int position) {
        if (showType == SHOW_FOLDER) {
            Media media = data.get(position);
            return media instanceof Folder ? bigSpanCount : 1;
        }
        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case ITEM_FOLDER:
                view = inflater.inflate(R.layout.item_folder, parent, false);
                break;
            case ITEM_IMAGE:
                view = inflater.inflate(R.layout.item_image, parent, false);
                break;
        }
        view.setOnClickListener(this);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        View root = holder.itemView;
        root.setTag(R.id.layout_position, position);
        switch (itemViewType) {
            case ITEM_FOLDER:
                Folder folder = (Folder) data.get(position);
                TextView folderName = (TextView) root.findViewById(R.id.folderName);
//                TextView folderDesc = (TextView) root.findViewById(R.id.folderDesc);
                folderName.setText(new File(folder.getPath()).getName());
//                folderDesc.setText((folder.getCount() > 0 ? String.format("%d张图片", folder.getCount()) : ""));
                break;
            case ITEM_IMAGE:
                Image image = (Image) (showType == SHOW_FOLDER ? data : images).get(position);
                ImageView imageView = (ImageView) root.findViewById(R.id.imageView);
                requestManager.load(new File(image.getPath())).placeholder(R.drawable.ic_empty_picture)
                        .into(imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return showType == SHOW_FOLDER ? Utils.size(data) : Utils.size(images);
    }

    public int getBigSpanCount() {
        return bigSpanCount;
    }

    public void setBigSpanCount(int bigSpanCount) {
        this.bigSpanCount = bigSpanCount;
        this.notifyDataSetChanged();
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
        this.notifyDataSetChanged();
    }

    public void setData(List<Media> data, List<Image> images) {
        assert data != null;
        assert images != null;
        this.data = data;
        this.images = images;
        this.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(R.id.layout_position);
        int itemViewType = getItemViewType(position);
        List data = (showType == SHOW_FOLDER ? this.data : this.images);
        Log.d(TAG, "itemViewType:" + (itemViewType == ITEM_FOLDER ? "ITEM_FOLDER" : "ITEM_IMAGE"));
        if (itemViewType == ITEM_FOLDER) {
            Folder folder = (Folder) data.get(position);
            Intent intent = new Intent(v.getContext(), ImageFolderActivity.class);
            intent.putExtra(ImageFolderActivity.FOLDER_PATH, folder.getPath());
            v.getContext().startActivity(intent);
        } else if (itemViewType == ITEM_IMAGE) {
            Image image = (Image) data.get(position);
            Context context =v.getContext();
            ActivityOptionsCompat activityOptions =ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity)context,
                    new Pair<View,String>(v.findViewById(R.id.imageView),"image")
            );
            ImageDisplayActivity.showLocalImage(v.getContext(),image,activityOptions.toBundle());
        }
    }
}
