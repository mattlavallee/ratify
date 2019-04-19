package io.github.mattlavallee.ratify

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.transition.Slide
import android.support.transition.TransitionInflater
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import io.github.mattlavallee.ratify.core.Constants
import io.github.mattlavallee.ratify.data.GroupViewModel
import io.github.mattlavallee.ratify.presentation.CreateFragment
import io.github.mattlavallee.ratify.presentation.HomeFragment
import io.github.mattlavallee.ratify.presentation.JoinView
import io.github.mattlavallee.ratify.presentation.interfaces.FragmentSwitchInterface
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface
import kotlinx.android.synthetic.main.activity_ratify.*
import java.util.Arrays

class RatifyActivity : AppCompatActivity(), FragmentSwitchInterface {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var joinViewModel: JoinView? = null
    private var groupViewModel: GroupViewModel? = null
    private var selectedFragment: Fragment? = null
    private var mainContainerLayout: ConstraintLayout? = null
    private var userAuth: FirebaseAuth? = null
    private var priorFragmentTitle: String? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val callback: () -> Unit = {
            val previousFragment = selectedFragment
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        val bundle = Bundle()
                        bundle.putBoolean("fetchOnStart", true)
                        selectedFragment?.arguments = bundle
                    }
                }
                R.id.navigation_join -> {
                    joinViewModel?.resetCodeInput()
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    this.priorFragmentTitle = this.title.toString()
                    this.title = "Join a Group"
                }
                R.id.navigation_create -> {
                    selectedFragment = CreateFragment()
                }
            }

            setFragmentTransitions(previousFragment, selectedFragment)
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content_container, selectedFragment!!)
                    .addToBackStack(null)
                    .commit()
        }

        if (selectedFragment is CreateFragment) {
            val switchingToJoin: Boolean = item.itemId == R.id.navigation_join
            val navigationUpdateCallback: () -> Unit = {
                if (navigation.selectedItemId != item.itemId && !switchingToJoin) {
                    navigation.selectedItemId = item.itemId
                }
            }
            (selectedFragment as CreateFragment).confirmCancel(callback, navigationUpdateCallback, switchingToJoin)
            return@OnNavigationItemSelectedListener false
        }

        callback()
        if (item.itemId == R.id.navigation_join) {
            return@OnNavigationItemSelectedListener false
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun onResetToHomeFragment(code: String) {
        if (code.isEmpty()) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            if (selectedFragment is HomeFragment) {
                (selectedFragment as HomeFragment).onUserAuthSuccess()
            }
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            Handler().postDelayed({
                imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
            }, 500)
        }

        navigation.selectedItemId = R.id.navigation_home
        if (!code.isEmpty()) {
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType("text/plain")
                    .setChooserTitle("Share Group Code")
                    .setText("Join my group on Ratify with code: $code")
                    .startChooser()
        }
    }

    private fun initJoinView() {
        if (joinViewModel != null) {
            return
        }

        val baseView = findViewById<View>(android.R.id.content)
        joinViewModel = JoinView(baseView, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratify)

        mainContainerLayout = findViewById(R.id.mainContainer)
        userAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        var shouldLaunchLoginScreen = true
        val homeFragmentParams = Bundle()
        if (userAuth?.currentUser != null) {
            shouldLaunchLoginScreen = false
            homeFragmentParams.putBoolean("fetchOnStart", true)
        }

        // initialize the default home fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (selectedFragment == null) {
            selectedFragment = HomeFragment()
            if (!shouldLaunchLoginScreen) {
                selectedFragment?.arguments = homeFragmentParams
            }

            setFragmentTransitions(null, selectedFragment)
            transaction.replace(R.id.content_container, selectedFragment as Fragment).commit()
        }

        // initialize the join bottomsheet
        initJoinView()

        if (groupViewModel == null) {
            groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
            groupViewModel?.getGroupCode()?.observe(this, Observer {
                code ->
                    if (code != null) {
                        val previous = selectedFragment
                        val groupCreatedTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                        selectedFragment = HomeFragment()
                        selectedFragment?.arguments = Bundle()
                        (selectedFragment?.arguments as Bundle).putBoolean("fetchOnStart", true)
                        setFragmentTransitions(previous, selectedFragment)
                        groupCreatedTransaction.replace(R.id.content_container, selectedFragment as Fragment)
                                .addToBackStack(null)
                                .commit()
                    }
            })
        }

        // TODO: move this to JoinViewModel
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (!shouldLaunchLoginScreen) {
            toggleDisplays(true)
        } else {
            launchLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        initJoinView()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            if (this.priorFragmentTitle != null) {
                this.title = this.priorFragmentTitle
                this.priorFragmentTitle = null
            }
        } else if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            if (selectedFragment is CreateFragment) {
                val callback: () -> Unit = { supportFragmentManager.popBackStack() }
                (selectedFragment as CreateFragment).confirmCancel(callback)
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    private fun launchLogin() {
        val providers: List<AuthUI.IdpConfig> = Arrays.asList(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // create and launch the sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.ratify_logo)
                        .setTheme(R.style.RatifyTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setAvailableProviders(providers)
                        .build(),
                Constants.RC_SIGN_IN
        )
    }

    private fun toggleDisplays(showFragment: Boolean) {
        if (showFragment) {
            mainContainerLayout?.visibility = View.VISIBLE
        } else {
            mainContainerLayout?.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedFragment?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                toggleDisplays(true)
                (selectedFragment as UserAuthInterface?)?.onUserAuthSuccess()
            } else {
                // sign in failed
                toggleDisplays(false)
            }
        }
    }

    private fun setFragmentTransitions(previous: Fragment?, current: Fragment?) {
        // set a fade out exit transition
        val slideTran = Slide(Gravity.RIGHT)
        slideTran.duration = Constants.TRANSITION_SLIDE_DURATION
        previous?.exitTransition = slideTran

        // set a move enter transition
        val transitionSet = TransitionSet()
        transitionSet.addTransition(TransitionInflater.from(applicationContext).inflateTransition(android.R.transition.slide_left))
        transitionSet.duration = Constants.TRANSITION_DURATION
        current?.sharedElementEnterTransition = transitionSet
    }
}
