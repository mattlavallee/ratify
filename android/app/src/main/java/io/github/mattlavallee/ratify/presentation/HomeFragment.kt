package io.github.mattlavallee.ratify.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.transition.TransitionInflater
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.adapters.GroupAdapter
import io.github.mattlavallee.ratify.core.Constants
import io.github.mattlavallee.ratify.core.DetailedGroup
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.data.HomeViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface

class HomeFragment : Fragment(), UserAuthInterface {
    private var viewModel: HomeViewModel? = null
    private var joinedGroups: ArrayList<Group> = ArrayList()
    private lateinit var pendingFetchSpinner: ProgressBar
    private lateinit var groupRecyclerView: RecyclerView
    private lateinit var viewLayoutManager: RecyclerView.LayoutManager
    private lateinit var joinedGroupAdapter: RecyclerView.Adapter<*>
    private lateinit var groupSwipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel?.getErrorMessage()?.observe(this, Observer {
            msg -> if (msg?.isEmpty() == false || msg != null) SnackbarGenerator.generateSnackbar(view, msg)
        })
        viewModel?.getGroups()?.observe(this, Observer {
            results ->
                results!!.sortBy { it.voteConclusion }
                joinedGroups.clear()
                joinedGroups.addAll(results)
                joinedGroupAdapter.notifyDataSetChanged()
                groupSwipeRefresh.isRefreshing = false
        })
        viewModel?.getFetchIsPending()?.observe(this, Observer {
            isPending -> if (isPending == true && !groupSwipeRefresh.isRefreshing) pendingFetchSpinner.visibility = View.VISIBLE else pendingFetchSpinner.visibility = View.GONE
        })
        viewModel?.getGroupDetails()?.observe(this, Observer {
            selectedGroup -> launchGroupView(selectedGroup)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_home, container, false)
        val bundle: Bundle? = arguments
        if (bundle?.getBoolean("fetchOnStart") == true) {
            viewModel?.fetch()
        }
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Ratify"
        viewLayoutManager = LinearLayoutManager(context)
        joinedGroupAdapter = GroupAdapter(joinedGroups, viewModel)
        pendingFetchSpinner = view.findViewById(R.id.group_list_spinner)
        groupSwipeRefresh = view.findViewById(R.id.group_swipe_refresh)
        groupRecyclerView = view.findViewById<RecyclerView>(R.id.groups_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewLayoutManager
            adapter = joinedGroupAdapter
        }

        groupSwipeRefresh.setOnRefreshListener {
            viewModel?.fetch()
        }
    }

    private fun launchGroupView(group: DetailedGroup?) {
        if (group == null) {
            SnackbarGenerator.generateSnackbar(view, "Error accessing group!")?.show()
        }

        var groupFragment: Fragment = if (group!!.isConcluded()) ConcludedGroupFragment() else ActiveGroupFragment()

        val bundle = Bundle()
        bundle.putSerializable("group", group)
        groupFragment.arguments = bundle
        val groupTransaction: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        val transitionSet = TransitionSet()
        val fragmentActivity = activity!!
        transitionSet.addTransition(TransitionInflater.from(fragmentActivity.applicationContext).inflateTransition(android.R.transition.explode))
        transitionSet.duration = Constants.TRANSITION_DURATION
        groupFragment.sharedElementEnterTransition = transitionSet
        groupTransaction.replace(R.id.content_container, groupFragment)
        groupTransaction.commit()
    }

    override fun onUserAuthError() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAuthSuccess() {
        viewModel?.fetch()
    }
}