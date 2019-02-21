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
import io.github.mattlavallee.ratify.adapters.HorizontalOverlapDecoration
import io.github.mattlavallee.ratify.adapters.MatchImageAdapter
import io.github.mattlavallee.ratify.core.DetailedGroup
import io.github.mattlavallee.ratify.core.UserVote
import io.github.mattlavallee.ratify.core.YelpResult

class ConcludedGroupFragment : Fragment() {
    private lateinit var group: DetailedGroup
    private lateinit var concludedGoupText: TextView
    private lateinit var concludedImagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: RecyclerView.Adapter<*>
    private lateinit var imagesViewLayoutManager: RecyclerView.LayoutManager

    private lateinit var matchesAdapter: RecyclerView.Adapter<*>
    private lateinit var matchesViewLayoutManager: RecyclerView.LayoutManager
    private lateinit var matchesRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val bundle: Bundle? = arguments
        group = bundle?.getSerializable("group") as DetailedGroup

        return inflater.inflate(R.layout.fragment_group_concluded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        concludedImagesRecyclerView = view.findViewById(R.id.winner_images_view)
        concludedGoupText = view.findViewById(R.id.concluded_winner_text)
        matchesRecyclerView = view.findViewById(R.id.concluded_winner_details)

        activity?.title = group.getName()

        val winnerMatches = this.getGroupWinners()
        initializeWinnerImageCarousel(winnerMatches)
        initializeWinnerDetails(winnerMatches)

        if (winnerMatches.size == 1) {
            concludedGoupText.text = "Enjoy your time at:"
        } else {
            concludedGoupText.text = "It's a tie. Time to duke it out!"
        }
    }

    private fun getGroupWinners(): ArrayList<YelpResult> {
        val resultTally: MutableMap<String, Int> = mutableMapOf()
        val winners: ArrayList<String> = ArrayList()
        if (group.allUserVotes == null) {
            return ArrayList()
        }

        for ((_, userVotes) in group.allUserVotes!!) {
            for ((matchId, userVote) in userVotes) {
                if (!resultTally.containsKey(matchId)) {
                    resultTally[matchId] = 0
                }

                if (userVote.value == UserVote.YES) {
                    resultTally[matchId] = resultTally[matchId]!!.plus(1)
                } else if (userVote.value == UserVote.NO) {
                    resultTally[matchId] = resultTally[matchId]!!.minus(1)
                }
            }
        }

        val maxVoteTally = resultTally.values.max()
        for((matchId, tally) in resultTally) {
            if (tally == maxVoteTally) {
                winners.add(matchId)
            }
        }

        val winnerMatches = group.matches.filter { winners.contains(it.id) }
        return ArrayList(winnerMatches)
    }

    private fun initializeWinnerImageCarousel(winners: ArrayList<YelpResult>) {
        imagesAdapter = MatchImageAdapter(winners)

        // create the layout manager, set to render horizontally
        // reverse the layout and stack from end so that the images will overlap with the first image on top
        imagesViewLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        (imagesViewLayoutManager as LinearLayoutManager).reverseLayout = true
        (imagesViewLayoutManager as LinearLayoutManager).stackFromEnd = true

        // item decoration will set items to overlap each other
        concludedImagesRecyclerView.addItemDecoration(HorizontalOverlapDecoration(winners.size))

        // dynamically set the size of the recycler view based on how many winners there are.
        // this sizing wll essentially horizontally center the items on the screen
        val layoutParams = concludedImagesRecyclerView.layoutParams
        val density = context!!.resources.displayMetrics.density
        layoutParams.width = Math.round(((winners.size * 100) - ((winners.size - 1) * 40)) * density)
        concludedImagesRecyclerView.layoutParams = layoutParams

        concludedImagesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = imagesViewLayoutManager
            adapter = imagesAdapter
        }
    }

    private fun initializeWinnerDetails(winners: ArrayList<YelpResult>) {
        matchesViewLayoutManager = LinearLayoutManager(context)
        matchesAdapter = GroupVoteResultsAdapter(winners, group.votes, true)
        matchesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = matchesViewLayoutManager
            adapter = matchesAdapter
        }
    }
}
