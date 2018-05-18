package io.github.mattlavallee.ratify.presentation

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import io.github.mattlavallee.ratify.R
import android.widget.NumberPicker
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import io.github.mattlavallee.ratify.core.FormError
import io.github.mattlavallee.ratify.data.GroupViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface

class CreateFragment : Fragment(), UserAuthInterface {
    private var groupViewModel: GroupViewModel? = null
    private var autocompleteFragment: SupportPlaceAutocompleteFragment? = null
    private var createGroupName: TextInputEditText? = null
    private var createGroupDescription: TextInputEditText? = null
    private var createGroupPlace: Place? = null
    private var createMaxResults: NumberPicker? = null
    private var createCreateBtn: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        var hasPlayServicesAccess = GoogleApiAvailability.getInstance()?.isGooglePlayServicesAvailable(activity?.applicationContext)
        if (hasPlayServicesAccess != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()?.getErrorDialog(activity, hasPlayServicesAccess!!, 9000)?.show()
        }

        this.createGroupName = view.findViewById(R.id.create_group_name)
        this.createGroupDescription = view.findViewById(R.id.create_group_description)
        this.createMaxResults = view.findViewById(R.id.create_group_max_results)
        this.createCreateBtn = view.findViewById(R.id.create_group_create_btn)
        autocompleteFragment = activity?.supportFragmentManager?.findFragmentById(R.id.place_autocomplete_fragment) as? SupportPlaceAutocompleteFragment
        val typeFilter: AutocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build()
        autocompleteFragment?.setFilter(typeFilter)
        autocompleteFragment?.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(selectedPlace: Place?) {
                createGroupPlace = selectedPlace
            }
            override fun onError(p0: Status?) {
                SnackbarGenerator.generateSnackbar(view, "Error fetching location")?.show()
            }
        })

        var maxResults: NumberPicker = activity?.findViewById(R.id.create_group_max_results) as NumberPicker
        maxResults.maxValue = 30
        maxResults.minValue = 0
        maxResults.value = 20

        var createBtn: Button = activity?.findViewById(R.id.create_group_create_btn) as Button
        createBtn.setOnClickListener {
            val groupName: String = this.createGroupName?.text.toString()
            val groupDescription: String = this.createGroupDescription?.text.toString()
            val results: Int = if (this.createMaxResults?.value != null) this.createMaxResults?.value as Int else 0
            val errors: FormError? = this.groupViewModel?.validateGroup(groupName, groupDescription, results, this.createGroupPlace)

            if (errors != null) {
                SnackbarGenerator.generateSnackbar(view, errors.generateErrorMessage())?.show()
            } else {
                this.groupViewModel?.createGroup()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        autocompleteFragment?.onActivityResult(requestCode, resultCode, data)

        //handle selection from place autocomplete
        when (resultCode) {
            Activity.RESULT_OK -> {
                this.createGroupPlace = PlaceAutocomplete.getPlace(view?.context, data)
            }
            PlaceAutocomplete.RESULT_ERROR -> {
                val status = PlaceAutocomplete.getStatus(view?.context, data)
                SnackbarGenerator.generateSnackbar(view, "Error fetching location: " + status.statusMessage)?.show()
                this.createGroupPlace = null
            }
            Activity.RESULT_CANCELED -> {
                Log.i("Ratify", "User cancelled place search")
            }
        }
    }

    override fun onUserAuthError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAuthSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}