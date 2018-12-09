package io.github.mattlavallee.ratify.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.data.HomeViewModel
import java.text.SimpleDateFormat

class GroupAdapter(private val data: ArrayList<Group>, private val viewModel: HomeViewModel?) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    private val conclusionFormat: String = "MMM d yyyy h:mm a"
    private val conclusionFormatter: SimpleDateFormat = SimpleDateFormat(conclusionFormat)

    class ViewHolder(groupView: View) : RecyclerView.ViewHolder(groupView) {
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
        val currGroup = data[position]
        holder.name.text = currGroup.name
        holder.description.text = currGroup.description
        var totalParticipants = currGroup.participants
        var participantText = " participants"
        if (totalParticipants <= 0) {
            // if it's showing up in someone's view, there's got to be at least one user
            totalParticipants = 1
            participantText = " participant"
        }
        holder.participants.text = "(" + totalParticipants + participantText + ")"
        holder.code.text = currGroup.id
        holder.expiration.text = conclusionFormatter.format(currGroup.voteConclusion)
        holder.itemView.tag = currGroup.id

        if (currGroup.isConcluded()) {
            holder.name.setTextColor(Color.LTGRAY)
            holder.description.setTextColor(Color.LTGRAY)
            holder.participants.setTextColor(Color.LTGRAY)
            holder.code.setTextColor(Color.LTGRAY)
            holder.expiration.setTextColor(Color.LTGRAY)
        }

        initGroupTapAction(holder.itemView)
    }

    private fun initGroupTapAction(groupView: View) {
        groupView.setOnClickListener {
            viewModel?.fetchGroup(groupView.tag as String)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.group_layout
    }

    override fun getItemCount() = data.size
}