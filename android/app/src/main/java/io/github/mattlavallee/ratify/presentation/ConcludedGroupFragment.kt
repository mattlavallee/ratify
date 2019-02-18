package io.github.mattlavallee.ratify.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.adapters.HorizontalOverlapDecoration
import io.github.mattlavallee.ratify.adapters.MatchImageAdapter
import io.github.mattlavallee.ratify.core.DetailedGroup
import io.github.mattlavallee.ratify.core.UserVote

class ConcludedGroupFragment : Fragment() {
    lateinit var group: DetailedGroup
    lateinit var concludedImagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: RecyclerView.Adapter<*>
    private lateinit var imagesViewLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val bundle: Bundle? = arguments
        group = bundle?.getSerializable("group") as DetailedGroup

        return inflater.inflate(R.layout.fragment_group_concluded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        concludedImagesRecyclerView = view.findViewById(R.id.winner_images_view)

        activity?.title = group.getName()

        val winners = this.getGroupWinners()
        initializeWinnerImageCarousel(winners)

        //TODO: it shouldn't be possible for winner set to be 0, but better check just in case
        //
        //    Results
        //  (pic) (pic)...
        //
        //  (Single winner: )Enjoy your time at:
        //  (Multiple winners: ) something funny about duking it out
        //
        //  [blah blah blah] <-- These are details about the winner
        //  [blah blah blah] <-- The other winner if there is one

        // TODO: Populate fragment fields here
    }

    private fun getGroupWinners(): ArrayList<String> {
        val resultTally: MutableMap<String, Int> = mutableMapOf()
        val winners: ArrayList<String> = ArrayList()
        if (group.allUserVotes == null) {
            return winners
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

        return winners
    }

    private fun initializeWinnerImageCarousel(winners: ArrayList<String>) {
        imagesAdapter = MatchImageAdapter(winners, group.matches)

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
        var density = context!!.resources.displayMetrics.density
        layoutParams.width = Math.round(((winners.size * 100) - ((winners.size - 1) * 40)) * density)
        concludedImagesRecyclerView.layoutParams = layoutParams

        concludedImagesRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = imagesViewLayoutManager
            adapter = imagesAdapter
        }
    }
}
