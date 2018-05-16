package io.github.mattlavallee.ratify.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.data.HomeViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface

class HomeFragment : Fragment(), UserAuthInterface {
    private var viewModel: HomeViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = HomeViewModel(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStop() {
        super.onStop()
        viewModel?.closeConnections()
    }

    override fun onUserAuthError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAuthSuccess() {
        viewModel?.fetch()
    }
}