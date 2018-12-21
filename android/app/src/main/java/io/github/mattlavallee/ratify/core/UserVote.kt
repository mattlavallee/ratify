package io.github.mattlavallee.ratify.core

class UserVote(var matchId: String, var value: Number) {
    private var isDirty: Boolean = false

    fun updateVote(voteState: Number) {
        this.value = voteState
        this.isDirty = true
    }

    fun isUpdated(): Boolean {
        return this.isDirty
    }

    companion object {
        val YES = 1
        val NO = 2

        fun fromHashMap(jsonPayload: HashMap<String, Any>): Map<String, UserVote> {
            val result: MutableMap<String, UserVote> = mutableMapOf()

            if (jsonPayload != null) {
                for ((id: String, voteValue: Any) in jsonPayload) {
                    var voteStatus: Number = 0
                    if (voteValue is Number) {
                        voteStatus = voteValue
                    }
                    result[id] = UserVote(id, voteStatus)
                }
            }

            return result.toMap()
        }
    }
}