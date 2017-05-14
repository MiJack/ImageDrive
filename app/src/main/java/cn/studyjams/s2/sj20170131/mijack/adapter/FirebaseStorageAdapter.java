package cn.studyjams.s2.sj20170131.mijack.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.entity.FirebaseImage;
import cn.studyjams.s2.sj20170131.mijack.ui.ImageDisplayActivity;

/**
 * @author Mr.Yuan
 * @date 2017/5/7
 */
public class FirebaseStorageAdapter extends FirebaseRecyclerAdapter<FirebaseImage, FirebaseStorageAdapter.StorageHolder> {
    public final static String TAG = "FirebaseStorageAdapter";

    public FirebaseStorageAdapter(Query ref) {
        super(FirebaseImage.class, R.layout.item_driver, StorageHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(StorageHolder storageHolder, FirebaseImage firebaseImage, int i) {
        storageHolder.loadImage(firebaseImage);
    }


    public static class StorageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView image;
        private FirebaseImage firebaseImage;

        public StorageHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            image.setOnClickListener(this);
        }

        private static final String TAG = "StorageHolder";

        public void loadImage(FirebaseImage firebaseImage) {
            this.firebaseImage = firebaseImage;
            String url = firebaseImage.getDownloadUrl();
            Log.d(TAG, "loadImage: url" + url);
            Glide.with(itemView.getContext()).load(url).into(image);
        }

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.image){
                ImageDisplayActivity.showFirebaseImage(v.getContext(),firebaseImage);
            }
        }
    }
}
