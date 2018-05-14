package io.github.mattlavallee.rightify.core

import com.google.android.gms.location.places.Place

class Group {
    var name: String = ""
    var description: String = ""
    var placeName: String = ""
    var placeLatitude: Double = -1.0
    var placeLongitude: Double = -1.0
    var maxResults: Int = 0

    constructor(name: String, descr: String, location: Place, numResults: Int) {
        this.name = name
        this.description = descr
        this.placeName = location.name.toString()
        this.placeLatitude = location.latLng.latitude
        this.placeLongitude = location.latLng.longitude
        this.maxResults = numResults
    }
}