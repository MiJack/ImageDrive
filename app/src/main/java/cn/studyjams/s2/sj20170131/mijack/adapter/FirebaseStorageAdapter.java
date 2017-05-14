package cn.studyjams.s2.sj20170131.mijack.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.widget.RecyclerViewCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.database.DatabaseSQLiteOpenHelper;

/**
 * @author Mr.Yuan
 * @date 2017/5/7
 */
public class FirebaseStorageAdapter extends RecyclerViewCursorAdapter {
    public FirebaseStorageAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driver, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cursor cursor = getItem(position);
        ConstraintLayout constraintLayout = (ConstraintLayout) holder.itemView.findViewById(R.id.container);
        String name = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteOpenHelper.Database.COLUMNS_NAME));
        int width = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteOpenHelper.Database.COLUMNS_WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(DatabaseSQLiteOpenHelper.Database.COLUMNS_HEIGHT));
        String url = cursor.getString(cursor.getColumnIndex(DatabaseSQLiteOpenHelper.Database.COLUMNS_URL));
        TextView textView = (TextView) holder.itemView.findViewById(R.id.name);
        textView.setText(name);
        ImageView image = (ImageView) holder.itemView.findViewById(R.id.image);
        ConstraintSet constraintSet = new ConstraintSet();
        String ratio = String.format("W,%d:%d", height, width);
        constraintSet.setDimensionRatio(R.id.image, ratio);
//        constraintSet.connect(R.id.image, ConstraintSet.TOP, R.id.name, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.image, ConstraintSet.TOP, R.id.name, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.image, ConstraintSet.LEFT, R.id.container, ConstraintSet.LEFT);
        constraintSet.connect(R.id.image, ConstraintSet.RIGHT, R.id.container, ConstraintSet.RIGHT);
        constraintSet.applyTo(constraintLayout);
        Glide.with(holder.itemView.getContext()).load(url).into(image);
    }
}
