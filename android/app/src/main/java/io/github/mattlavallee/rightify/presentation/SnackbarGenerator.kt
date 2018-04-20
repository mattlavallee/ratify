package io.github.mattlavallee.rightify.presentation

import android.graphics.Color
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView

class SnackbarGenerator {
    companion object {
        private fun setTextColor(snack: Snackbar, color: Int, viewId: Int) {
            var text: TextView = snack.view.findViewById(viewId) as TextView
            text.setTextColor(color)
        }

        fun generateSnackbar(view: View?, msg: String): Snackbar? {
            if (view != null) {
                var snack: Snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                setTextColor(snack, Color.CYAN, android.support.design.R.id.snackbar_text)
                return snack
            }
            return null
        }
    }
}