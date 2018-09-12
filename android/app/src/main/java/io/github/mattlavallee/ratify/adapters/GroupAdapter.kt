package io.github.mattlavallee.ratify.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.R

class GroupAdapter(private val data: ArrayList<Group>): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    class ViewHolder(groupView: View): RecyclerView.ViewHolder(groupView) {
        val groupName: TextView = groupView.findViewById(R.id.group_name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.ViewHolder {
        val groupView: View = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false) as View
        return ViewHolder(groupView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupName.text = data[position].name
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.group_layout
    }

    override fun getItemCount() = data.size
}