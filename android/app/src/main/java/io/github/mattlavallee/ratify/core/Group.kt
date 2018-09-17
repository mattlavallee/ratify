package io.github.mattlavallee.ratify.core

import com.google.android.gms.location.places.Place
import java.util.*

class Group {
    var id: String
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

    constructor(id: String, name: String, descr: String, activity: String, location: Place?, numResults: Int,
                conclusion: Date, expiration: Int) {
        this.id = id
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

    constructor(id: String, name: String, descr: String, activity: String, latitude: Double, longitude: Double,
                numResults: Int, conclusion: Date, expiration: Int, participants: Int?) {
        this.id = id
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

    fun populateParams(params: MutableMap<String, Any>): MutableMap<String, Any> {
        params["name"] = this.name
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
            val participants: HashMap<String, Boolean> = model["members"] as HashMap<String, Boolean>

            val totalActiveParticipants: Int = participants.count { it.value }
            return Group(
                id,
                model["name"] as String,
                model["description"] as String,
                model["query"] as String,
                location["latitude"] as Double,
                location["longitude"] as Double,
                model["numberResults"] as Int,
                Date(model["voteConclusion"] as Long),
                model["daysToExpire"] as Int,
                totalActiveParticipants
            )
        }
    }
}