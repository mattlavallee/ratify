package io.github.mattlavallee.ratify.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.adapters.GroupVoteResultsAdapter
import io.github.mattlavallee.ratify.core.DetailedGroup

class ActiveGroupFragment: Fragment() {
    private lateinit var group: DetailedGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var matchesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewLayoutManager: RecyclerView.LayoutManager

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
        groupDescription.text = group.getDescription()
        groupVoteConclusion.text = "Voting ends " + group.getVoteConclusion()

        viewLayoutManager = LinearLayoutManager(context)
        matchesAdapter = GroupVoteResultsAdapter(group.matches)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewLayoutManager
            adapter = matchesAdapter
        }
    }
}

