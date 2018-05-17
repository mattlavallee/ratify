package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions

class HomeViewModel: ViewModel {
    private val testData: MutableLiveData<String> = MutableLiveData()

    public constructor(){}

    fun getTestData(): LiveData<String> {
        return testData
    }

    fun fetch() {
        FirebaseFunctions.getInstance().getHttpsCallable("getGroups").call()
            .continueWith({ task ->
                if (task.isSuccessful) {
                    testData.value = task.result.data.toString()
                } else {
                    testData.value = "Error getting groups!"
                }
            })
    }
}