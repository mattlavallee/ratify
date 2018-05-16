package io.github.mattlavallee.ratify.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import io.github.mattlavallee.ratify.core.Constants

class HomeViewModel {
    var requestQueue: RequestQueue? = null

    constructor(ctx: Context) {
        requestQueue = Volley.newRequestQueue(ctx)
    }

    fun fetch() {
        var user: FirebaseUser = FirebaseAuth.getInstance().currentUser as FirebaseUser
        user.getIdToken(true).addOnCompleteListener(object: OnCompleteListener<GetTokenResult> {
            override fun onComplete(task: Task<GetTokenResult>) {
                if (task.isSuccessful) {
                    val idToken: String? = task.result.token
                    if (idToken.isNullOrEmpty()) {
                        //TODO: same error handling
                    }
                    fetchUserGroups(idToken!!)
                } else {
                    //TODO: implement error handling
                }
            }
        })
    }

    private fun fetchUserGroups(token: String) {
        val url: String = Constants.API_BASE_URL + "getGroups?uuid=" + token
        val request = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.i("TEST", response.toString())
                },
                Response.ErrorListener { error ->
                    // TODO: Handle error
                })
        requestQueue?.add(request)
    }

    fun closeConnections() {
        requestQueue?.cancelAll(Constants.APP_TAG)
    }
}