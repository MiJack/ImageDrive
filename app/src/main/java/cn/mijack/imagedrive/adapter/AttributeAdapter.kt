package cn.mijack.imagedrive.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.entity.Attribute
import org.w3c.dom.Attr

/**
 * @author admin
 * @date 2017/8/30
 */
class AttributeAdapter<V> : Adapter<RecyclerView.ViewHolder>() {
    var list = ArrayList<Attribute<V>>()
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        var data: Attribute<V> = list.get(position)
        (holder?.itemView?.findViewById<TextView>(R.id.attribute))?.text = data.name
        (holder?.itemView?.findViewById<TextView>(R.id.attributeValue))?.text = data?.value?.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var view: View = LayoutInflater.from(parent!!.context).inflate(R.layout.item_exit_interface, parent, false);
        return ViewHolder(view); }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemCount(): Int = list.size

    fun setList(list: List<Attribute<V>>) {
        this.list.clear()
        this.list.addAll(list)
        this.notifyDataSetChanged()
    }
}
