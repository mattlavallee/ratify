package io.github.mattlavallee.ratify.core

import java.io.Serializable
import java.text.SimpleDateFormat

class DetailedGroup(
    private val details: Group,
    val matches: ArrayList<YelpResult>,
    val votes: Map<String, UserVote>,
    val allUserVotes: Map<String, Map<String, UserVote>>?
) : Serializable {
    private val conclusionFormatter: SimpleDateFormat = SimpleDateFormat("MMM d yyyy h:mm a")

    fun isConcluded(): Boolean {
        return this.details.isConcluded()
    }

    fun getName(): String {
        return this.details.name
    }

    fun getGroupId(): String {
        return this.details.id
    }

    fun getDescription(): String {
        return this.details.description
    }

    fun getVoteConclusion(): String {
        return conclusionFormatter.format(this.details.voteConclusion)
    }

    companion object {
        fun fromJsonHashMap(groupId: String, userId: String, model: HashMap<String, Any>): DetailedGroup {
            val groupDetails = Group.fromJsonHashMap(groupId, model)

            @Suppress("UNCHECKED_CAST")
            val matches = YelpResult.fromHashMap(model["matches"] as HashMap<String, HashMap<String, Any>>)

            @Suppress("UNCHECKED_CAST")
            val userVotesMap = (model["userVotes"] as HashMap<String, Any>)[userId] as HashMap<String, Any>
            val userVotes: Map<String, UserVote> = UserVote.fromHashMap(userVotesMap)

            var allUserVotes: MutableMap<String, Map<String, UserVote>>? = null
            if (groupDetails.isConcluded()) {
                allUserVotes = mutableMapOf()
                for( (currUserId, voteMap) in (model["userVotes"] as HashMap<String, Any>)) {
                    allUserVotes[currUserId] = UserVote.fromHashMap(voteMap as HashMap<String, Any>)
                }
            }
            return DetailedGroup(groupDetails, matches, userVotes, allUserVotes?.toMap())
        }
    }
}