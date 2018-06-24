package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group
import java.util.*

class GroupViewModel: ViewModel {
    private val createPending: MutableLiveData<Boolean> = MutableLiveData()

    public constructor(){}

    fun getCreatePending(): LiveData<Boolean> {
        return createPending
    }

    fun validateGroup(group: Group): ArrayList<String> {
        var errorFields = ArrayList<String>()
        if (group.name.trim().isEmpty()) errorFields.add("name")
        if (group.activity.trim().isEmpty()) errorFields.add("activity")
        if (group.placeName.isEmpty() || group.placeName == null) errorFields.add("place")
        if (group.maxResults < 0 || group.maxResults > 30) errorFields.add("maxResults")
        if (group.voteConclusion == null || group.voteConclusion.before(Date())) errorFields.add("voteConclusion")
        if (group.expirationDays < 0 || group.expirationDays > 14) errorFields.add("expirationDays")

        return errorFields
    }

    fun createGroup(group: Group) {
        this.createPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("createGroup").call().continueWith() { task ->
            this.createPending.value = false
        }
    }
}