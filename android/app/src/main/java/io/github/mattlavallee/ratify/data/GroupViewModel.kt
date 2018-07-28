package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group
import java.util.*

class GroupViewModel: ViewModel {
    private val createPending: MutableLiveData<Boolean> = MutableLiveData()
    private val createGroupError: MutableLiveData<String> = MutableLiveData()
    private val createGroupCode: MutableLiveData<String> = MutableLiveData()

    public constructor(){}

    fun getCreatePending(): LiveData<Boolean> {
        return createPending
    }

    fun getGroupError(): LiveData<String> {
        return createGroupError
    }


    fun getGroupCode(): LiveData<String> {
        return createGroupCode
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
        var params: MutableMap<String, Any> = mutableMapOf()
        group.populateParams(params)

        this.createPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("createGroup").call(params).continueWith { task ->
            this.createPending.value = false
            if (task.isSuccessful) {
                val response: HashMap<String, Any> = task.result.data as HashMap<String, Any>
                if (response["error"] != null) {
                    createGroupError.value = response["error"].toString()
                } else {
                    createGroupCode.value = response["groupId"].toString()
                }
            } else {
                createGroupError.value = "Error creating group! " + task.exception?.stackTrace.toString()
            }
        }
    }
}