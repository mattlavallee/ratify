package io.github.mattlavallee.rightify

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_rightify.*

class RightifyActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.nav_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_join -> {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_create -> {
                message.setText(R.string.nav_create)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rightify)

        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN;
        } else {
            super.onBackPressed();
        }
    }
}
