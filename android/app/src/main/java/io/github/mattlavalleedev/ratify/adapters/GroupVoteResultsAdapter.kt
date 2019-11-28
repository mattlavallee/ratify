package io.github.mattlavalleedev.ratify.adapters

import android.content.Intent
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.github.mattlavalleedev.ratify.R
import io.github.mattlavalleedev.ratify.core.UserVote
import io.github.mattlavalleedev.ratify.core.YelpResult

class GroupVoteResultsAdapter(private val data: ArrayList<YelpResult>,
                              private val voteState: Map<String, UserVote>,
                              private val callingActivity: FragmentActivity,
                              private val isConcluded: Boolean = false) : RecyclerView.Adapter<GroupVoteResultsAdapter.ViewHolder>() {
    class ViewHolder(matchView: View) : RecyclerView.ViewHolder(matchView) {
        val name: TextView = matchView.findViewById(R.id.match_details_name)
        val location: TextView = matchView.findViewById(R.id.match_details_location)
        val image: ImageView = matchView.findViewById(R.id.match_image)
        val ratingStars: ImageView = matchView.findViewById(R.id.match_rating_stars)
        val ratingText: TextView = matchView.findViewById(R.id.match_rating_text)
        val price: TextView = matchView.findViewById(R.id.match_price)
        val positiveVoteBtn: ImageButton = matchView.findViewById(R.id.match_details_vote_positive)
        val negativeVoteBtn: ImageButton = matchView.findViewById(R.id.match_details_vote_negative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val matchView = LayoutInflater.from(parent.context).inflate(viewType, parent, false) as View
        return ViewHolder(matchView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currMatch = data[position]

        holder.name.text = currMatch.name
        holder.location.text = currMatch.address
        val ratingText = "%.1f".format(currMatch.rating)
        when(ratingText) {
            "5.0" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_5)
            "4.5" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_4_half)
            "4.0" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_4)
            "3.5" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_3_half)
            "3.0" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_3)
            "2.5" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_2_half)
            "2.0" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_2)
            "1.5" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_1_half)
            "1.0" -> holder.ratingStars.setImageResource(R.mipmap.yelp_stars_1)
            else -> { holder.ratingStars.setImageResource(R.mipmap.yelp_stars_0) }
        }
        holder.ratingText.text = ratingText
        holder.price.text = currMatch.price

        if (!isConcluded) {
            if (this.voteState[currMatch.id]?.value == UserVote.YES) {
                holder.positiveVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border_positive)
                holder.negativeVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border)
            } else if (this.voteState[currMatch.id]?.value == UserVote.NO) {
                holder.negativeVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border_negative)
                holder.positiveVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border)
            }
        } else {
            holder.positiveVoteBtn.visibility = View.GONE
            holder.negativeVoteBtn.visibility = View.GONE
        }

        if (currMatch.businessImage.isNotEmpty()) {
            Picasso.get()
                .load(currMatch.businessImage)
                .placeholder(R.drawable.ic_cloud_off_16dp)
                .fit()
                .centerCrop()
                .into(holder.image)
        }

        if (isConcluded) {
            return
        }

        holder.positiveVoteBtn.setOnClickListener {
            if (this.voteState.containsKey(currMatch.id)) {
                this.voteState[currMatch.id]?.updateVote(UserVote.YES)
                holder.positiveVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border_positive)
                holder.negativeVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border)
            }
        }

        holder.negativeVoteBtn.setOnClickListener {
            if (this.voteState.containsKey(currMatch.id)) {
                this.voteState[currMatch.id]?.updateVote(UserVote.NO)
                holder.positiveVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border)
                holder.negativeVoteBtn.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_button_border_negative)
            }
        }

        holder.image.setOnClickListener {
            val locationUri = Uri.parse("geo:0,0?q=" + currMatch.address)
            val mapIntent = Intent(Intent.ACTION_VIEW, locationUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(callingActivity.packageManager) != null) {
                callingActivity.startActivity(mapIntent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.active_group_match_layout
    }

    override fun getItemCount() = data.size
}
