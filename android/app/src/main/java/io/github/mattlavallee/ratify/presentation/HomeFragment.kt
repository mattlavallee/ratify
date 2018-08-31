package io.github.mattlavallee.ratify.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.data.HomeViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface

class HomeFragment : Fragment(), UserAuthInterface {
    private var viewModel: HomeViewModel? = null
    private var pendingFetchSpinner: ProgressBar? = null
    private var dummyView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel?.getErrorMessage()?.observe(this, Observer{
            msg -> if (msg?.isEmpty() == false || msg != null) SnackbarGenerator.generateSnackbar(view, msg!!)
        })
        viewModel?.getGroups()?.observe(this, Observer{
            results -> dummyView?.text = results?.size.toString()
        })
        viewModel?.getFetchIsPending()?.observe(this, Observer{
            isPending -> if (isPending == true) pendingFetchSpinner?.visibility = View.VISIBLE else pendingFetchSpinner?.visibility = View.GONE
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pendingFetchSpinner = view.findViewById(R.id.group_list_spinner)
        dummyView = view.findViewById(R.id.testData)
        dummyView?.text = "initialized!"
    }

    override fun onUserAuthError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAuthSuccess() {
        viewModel?.fetch()
    }
}