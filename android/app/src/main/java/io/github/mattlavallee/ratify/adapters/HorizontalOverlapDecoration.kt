package io.github.mattlavallee.ratify.adapters

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class HorizontalOverlapDecoration(private val size: Int): RecyclerView.ItemDecoration() {
    private val horizontalOverlap = -40

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var density = parent.context?.resources?.displayMetrics?.density
        if (density == null) {
            density = 1f
        }

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == size - 1) {
            return
        }
        outRect.set(Math.round(horizontalOverlap * density), 0, 0, 0)
    }
}
