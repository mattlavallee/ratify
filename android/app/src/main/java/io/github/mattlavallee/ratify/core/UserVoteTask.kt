package io.github.mattlavallee.ratify.core

import com.google.firebase.functions.FirebaseFunctions

class UserVoteTask(private val group: DetailedGroup): Runnable {
    override fun run() {
        val updatedVotes = this.group.votes.filter { it.value.isUpdated() }
        if (!updatedVotes.isEmpty()) {
            for ((_, vote) in updatedVotes) {
                vote.markClean()
            }

            var params: MutableMap<String, Any> = mutableMapOf()
            params["groupId"] = group.getGroupId()
            params["votes"] = UserVote.toJsonArray(updatedVotes.values)

            FirebaseFunctions.getInstance().getHttpsCallable("setGroupVotes").call(params).continueWith { task ->
                if (!task.isSuccessful) {
                    for ((_, vote) in updatedVotes) {
                        vote.markDirty()
                    }
                }
            }
        }
    }
}
