package io.github.mattlavallee.ratify.core

import com.google.android.gms.location.places.Place
import java.util.Date

class Group {
    var id: String
    var type: String
    var name: String
    var description: String
    var activity: String
    var placeName: String = ""
    var placeLatitude: Double = -1.0
    var placeLongitude: Double = -1.0
    var maxResults: Int = -1
    var expirationDays: Int = -1
    var voteConclusion: Date = Date(0)
    var participants: Int = -1

    constructor(
        id: String,
        type: String,
        name: String,
        descr: String,
        activity: String,
        location: Place?,
        numResults: Int,
        conclusion: Date,
        expiration: Int
    ) {
        this.id = id
        this.type = type
        this.name = name
        this.description = descr
        this.activity = activity
        if (location != null) {
            this.placeName = location.name.toString()
            this.placeLatitude = location.latLng.latitude
            this.placeLongitude = location.latLng.longitude
        }

        this.maxResults = numResults
        this.voteConclusion = conclusion
        this.expirationDays = expiration
    }

    constructor(
        id: String,
        type: String,
        name: String,
        descr: String,
        activity: String,
        latitude: Double,
        longitude: Double,
        numResults: Int,
        conclusion: Date,
        expiration: Int,
        participants: Int?) {
        this.id = id
        this.type = type
        this.name = name
        this.description = descr
        this.activity = activity
        this.placeLatitude = latitude
        this.placeLongitude = longitude
        this.maxResults = numResults
        this.voteConclusion = conclusion
        this.expirationDays = expiration
        if (participants != null) {
            this.participants = participants
        }
    }

    fun isConcluded(): Boolean {
        return this.voteConclusion.before(Date())
    }

    fun populateParams(params: MutableMap<String, Any>): MutableMap<String, Any> {
        params["name"] = this.name
        params["type"] = this.type
        params["description"] = this.description
        params["activity"] = this.activity
        params["startingLocation"] = this.placeName
        params["latitude"] = this.placeLatitude
        params["longitude"] = this.placeLongitude
        params["results"] = this.maxResults
        params["expiration"] = this.expirationDays
        params["conclusion"] = this.voteConclusion.time
        return params
    }

    companion object {
        fun fromJsonHashMap(id: String, model: HashMap<String, Any>): Group {
            @Suppress("UNCHECKED_CAST")
            val location: HashMap<String, Double> = model["location"] as HashMap<String, Double>

            var totalActiveParticipants = 0
            if (model["members"] != null) {
                @Suppress("UNCHECKED_CAST")
                val participants: HashMap<String, Boolean> = model["members"] as HashMap<String, Boolean>

                totalActiveParticipants = participants.count { it.value }
            }

            var query = if (model["query"] != null) model["query"] as String else model["activity"] as String
            var numberResults = if(model["numberResults"] != null) model["numberResults"] as Int else model["maxResults"] as Int
            var expiration = if(model["daysToExpire"] != null) model["daysToExpire"] as Int else model["expiration"] as Int
            var conclusion = if(model["voteConclusionEpoch"] != null) model["voteConclusionEpoch"] as Long else model["voteConclusion"] as Long

            return Group(
                id,
                model["type"] as String,
                model["name"] as String,
                model["description"] as String,
                query,
                location["latitude"] as Double,
                location["longitude"] as Double,
                numberResults,
                Date(conclusion),
                expiration,
                totalActiveParticipants
            )
        }
    }
}