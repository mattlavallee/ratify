package io.github.mattlavallee.ratify.adapters

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.core.YelpResult

class GroupVoteResultsAdapter(private val data: ArrayList<YelpResult>): RecyclerView.Adapter<GroupVoteResultsAdapter.ViewHolder>() {
    class ViewHolder(matchView: View): RecyclerView.ViewHolder(matchView) {
        val name: TextView = matchView.findViewById(R.id.match_details_name)
        val location: TextView = matchView.findViewById(R.id.match_details_location)
        val image: ImageView = matchView.findViewById(R.id.match_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupVoteResultsAdapter.ViewHolder {
        val matchView = LayoutInflater.from(parent.context).inflate(viewType, parent, false) as View
        return ViewHolder(matchView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currMatch = data[position]

        holder.name.text = currMatch.name
        holder.location.text = currMatch.address

        holder.image.setImageDrawable(null) //force a redraw
        holder.image.setImageURI(Uri.parse(currMatch.businessImage))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.active_group_match_layout
    }

    override fun getItemCount() = data.size
}

/*
class GroupAdapter(private val data: ArrayList<Group>, private val viewModel: HomeViewModel?) : RecyclerView.Adapter<GroupAdapter.ViewHolder>()
        override fun getItemViewType(position: Int): Int {
        return R.layout.group_layout
    }

    override fun getItemCount() = data.size
}
 */