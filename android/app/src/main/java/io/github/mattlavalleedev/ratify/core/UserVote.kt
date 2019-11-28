package io.github.mattlavalleedev.ratify.core

import java.io.Serializable

class UserVote(var matchId: String, var value: Number) : Serializable {
    private var isDirty: Boolean = false
    private var originalValue: Number

    init {
        this.originalValue = this.value
    }

    fun updateVote(voteState: Number) {
        this.value = voteState

        if (this.originalValue != voteState) {
            this.isDirty = true
        }
    }

    fun isUpdated(): Boolean {
        return this.isDirty
    }

    fun markClean() {
        this.isDirty = false
    }

    fun markDirty() {
        this.isDirty = true
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

        fun toJsonArray(data: Collection<UserVote>): List<Map<String, Any>> {
            val resultAsJson: ArrayList<Map<String, Any>> = ArrayList()
            data.map {
                val currResult: MutableMap<String, Any> = mutableMapOf()
                currResult["matchId"] = it.matchId
                currResult["value"] = it.value
                resultAsJson.add(currResult.toMap())
            }

            return resultAsJson.toList()
        }
    }
}