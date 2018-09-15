package io.github.mattlavallee.ratify.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.R
import java.text.SimpleDateFormat

class GroupAdapter(private val data: ArrayList<Group>): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private val conclusionFormat: String = "MMM d yyyy h:mm a"
    private val conclusionFormatter: SimpleDateFormat = SimpleDateFormat(conclusionFormat)

    class ViewHolder(groupView: View): RecyclerView.ViewHolder(groupView) {
        val name: TextView = groupView.findViewById(R.id.group_name) as TextView
        val participants: TextView = groupView.findViewById(R.id.group_participants) as TextView
        val description: TextView = groupView.findViewById(R.id.group_description) as TextView
        val code: TextView = groupView.findViewById(R.id.group_code) as TextView
        val expiration: TextView = groupView.findViewById(R.id.group_expiration) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.ViewHolder {
        val groupView: View = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false) as View
        return ViewHolder(groupView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = data[position].name
        holder.description.text = data[position].description
        var totalParticipants = data[position].participants
        var participantText = " participants"
        if (totalParticipants <= 0) {
            //if it's showing up in someone's view, there's got to be at least one user
            totalParticipants = 1
            participantText = " participant"
        }
        holder.participants.text = "(" + totalParticipants + participantText + ")"
        holder.code.text = data[position].id
        holder.expiration.text = conclusionFormatter.format(data[position].voteConclusion)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.group_layout
    }

    override fun getItemCount() = data.size
}