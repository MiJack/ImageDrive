package cn.studyjams.s220170131.mijack.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/29
 */
public class ExitAttributeAdapter extends RecyclerView.Adapter {
    private List<String> list;

    public ExitAttributeAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exit_interface, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String data = list.get(position);
        String[] split = data.split(":");
        ((TextView) holder.itemView.findViewById(R.id.attribute)).setText(split[0]);
        ((TextView) holder.itemView.findViewById(R.id.attributeValue)).setText(split[1]);
    }

    @Override
    public int getItemCount() {
        return Utils.size(list);
    }
}
