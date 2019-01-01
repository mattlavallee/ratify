package io.github.mattlavallee.ratify.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.core.YelpResult

class GroupVoteResultsAdapter(private val data: ArrayList<YelpResult>) : RecyclerView.Adapter<GroupVoteResultsAdapter.ViewHolder>() {
    class ViewHolder(matchView: View) : RecyclerView.ViewHolder(matchView) {
        val name: TextView = matchView.findViewById(R.id.match_details_name)
        val location: TextView = matchView.findViewById(R.id.match_details_location)
        val image: ImageView = matchView.findViewById(R.id.match_image)
        val rating: RatingBar = matchView.findViewById(R.id.match_rating)
        val ratingText: TextView = matchView.findViewById(R.id.match_rating_text)
        val price: TextView = matchView.findViewById(R.id.match_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupVoteResultsAdapter.ViewHolder {
        val matchView = LayoutInflater.from(parent.context).inflate(viewType, parent, false) as View
        return ViewHolder(matchView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currMatch = data[position]

        holder.name.text = currMatch.name
        holder.location.text = currMatch.address
        holder.rating.rating = currMatch.rating.toFloat()
        holder.ratingText.text = "%.1f".format(currMatch.rating)
        holder.price.text = currMatch.price

        if (currMatch.businessImage.isNotEmpty()) {
            Picasso.get()
                .load(currMatch.businessImage)
                .placeholder(R.drawable.ic_cloud_off_16dp)
                .fit()
                .centerCrop()
                .into(holder.image)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.active_group_match_layout
    }

    override fun getItemCount() = data.size
}