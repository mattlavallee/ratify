package io.github.mattlavallee.rightify.data

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import io.github.mattlavallee.rightify.R
import io.github.mattlavallee.rightify.core.FormError
import io.github.mattlavallee.rightify.presentation.SnackbarGenerator

class GroupViewModel {
    private var createViewRef: View? = null
    private var createGroupName: TextInputEditText? = null
    private var createGroupDescription: TextInputEditText? = null
    private var createGroupPlace: Place? = null
    private var createMaxResults: NumberPicker? = null
    private var createCreateBtn: Button? = null

    constructor(view: View?) {
        this.createViewRef = view

        this.createGroupName = this.createViewRef?.findViewById(R.id.create_group_name)
        this.createGroupDescription = this.createViewRef?.findViewById(R.id.create_group_description)
        this.createMaxResults = this.createViewRef?.findViewById(R.id.create_group_max_results)
        this.createCreateBtn = this.createViewRef?.findViewById(R.id.create_group_create_btn)
    }

    fun handlePlaceSelection(selectedPlace: Place?, ctx: Context?, resultCode: Int?, data: Intent?, fromListener: Boolean) {
        if (fromListener) {
            //being handled via PlaceSelectionListener
            this.createGroupPlace = selectedPlace
        } else {
            //being handled via onActivityResult
            when (resultCode) {
                RESULT_OK -> {
                    this.createGroupPlace = PlaceAutocomplete.getPlace(ctx, data)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(ctx, data)
                    SnackbarGenerator.generateSnackbar(this.createViewRef, "Error fetching location: " + status.statusMessage)?.show()
                    this.createGroupPlace = null
                }
                RESULT_CANCELED -> {
                    Log.i("Rightify", "User cancelled place search")
                }
            }
        }
    }

    fun createGroupHandler() {
        val groupName: String = this.createGroupName?.text.toString()
        val groupDescription: String = this.createGroupDescription?.text.toString()
        val results: Int = if (this.createMaxResults?.value != null) this.createMaxResults?.value as Int else 0
        var errors = FormError()

        if (groupName.isEmpty()) errors.missing.add("Name")
        if (groupDescription.isEmpty()) errors.missing.add("Description")
        if (this.createGroupPlace == null) errors.missing.add("Location")
        if (results == 0) errors.missing.add("Total Results")

        if (errors.hasErrors()) {
            SnackbarGenerator.generateSnackbar(this.createViewRef, errors.generateErrorMessage())?.show()
        } else {
            SnackbarGenerator.generateSnackbar(this.createViewRef, "Success!")?.show()
        }
    }
}