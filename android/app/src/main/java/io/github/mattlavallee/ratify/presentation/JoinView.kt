package io.github.mattlavallee.ratify.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.View
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import io.github.mattlavallee.ratify.R
import io.github.mattlavallee.ratify.data.JoinViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.FragmentSwitchInterface

class JoinView(view: View?, activity: AppCompatActivity) {
    private var joinCode: EditText? = null
    private var joinBtn: Button? = null
    private var viewModel: JoinViewModel? = null
    private var callbackActivity: FragmentSwitchInterface? =  null

    init {
        viewModel = ViewModelProviders.of(activity).get(JoinViewModel::class.java)
        if (activity is FragmentSwitchInterface) {
            callbackActivity = activity
        }
        this.joinCode = view?.findViewById(R.id.join_code)
        this.joinBtn = view?.findViewById(R.id.btn_join)
        this.joinCode?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, pq: Int, p2: Int, p3: Int){}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun afterTextChanged(p0: Editable?){
                joinBtn?.isEnabled = !p0.toString().isBlank()
                joinBtn?.alpha = if(joinBtn?.isEnabled == true) 1f else 0.5f
            }
        })

        this.joinBtn?.setOnClickListener {
            val code: String = this.joinCode?.text.toString()
            this.viewModel?.joinGroup(code)
        }

        viewModel?.getGroup()?.observe(activity, Observer{
            groupVal -> callbackActivity?.onResetToHomeFragment("")
        })
    }

    fun resetCodeInput() {
        this.joinCode?.setText("")
        this.joinBtn?.isEnabled = false
        this.joinBtn?.alpha = 0.5f
    }
}