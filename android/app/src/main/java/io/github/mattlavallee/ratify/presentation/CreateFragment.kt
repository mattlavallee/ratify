package io.github.mattlavallee.ratify.presentation

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
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
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import io.github.mattlavallee.ratify.core.FormError
import io.github.mattlavallee.ratify.data.GroupViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface
import java.text.SimpleDateFormat
import java.util.*

class CreateFragment : Fragment(), UserAuthInterface {
    private var groupViewModel: GroupViewModel? = null
    private var autocompleteFragment: SupportPlaceAutocompleteFragment? = null
    private var createGroupName: TextInputEditText? = null
    private var createGroupDescription: TextInputEditText? = null
    private var createGroupVoteConclusion: TextInputEditText? = null
    private var createGroupMaxResultsDisplay: TextInputEditText? = null
    private var createGroupExpirationDisplay: TextInputEditText? = null
    private var createCreateBtn: Button? = null

    private var createGroupPlace: Place? = null
    private var createGroupMaxResults: Int = 20
    private var createGroupExpirationDays: Int = 7

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        var hasPlayServicesAccess = GoogleApiAvailability.getInstance()?.isGooglePlayServicesAvailable(activity?.applicationContext)
        if (hasPlayServicesAccess != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()?.getErrorDialog(activity, hasPlayServicesAccess!!, 9000)?.show()
        }

        this.createGroupName = view.findViewById(R.id.create_group_name)
        this.createGroupDescription = view.findViewById(R.id.create_group_description)
        this.createCreateBtn = view.findViewById(R.id.create_group_create_btn)
        autocompleteFragment = activity?.supportFragmentManager?.findFragmentById(R.id.place_autocomplete_fragment) as? SupportPlaceAutocompleteFragment
        configureAutocompleteFragment()

        this.createGroupVoteConclusion = activity?.findViewById(R.id.create_group_vote_conclusion) as TextInputEditText
        configureVoteConclusion()

        this.createGroupMaxResultsDisplay = activity?.findViewById(R.id.create_group_max_results_display) as TextInputEditText
        configureMaxResults()

        this.createGroupExpirationDisplay = activity?.findViewById(R.id.create_group_expiration) as TextInputEditText
        configureExpirationDialog()

        this.createCreateBtn = activity?.findViewById(R.id.create_group_create_btn) as Button
        configureCreate()
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

    /*
     * Configures the place autocomplete fragment.
     * Sets autocomplete listener and type filter
     */
    private fun configureAutocompleteFragment() {
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
    }

    /*
     * Configures vote conclusion edit text to launch date and time picker dialogs
     * Updates local variable with the modified datetime associated with the dialogs
     */
    private fun configureVoteConclusion() {
        var voteConclusionCalendar: Calendar = Calendar.getInstance()
        var timeListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
            voteConclusionCalendar.set(Calendar.HOUR_OF_DAY, hours)
            voteConclusionCalendar.set(Calendar.MINUTE, minutes)
            val dateFormat = SimpleDateFormat("M/dd/yyyy h:mm a", Locale.US)
            this.createGroupVoteConclusion?.setText(dateFormat.format(voteConclusionCalendar.time))
        }
        var dateListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { v, year, monthOfYear, dayOfMonth ->
            voteConclusionCalendar.set(Calendar.YEAR, year)
            voteConclusionCalendar.set(Calendar.MONTH, monthOfYear)
            voteConclusionCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(v.context, 0, timeListener, voteConclusionCalendar.get(Calendar.HOUR), voteConclusionCalendar.get(Calendar.MINUTE), false).show()
        }

        this.createGroupVoteConclusion?.setOnClickListener {
            DatePickerDialog(context!!, 0, dateListener, voteConclusionCalendar.get(Calendar.YEAR),
                    voteConclusionCalendar.get(Calendar.MONTH), voteConclusionCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun configureMaxResults() {
        val maxResults = NumberPicker(activity)
        maxResults.maxValue = 30
        maxResults.minValue = 0
        maxResults.value = 20

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Max Results to Display")
        builder.setView(maxResults)

        val listener = DialogInterface.OnClickListener { _, _ ->
            this.createGroupMaxResults = maxResults.value
            this.createGroupMaxResultsDisplay?.setText(this.createGroupMaxResults.toString())
        }

        builder.setPositiveButton("OK", listener)
        builder.setNegativeButton("Cancel", listener)
        val numberPickerDialog = builder.create() as AlertDialog
        this.createGroupMaxResultsDisplay?.setOnClickListener {
            numberPickerDialog.show()
        }
    }

    private fun configureExpirationDialog() {
        val expirationTime = NumberPicker(activity)
        expirationTime.maxValue = 14
        expirationTime.minValue = 0
        expirationTime.value = 7

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Days to Expiration")
        builder.setView(expirationTime)

        val clickListener = DialogInterface.OnClickListener { _, _ ->
            this.createGroupExpirationDays = expirationTime.value
            this.createGroupExpirationDisplay?.setText(this.createGroupExpirationDays.toString() + " days")
        }
        builder.setPositiveButton("OK", clickListener)
        builder.setNegativeButton("Cancel", clickListener)
        val expirationTimeDialog = builder.create() as AlertDialog
        this.createGroupExpirationDisplay?.setOnClickListener {
            expirationTimeDialog.show()
        }
    }

    /*
     * Configures create button - onclicklistener
     *  Grabs values to begin creating a new group
     */
    private fun configureCreate() {
        this.createCreateBtn?.setOnClickListener {
            val groupName: String = this.createGroupName?.text.toString()
            val groupDescription: String = this.createGroupDescription?.text.toString()
            val errors: FormError? = this.groupViewModel?.validateGroup(groupName, groupDescription, this.createGroupMaxResults, this.createGroupPlace)

            if (errors != null) {
                SnackbarGenerator.generateSnackbar(view, errors.generateErrorMessage())?.show()
            } else {
                this.groupViewModel?.createGroup()
            }
        }
    }
}