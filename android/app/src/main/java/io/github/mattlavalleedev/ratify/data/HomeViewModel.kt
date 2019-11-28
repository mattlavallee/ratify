package io.github.mattlavalleedev.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavalleedev.ratify.core.DetailedGroup
import io.github.mattlavalleedev.ratify.core.Group

class HomeViewModel : ViewModel {
    private val fetchPending: MutableLiveData<Boolean> = MutableLiveData()
    private val groupDetails: MutableLiveData<DetailedGroup> = MutableLiveData()
    private val groups: MutableLiveData<ArrayList<Group>> = MutableLiveData()
    private val error: MutableLiveData<String> = MutableLiveData()

    public constructor() {}

    fun getGroups(): LiveData<ArrayList<Group>> {
        return groups
    }

    fun getErrorMessage(): LiveData<String> {
        return error
    }

    fun getFetchIsPending(): LiveData<Boolean> {
        return fetchPending
    }

    fun getGroupDetails(): LiveData<DetailedGroup> {
        return groupDetails
    }

    fun fetch() {
        fetchPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("getGroups").call().continueWith { task ->
            if (task.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val response: HashMap<String, Any> = task.result?.data as HashMap<String, Any>
                @Suppress("UNCHECKED_CAST")
                val createdGroups: HashMap<String, Any> = response["created_groups"] as HashMap<String, Any>
                @Suppress("UNCHECKED_CAST")
                val joinedGroups: HashMap<String, Any> = response["joined_groups"] as HashMap<String, Any>

                val groupsList: ArrayList<Group> = ArrayList()
                for (key: String in createdGroups.keys) {
                    @Suppress("UNCHECKED_CAST")
                    groupsList.add(Group.fromJsonHashMap(key, createdGroups[key] as HashMap<String, Any>))
                }
                for (key: String in joinedGroups.keys) {
                    @Suppress("UNCHECKED_CAST")
                    groupsList.add(Group.fromJsonHashMap(key, joinedGroups[key] as HashMap<String, Any>))
                }

                groups.value = groupsList
                error.value = null
            } else {
                Log.e("RATIFY", task.exception?.stackTrace.toString())
                error.value = "Error getting groups!"
            }
            fetchPending.value = false
        }
    }

    fun fetchGroup(groupId: String) {
        val params: MutableMap<String, String> = mutableMapOf()
        params["groupId"] = groupId
        fetchPending.value = true
        var userId = ""
        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            userId = FirebaseAuth.getInstance().currentUser?.uid as String
        }
        FirebaseFunctions.getInstance().getHttpsCallable("getGroupById").call(params).continueWith {
            task ->
                fetchPending.value = false
                if (task.isSuccessful) {
                    error.value = null
                    @Suppress("UNCHECKED_CAST")
                    val results = task.result?.data as HashMap<String, Any>
                    groupDetails.value = DetailedGroup.fromJsonHashMap(
                        groupId,
                        userId,
                        @Suppress("UNCHECKED_CAST")
                        results["results"] as HashMap<String, Any>
                    )
                } else {
                    Log.e("RATIFY", task.exception?.stackTrace.toString())
                    error.value = "Error launching group!"
                }
        }
    }
}