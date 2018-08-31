package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions
import io.github.mattlavallee.ratify.core.Group

class JoinViewModel: ViewModel {
    private val joinedGroup: MutableLiveData<Pair<String, Group?>> = MutableLiveData()

    public constructor(){}

    fun getGroup(): LiveData<Pair<String, Group?>> {
        return joinedGroup
    }

    fun joinGroup(groupCode: String) {
        var params: MutableMap<String, String> = mutableMapOf()
        params["groupCode"] = groupCode

        FirebaseFunctions.getInstance().getHttpsCallable("joinGroup").call(params).continueWith { task ->
            if (task.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                val joinGroupResponse: HashMap<String, Any> = task.result.data as HashMap<String, Any>
                if (joinGroupResponse["error"] != null) {
                    joinedGroup.value = Pair(joinGroupResponse["error"].toString(), null)
                } else {
                    joinedGroup.value = Pair("", null)
                }
            } else {
                joinedGroup.value = Pair("Error getting groups! " + task.exception?.stackTrace.toString(), null)
            }
        }
    }
}