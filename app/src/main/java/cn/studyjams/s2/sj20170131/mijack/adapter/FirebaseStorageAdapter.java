package cn.studyjams.s2.sj20170131.mijack.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.RecyclerViewCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cursor cursor = getItem(position);
        StringBuilder sb = new StringBuilder();
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            sb.append(cursor.getColumnName(i)).append(":").append(cursor.getString(i)).append("\n");
        }
        ((TextView) holder.itemView).setText(sb.toString());
    }
}
