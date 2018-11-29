package io.github.mattlavallee.ratify.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.core.YelpResult

class PreviewResultsAdapter(private val data: ArrayList<YelpResult>) : RecyclerView.Adapter<PreviewResultsAdapter.ViewHolder>() {
    class ViewHolder(previewView: View) : RecyclerView.ViewHolder(previewView) {
        val name: TextView = previewView.findViewById(R.id.preview_name)
        val address: TextView = previewView.findViewById(R.id.preview_address)
        val rating: TextView = previewView.findViewById(R.id.preview_rating)
        val price: TextView = previewView.findViewById(R.id.preview_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewResultsAdapter.ViewHolder {
        val previewView: View = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false) as View
        return ViewHolder(previewView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currPreviewResult = data[position]
        holder.name.text = currPreviewResult.name
        holder.address.text = currPreviewResult.address.replace("\n", ", ")
        holder.rating.text = "Rating: " + currPreviewResult.rating.toString()
        holder.price.text = "Price: " + currPreviewResult.price
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.preview_results_item_layout
    }

    override fun getItemCount() = data.size
}