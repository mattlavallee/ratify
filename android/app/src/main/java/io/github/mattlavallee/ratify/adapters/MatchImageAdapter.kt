package io.github.mattlavallee.ratify.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.core.YelpResult

class MatchImageAdapter(private val data: ArrayList<YelpResult>): RecyclerView.Adapter<MatchImageAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.concluded_match_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchImageAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false) as View
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currMatch = data[position]

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
        return R.layout.concluded_group_image_layout
    }

    override fun getItemCount() = data.size
}
