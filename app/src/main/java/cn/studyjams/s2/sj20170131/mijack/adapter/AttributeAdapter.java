package cn.studyjams.s2.sj20170131.mijack.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.entity.Attribute;

/**
 * @author Mr.Yuan
 * @date 2017/5/21
 */
public class AttributeAdapter<V> extends RecyclerView.Adapter {
    List<Attribute<V>> list = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exit_interface, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Attribute data = list.get(position);
        ((TextView) holder.itemView.findViewById(R.id.attribute)).setText(data.getName());
        ((TextView) holder.itemView.findViewById(R.id.attributeValue)).setText(data.getValue()!=null?data.getValue().toString():"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<Attribute<V>> list) {
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }
}
