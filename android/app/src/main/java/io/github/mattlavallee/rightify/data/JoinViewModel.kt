package io.github.mattlavallee.rightify.data

import android.view.View
import android.widget.Button
import android.widget.EditText
import io.github.mattlavallee.rightify.R
import io.github.mattlavallee.rightify.presentation.SnackbarGenerator

class JoinViewModel {
    private var joinViewRef: View? = null
    private var joinCode: EditText? = null
    private var joinButton: Button? = null

    constructor(view: View?) {
        this.joinViewRef = view
        this.joinCode = this.joinViewRef?.findViewById(R.id.join_code)
        this.joinButton = this.joinViewRef?.findViewById(R.id.btn_join)
    }

    fun handleJoinGroup() {
        val code: String = this.joinCode?.text.toString()
        if (code.isEmpty()) {
            SnackbarGenerator.generateSnackbar(this.joinViewRef, "You must enter a code to join a group")?.show()
            return
        }

        SnackbarGenerator.generateSnackbar(this.joinViewRef, "Group Joined!")?.show()
        //TODO: Make request to join group
    }
}