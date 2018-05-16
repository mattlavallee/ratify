package io.github.mattlavallee.ratify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.github.mattlavallee.ratify.presentation.CreateFragment
import io.github.mattlavallee.ratify.presentation.HomeFragment
import io.github.mattlavallee.ratify.presentation.JoinView
import io.github.mattlavallee.ratify.presentation.SnackbarGenerator
import kotlinx.android.synthetic.main.activity_ratify.*
import java.util.*

class RatifyActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var joinViewModel: JoinView? = null
    private var selectedFragment: Fragment? = null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                selectedFragment = HomeFragment()
            }
            R.id.navigation_join -> {
                joinViewModel?.resetCodeInput()
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                return@OnNavigationItemSelectedListener false
            }
            R.id.navigation_create -> {
                selectedFragment = CreateFragment()
            }
        }

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_container, selectedFragment)
        transaction.commit()
        return@OnNavigationItemSelectedListener true
    }

    private fun initJoinView() {
        val baseView = findViewById<View>(android.R.id.content)
        val joinEditText = findViewById<EditText>(R.id.join_code)
        val joinButton = findViewById<Button>(R.id.btn_join)
        joinViewModel = JoinView(joinEditText, joinButton, baseView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratify)

        //initialize the default home fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_container, HomeFragment())
        transaction.commit()

        //initialize the join bottomsheet
        initJoinView()

        //TODO: move this to JoinView
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var providers: List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        //create and launch the sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                  .createSignInIntentBuilder()
                  .setAvailableProviders(providers)
                  .build(),
            RC_SIGN_IN)
    }

    override fun onResume() {
        super.onResume()
        initJoinView()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedFragment?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            var response: IdpResponse = IdpResponse.fromResultIntent(data) as IdpResponse
            if (resultCode == Activity.RESULT_OK) {
                var user: FirebaseUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
                val baseView = findViewById<View>(android.R.id.content)
                SnackbarGenerator.generateSnackbar(baseView, "User: " + user.displayName)?.show()
            } else {
                //sign in failed
                val baseView = findViewById<View>(android.R.id.content)
                SnackbarGenerator.generateSnackbar(baseView, "Error signing in...")?.show()
            }
        }
    }
}
