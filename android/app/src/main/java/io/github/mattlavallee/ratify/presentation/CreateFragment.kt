package io.github.mattlavallee.ratify.presentation

import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
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
import io.github.mattlavallee.ratify.data.GroupViewModel

class CreateFragment : Fragment() {
    private var autocompleteFragment: SupportPlaceAutocompleteFragment? = null
    private var editGroup: GroupViewModel? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.editGroup = GroupViewModel(view)

        var hasPlayServicesAccess = GoogleApiAvailability.getInstance()?.isGooglePlayServicesAvailable(activity.applicationContext)
        if (hasPlayServicesAccess != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()?.getErrorDialog(activity, hasPlayServicesAccess!!, 9000)?.show()
        }

        autocompleteFragment = activity.supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as? SupportPlaceAutocompleteFragment
        val typeFilter: AutocompleteFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build()
        autocompleteFragment?.setFilter(typeFilter)
        autocompleteFragment?.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place?) {
                editGroup?.handlePlaceSelection(p0, null, null, null, true)
            }
            override fun onError(p0: Status?) {
                SnackbarGenerator.generateSnackbar(view, "Error fetching location")?.show()
            }
        })

        var maxResults: NumberPicker = activity.findViewById(R.id.create_group_max_results) as NumberPicker
        maxResults.maxValue = 30
        maxResults.minValue = 0
        maxResults.value = 20

        var createBtn: Button = activity.findViewById(R.id.create_group_create_btn) as Button
        createBtn.setOnClickListener {
            this.editGroup?.createGroupHandler()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_create, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        autocompleteFragment?.onActivityResult(requestCode, resultCode, data)

        this.editGroup?.handlePlaceSelection(null, this.context, resultCode, data, false)
    }
}