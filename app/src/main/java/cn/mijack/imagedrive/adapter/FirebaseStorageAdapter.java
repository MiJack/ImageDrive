package cn.mijack.imagedrive.adapter;

import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import cn.mijack.imagedrive.R;
import cn.mijack.imagedrive.entity.FirebaseImage;
import cn.mijack.imagedrive.ui.ImageDisplayActivity;

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
    protected void populateViewHolder(StorageHolder storageHolder, FirebaseImage firebaseImage, int position) {
        DatabaseReference reference = getRef(position);
        storageHolder.loadImage(firebaseImage,reference);
    }


    public static class StorageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView image;
        private FirebaseImage firebaseImage;
        private DatabaseReference reference;

        public StorageHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            image.setOnClickListener(this);
        }

        private static final String TAG = "StorageHolder";

        public void loadImage(FirebaseImage firebaseImage, DatabaseReference reference) {
            this.firebaseImage = firebaseImage;
            this.reference = reference;
            String url = firebaseImage.getDownloadUrl();
            Log.d(TAG, "loadImage: url" + url);
            Glide.with(itemView.getContext()).load(url).into(image);
        }

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.image){
                ActivityOptionsCompat activityOptions =ActivityOptionsCompat
                        .makeSceneTransitionAnimation((Activity)v.getContext(),v,"image");
                ImageDisplayActivity.showFirebaseImage(v.getContext(),firebaseImage,reference.toString(),activityOptions.toBundle());
            }
        }
    }
}
