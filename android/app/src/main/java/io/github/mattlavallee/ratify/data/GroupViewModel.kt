package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.ViewModel
import com.google.android.gms.location.places.Place
import java.util.*

class GroupViewModel: ViewModel {
    public constructor(){}

    fun validateGroup(name: String, activity: String, place: Place?, numResults: Int, voteConclusion: Calendar?, expirationDays: Int): ArrayList<String> {
        var errorFields = ArrayList<String>()
        if (name.trim().isEmpty()) errorFields.add("name")
        if (activity.trim().isEmpty()) errorFields.add("activity")
        if (place == null) errorFields.add("place")
        if (numResults < 0 || numResults > 30) errorFields.add("maxResults")
        if (voteConclusion == null || voteConclusion.time.before(Date())) errorFields.add("voteConclusion")
        if (expirationDays < 0 || expirationDays > 14) errorFields.add("expirationDays")

        return errorFields
    }

    fun createGroup() {
        //TODO: implement me
    }
}