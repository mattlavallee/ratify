package io.github.mattlavallee.rightify

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import io.github.mattlavallee.rightify.presentation.JoinView
import kotlinx.android.synthetic.main.activity_rightify.*

class RightifyActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var joinViewModel: JoinView? = null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.nav_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_join -> {
                joinViewModel?.resetCodeInput()
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

    private fun initJoinView() {
        val joinEditText = findViewById<EditText>(R.id.join_code)
        val joinButton = findViewById<Button>(R.id.btn_join)
        joinViewModel = JoinView(joinEditText, joinButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rightify)
        initJoinView()

        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()
        initJoinView()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN;
        } else {
            super.onBackPressed();
        }
    }
}
