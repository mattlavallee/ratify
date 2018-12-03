package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.core.YelpResult
import java.util.Date

class GroupViewModel : ViewModel {
    private val createPending: MutableLiveData<Boolean> = MutableLiveData()
    private val createGroupError: MutableLiveData<String> = MutableLiveData()
    private val createGroupCode: MutableLiveData<String> = MutableLiveData()
    private val createGroupPreview: MutableLiveData<ArrayList<YelpResult>> = MutableLiveData()
    private val groupMatchesCache: HashMap<String, ArrayList<YelpResult>> = HashMap()

    public constructor() {}

    fun getCreatePending(): LiveData<Boolean> {
        return createPending
    }

    fun getGroupError(): LiveData<String> {
        return createGroupError
    }

    fun getGroupCode(): LiveData<String> {
        return createGroupCode
    }

    fun getGroupPreview(): LiveData<ArrayList<YelpResult>> {
        return createGroupPreview
    }

    fun validateGroup(group: Group): ArrayList<String> {
        var errorFields = ArrayList<String>()
        if (group.name.trim().isEmpty()) errorFields.add("name")
        if (group.activity.trim().isEmpty()) errorFields.add("activity")
        if (group.placeName.isEmpty()) errorFields.add("place")
        if (group.maxResults < 0 || group.maxResults > 30) errorFields.add("maxResults")
        if (group.voteConclusion.before(Date())) errorFields.add("voteConclusion")
        if (group.expirationDays < 0 || group.expirationDays > 14) errorFields.add("expirationDays")

        return errorFields
    }

    fun createGroup(group: Group) {
        var params: MutableMap<String, Any> = mutableMapOf()
        group.populateParams(params)

        val cacheKey = group.activity + "-" + group.placeLatitude.toString() + "-" +
                group.placeLongitude.toString() + "-" + group.maxResults.toString()
        if (groupMatchesCache.containsKey(cacheKey)) {
            params["matches"] = YelpResult.toJsonArray(groupMatchesCache[cacheKey] as ArrayList<YelpResult>)
        }

        this.createPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("createGroup").call(params).continueWith { task ->
            this.createPending.value = false
            if (task.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val response: HashMap<String, Any> = task.result?.data as HashMap<String, Any>
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

    fun previewResults(activity: String, latitude: Double, longitude: Double, results: Int) {
        val cacheKey = activity + "-" + latitude.toString() + "-" + longitude.toString() + "-" + results.toString()

        if (groupMatchesCache.contains(cacheKey)) {
            createGroupPreview.value = groupMatchesCache[cacheKey]
            return
        }

        var params: MutableMap<String, Any> = mutableMapOf()
        params["activity"] = activity
        params["latitude"] = latitude
        params["longitude"] = longitude
        params["maxResults"] = results

        this.createPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("previewGroupResults").call(params).continueWith { task ->
            this.createPending.value = false
            if (task.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val response: HashMap<String, Any> = task.result?.data as HashMap<String, Any>
                if (response["error"] != null) {
                    createGroupError.value = response["error"].toString()
                } else {
                    val results = YelpResult.fromJsonArray((response["results"] as ArrayList<HashMap<String, Any>>))
                    groupMatchesCache[cacheKey] = results
                    createGroupPreview.value = results
                }
            } else {
                createGroupError.value = "Error getting group previews!"
            }
        }
    }
}