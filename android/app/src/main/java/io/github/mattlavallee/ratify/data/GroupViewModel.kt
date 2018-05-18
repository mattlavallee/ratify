package io.github.mattlavallee.ratify.data

import android.arch.lifecycle.ViewModel
import com.google.android.gms.location.places.Place
import io.github.mattlavallee.ratify.core.FormError

class GroupViewModel: ViewModel {
    public constructor(){}

    fun validateGroup(name: String, description: String, numResults: Int, place: Place?): FormError? {
        var errors = FormError()
        if (name.isEmpty()) errors.missing.add("Name")
        if (description.isEmpty()) errors.missing.add("Description")
        if (place == null) errors.missing.add("Location")
        if (numResults == 0) errors.missing.add("Total Results")

        if (errors.hasErrors()) {
            return errors
        }
        return null
    }

    fun createGroup() {
        //TODO: implement me
    }
}