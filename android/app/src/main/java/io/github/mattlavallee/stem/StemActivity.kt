package io.github.mattlavallee.stem

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_stem.*

class StemActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.nav_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_join -> {
                message.setText(R.string.nav_join)
                return@OnNavigationItemSelectedListener true
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
        setContentView(R.layout.activity_stem)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
