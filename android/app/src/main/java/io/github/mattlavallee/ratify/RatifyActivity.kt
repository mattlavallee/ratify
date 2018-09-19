package io.github.mattlavallee.ratify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.github.mattlavallee.ratify.core.Constants
import io.github.mattlavallee.ratify.presentation.CreateFragment
import io.github.mattlavallee.ratify.presentation.HomeFragment
import io.github.mattlavallee.ratify.presentation.JoinView
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface
import kotlinx.android.synthetic.main.activity_ratify.*
import java.util.*

class RatifyActivity : AppCompatActivity() {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var joinViewModel: JoinView? = null
    private var selectedFragment: Fragment? = null
    private var mainContainerLayout: ConstraintLayout? = null
    private var splashScreenLayout: ConstraintLayout? = null
    private var signInButton: Button? = null
    private var userAuth: FirebaseAuth? = null

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
        joinViewModel = JoinView(baseView, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratify)

        mainContainerLayout = findViewById(R.id.mainContainer)
        splashScreenLayout = findViewById(R.id.splash_screen_layout)
        signInButton = findViewById(R.id.sign_in)
        userAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        var launchLogin = true
        val homeFragmentParams = Bundle()
        if (userAuth?.currentUser != null) {
            launchLogin = false
            homeFragmentParams.putBoolean("fetchOnStart", true)
        }

        //initialize the default home fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        selectedFragment = HomeFragment()
        if (!launchLogin) {
            selectedFragment?.arguments = homeFragmentParams
        }
        transaction.replace(R.id.content_container, selectedFragment)
        transaction.commit()

        //initialize the join bottomsheet
        initJoinView()

        signInButton?.setOnClickListener {
            launchLogin()
        }

        //TODO: move this to JoinViewModel
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (!launchLogin) {
            toggleDisplays(true)
            return
        }
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

    private fun launchLogin() {
        val providers: List<AuthUI.IdpConfig> = Arrays.asList(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        //create and launch the sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                Constants.RC_SIGN_IN
        )
    }

    private fun toggleDisplays(showFragment: Boolean) {
        if (showFragment) {
            splashScreenLayout?.visibility = View.GONE
            mainContainerLayout?.visibility = View.VISIBLE
        } else {
            splashScreenLayout?.visibility = View.VISIBLE
            mainContainerLayout?.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedFragment?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                toggleDisplays(true)
                (selectedFragment as UserAuthInterface?)?.onUserAuthSuccess()
            } else {
                //sign in failed
                toggleDisplays(false)
            }
        }
    }
}
