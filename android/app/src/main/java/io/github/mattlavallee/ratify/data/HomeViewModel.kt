package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions

class HomeViewModel: ViewModel {
    private val fetchPending: MutableLiveData<Boolean> = MutableLiveData()
    private val testData: MutableLiveData<String> = MutableLiveData()

    public constructor(){}

    fun getTestData(): LiveData<String> {
        return testData
    }

    fun getFetchIsPending(): LiveData<Boolean> {
        return fetchPending
    }

    fun fetch() {
        fetchPending.value = true
        FirebaseFunctions.getInstance().getHttpsCallable("getGroups").call()
            .continueWith({ task ->
                if (task.isSuccessful) {
                    testData.value = task.result.data.toString()
                } else {
                    testData.value = "Error getting groups! " + task.exception?.stackTrace.toString()
                }
                fetchPending.value = false
            })
    }
}