package io.github.mattlavallee.ratify.core

import android.util.Log

class YelpResult(
    var id: String,
    var name: String,
    var businessImage: String,
    var address: String,
    var rating: Double,
    var price: String
) {
    constructor(payload: HashMap<String, Any>): this(payload["id"] as String, payload["name"] as String,
        payload["businessImage"] as String, payload["address"] as String, 0.0,
        payload["price"] as String) {
        if (payload["rating"] is Integer) {
            this.rating = (payload["rating"] as Integer).toDouble()
        } else {
            this.rating = payload["rating"] as Double
        }
    }

    companion object {
        fun fromJsonArray(jsonPayload: ArrayList<HashMap<String, Any>>): ArrayList<YelpResult> {
            val result: ArrayList<YelpResult> = ArrayList()
            for (payload in jsonPayload) {
                result.add(YelpResult(payload))
            }
            return result
        }
    }
}