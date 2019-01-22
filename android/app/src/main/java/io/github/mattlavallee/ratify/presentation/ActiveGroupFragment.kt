package io.github.mattlavallee.ratify.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.adapters.GroupVoteResultsAdapter
import io.github.mattlavallee.ratify.core.DetailedGroup
import io.github.mattlavallee.ratify.core.UserVoteTask
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ActiveGroupFragment : Fragment() {
    private lateinit var group: DetailedGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var matchesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewLayoutManager: RecyclerView.LayoutManager
    private lateinit var scheduleFuture: ScheduledFuture<*>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val bundle: Bundle? = arguments
        group = bundle?.getSerializable("group") as DetailedGroup

        return inflater.inflate(R.layout.fragment_group_active, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = group.getName()

        val groupDescription = view.findViewById<TextView>(R.id.current_group_description)
        val groupVoteConclusion = view.findViewById<TextView>(R.id.current_group_vote_conclusion)
        recyclerView = view.findViewById(R.id.current_group_matches_recycler_view)
        val itemDecoration = DividerItemDecoration(context, VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        groupDescription.text = group.getDescription()
        groupVoteConclusion.text = "Voting ends " + group.getVoteConclusion()

        viewLayoutManager = LinearLayoutManager(context)
        matchesAdapter = GroupVoteResultsAdapter(group.matches, group.votes)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewLayoutManager
            adapter = matchesAdapter
        }

        val executor = ScheduledThreadPoolExecutor(1)
        this.scheduleFuture = executor.scheduleWithFixedDelay(UserVoteTask(this.group), 0, 30000, TimeUnit.MILLISECONDS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.scheduleFuture.cancel(true)

        val executor = ScheduledThreadPoolExecutor(1)
        executor.execute(UserVoteTask(this.group))
    }
}
