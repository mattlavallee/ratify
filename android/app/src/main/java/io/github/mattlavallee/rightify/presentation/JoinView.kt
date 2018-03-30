package io.github.mattlavallee.rightify.presentation

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class JoinView(val codeInput: EditText, val joinBtn: Button) {
    init {
        codeInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, pq: Int, p2: Int, p3: Int){}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun afterTextChanged(p0: Editable?){
                joinBtn.isEnabled = !p0.toString().isBlank()
                joinBtn.alpha = if(joinBtn.isEnabled) 1f else 0.5f
            }
        })
    }

    fun resetCodeInput(): Unit {
        codeInput.setText("")
        joinBtn.isEnabled = false
        joinBtn.alpha = 0.5f
    }
}