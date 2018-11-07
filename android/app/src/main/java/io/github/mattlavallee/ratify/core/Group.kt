package io.github.mattlavallee.ratify.core

import com.google.android.gms.location.places.Place
import java.util.Date

data class Group(
    var id: String,
    var type: String,
    var name: String,
    var description: String,
    var activity: String,
    var placeLatitude: Double,
    var placeLongitude: Double,
    var maxResults: Int,
    var voteConclusion: Date,
    var expirationDays: Int,
    var participants: Int
) {
    var placeName: String = ""

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
    ):
            this(id, type, name, descr, activity, location?.latLng?.latitude!!,
                    location.latLng?.longitude!!, numResults, conclusion, expiration, -1) {
        if (location.name != null) {
            this.placeName = location.name.toString()
        }
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
            val participants: HashMap<String, Boolean> = model["members"] as HashMap<String, Boolean>

            val totalActiveParticipants: Int = participants.count { it.value }
            return Group(
                id,
                model["type"] as String,
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