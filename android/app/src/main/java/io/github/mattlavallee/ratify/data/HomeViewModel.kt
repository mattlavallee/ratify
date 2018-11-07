package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group

class HomeViewModel: ViewModel {
    private val fetchPending: MutableLiveData<Boolean> = MutableLiveData()
    private val groups: MutableLiveData<ArrayList<Group>> = MutableLiveData()
    private val error: MutableLiveData<String> = MutableLiveData()

    public constructor(){}

    fun getGroups(): LiveData<ArrayList<Group>> {
        return groups
    }

    fun getErrorMessage(): LiveData<String> {
        return error
    }

    fun getFetchIsPending(): LiveData<Boolean> {
        return fetchPending
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
                for(key: String in createdGroups.keys) {
                    @Suppress("UNCHECKED_CAST")
                    groupsList.add(Group.fromJsonHashMap(key, createdGroups[key] as HashMap<String, Any>))
                }
                for(key: String in joinedGroups.keys) {
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
}