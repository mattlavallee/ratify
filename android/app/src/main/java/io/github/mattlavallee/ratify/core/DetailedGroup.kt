package io.github.mattlavallee.ratify.core

import java.io.Serializable

class DetailedGroup(
    private val details: Group,
    private val matches: ArrayList<YelpResult>,
    private val votes: Map<String, UserVote>
): Serializable {
    fun isConcluded(): Boolean {
        return this.details.isConcluded()
    }

    companion object {
        fun fromJsonHashMap(groupId: String, userId: String, model: HashMap<String, Any>): DetailedGroup {
            val groupDetails = Group.fromJsonHashMap(groupId, model)
            val matches = YelpResult.fromHashMap(model["matches"] as HashMap<String, HashMap<String, Any>>)
            val userVotesMap = (model["userVotes"] as HashMap<String, Any>)[userId] as HashMap<String, Any>
            val userVotes: Map<String, UserVote> = UserVote.fromHashMap(userVotesMap)
            return DetailedGroup(groupDetails, matches, userVotes)
        }
    }
}