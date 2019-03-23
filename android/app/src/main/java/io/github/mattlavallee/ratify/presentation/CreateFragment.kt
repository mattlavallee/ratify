package io.github.mattlavallee.ratify.presentation

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Status
import io.github.mattlavallee.ratify.R
import android.widget.NumberPicker
import android.widget.ProgressBar
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import io.github.mattlavallee.ratify.adapters.GroupVoteResultsAdapter
import io.github.mattlavallee.ratify.core.Group
import io.github.mattlavallee.ratify.core.YelpResult
import io.github.mattlavallee.ratify.data.GroupViewModel
import io.github.mattlavallee.ratify.presentation.interfaces.FragmentSwitchInterface
import io.github.mattlavallee.ratify.presentation.interfaces.UserAuthInterface
import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateFragment : Fragment(), UserAuthInterface {
    private var pendingSpinner: ProgressBar? = null

    private var groupViewModel: GroupViewModel? = null
    private var autocompleteFragment: AutocompleteSupportFragment? = null
    private var createGroupName: TextInputEditText? = null
    private var createGroupDescription: TextInputEditText? = null
    private var createGroupActivity: TextInputEditText? = null
    private var createGroupVoteConclusion: TextInputEditText? = null
    private var createGroupMaxResultsDisplay: TextInputEditText? = null
    private var createGroupExpirationDisplay: TextInputEditText? = null
    private var createCreateBtn: Button? = null
    private var createPreviewBtn: Button? = null

    private var createGroupPlace: Place? = null
    private var createGroupMaxResults: Int = -1
    private var createGroupExpirationDays: Int = -1
    private var createGroupVoteConclusionDateTime: Calendar = Calendar.getInstance()

    private var requiredFieldsToEditTextMap: HashMap<String, TextInputEditText?> = HashMap()

    private var callbackActivity: FragmentSwitchInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            val apiKey = resources.getString(R.string.places_api_key)
            Places.initialize(this.activity!!.applicationContext, apiKey)
        }
        this.groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        this.groupViewModel?.getCreatePending()?.observe(this, Observer {
            isPending -> if (isPending == true) this.pendingSpinner?.visibility = View.VISIBLE else this.pendingSpinner?.visibility = View.GONE
        })
        this.groupViewModel?.getGroupError()?.observe(this, Observer {
            result -> SnackbarGenerator.generateSnackbar(view, result ?: "Unknown Error")?.show()
        })
        this.groupViewModel?.getGroupCode()?.observe(this, Observer {
            code ->
                if (code != null) {
                    this.clearCreateGroupForm()
                    callbackActivity?.onResetToHomeFragment(code)
                } else {
                    SnackbarGenerator.generateSnackbar(view, "Shucks, something went wrong generating the group code")?.show()
                }
        })
        this.groupViewModel?.getGroupPreview()?.observe(this, Observer {
            results -> this.displayPreviewResults(results!!)
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FragmentSwitchInterface) {
            callbackActivity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbackActivity = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Create Group"
        val hasPlayServicesAccess = GoogleApiAvailability.getInstance()?.isGooglePlayServicesAvailable(activity?.applicationContext)
        if (hasPlayServicesAccess != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance()?.getErrorDialog(activity, hasPlayServicesAccess!!, 9000)?.show()
        }

        this.pendingSpinner = view.findViewById(R.id.create_group_spinner)

        this.createGroupName = view.findViewById(R.id.create_group_name)
        this.createGroupDescription = view.findViewById(R.id.create_group_description)
        this.createGroupActivity = view.findViewById(R.id.create_group_activity)
        this.createCreateBtn = view.findViewById(R.id.create_group_create_btn)
        this.createPreviewBtn = view.findViewById(R.id.create_group_preview_btn)
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        configureAutocompleteFragment()

        this.createGroupVoteConclusion = activity?.findViewById(R.id.create_group_vote_conclusion) as TextInputEditText
        configureVoteConclusion()

        this.createGroupMaxResultsDisplay = activity?.findViewById(R.id.create_group_max_results_display) as TextInputEditText
        configureMaxResults()

        this.createGroupExpirationDisplay = activity?.findViewById(R.id.create_group_expiration) as TextInputEditText
        configureExpirationDialog()
        configureCreate()
        configurePreview()

        this.requiredFieldsToEditTextMap["name"] = this.createGroupName
        this.requiredFieldsToEditTextMap["activity"] = this.createGroupActivity
        this.requiredFieldsToEditTextMap["place"] = null
        this.requiredFieldsToEditTextMap["maxResults"] = this.createGroupMaxResultsDisplay
        this.requiredFieldsToEditTextMap["voteConclusion"] = this.createGroupVoteConclusion
        this.requiredFieldsToEditTextMap["expirationDays"] = this.createGroupExpirationDisplay
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        autocompleteFragment?.onActivityResult(requestCode, resultCode, data)

        // handle selection from place autocomplete
        when (resultCode) {
            Activity.RESULT_OK -> {
                this.createGroupPlace = Autocomplete.getPlaceFromIntent(data!!)
            }
            AutocompleteActivity.RESULT_ERROR -> {
                val status = Autocomplete.getStatusFromIntent(data!!)
                SnackbarGenerator.generateSnackbar(view, "Error fetching location: " + status.statusMessage)?.show()
                this.createGroupPlace = null
            }
            Activity.RESULT_CANCELED -> {
                Log.i("Ratify", "User cancelled place search")
            }
        }
    }

    override fun onUserAuthError() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onUserAuthSuccess() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    /*
     * Configures the place autocomplete fragment.
     * Sets autocomplete listener and type filter
     */
    private fun configureAutocompleteFragment() {
        autocompleteFragment?.setHint("Search Location")
        autocompleteFragment?.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment?.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(selectedPlace: Place) {
                createGroupPlace = selectedPlace
            }

            override fun onError(status: Status) {
                SnackbarGenerator.generateSnackbar(view, "Error fetching location")?.show()
            }
        })
    }

    /*
     * Configures vote conclusion edit text to launch date and time picker dialogs
     * Updates local variable with the modified datetime associated with the dialogs
     */
    private fun configureVoteConclusion() {
        this.createGroupVoteConclusionDateTime.time = Date(Long.MIN_VALUE)
        val voteConclusionCalendar: Calendar = Calendar.getInstance()
        val timeListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
            voteConclusionCalendar.set(Calendar.HOUR_OF_DAY, hours)
            voteConclusionCalendar.set(Calendar.MINUTE, minutes)

            this.createGroupVoteConclusionDateTime = voteConclusionCalendar
            val dateFormat = SimpleDateFormat("M/dd/yyyy h:mm a", Locale.US)
            this.createGroupVoteConclusion?.setText(dateFormat.format(this.createGroupVoteConclusionDateTime.time))
            this.createGroupVoteConclusion?.error = null
        }
        val dateListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { v, year, monthOfYear, dayOfMonth ->
            voteConclusionCalendar.set(Calendar.YEAR, year)
            voteConclusionCalendar.set(Calendar.MONTH, monthOfYear)
            voteConclusionCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            TimePickerDialog(v.context, 0, timeListener, voteConclusionCalendar.get(Calendar.HOUR_OF_DAY), voteConclusionCalendar.get(Calendar.MINUTE), false).show()
        }

        this.createGroupVoteConclusion?.setOnClickListener {
            val dateDialog = DatePickerDialog(context!!, 0, dateListener, voteConclusionCalendar.get(Calendar.YEAR),
                    voteConclusionCalendar.get(Calendar.MONTH), voteConclusionCalendar.get(Calendar.DAY_OF_MONTH));
            dateDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dateDialog.show()
        }
    }

    /*
     * Configure max results number picker dialog
     */
    private fun configureMaxResults() {
        val maxResults = NumberPicker(activity)
        maxResults.maxValue = 20
        maxResults.minValue = 1
        maxResults.value = 10

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Max Results to Display")
        builder.setView(maxResults)

        val listener = DialogInterface.OnClickListener { _, _ ->
            this.createGroupMaxResults = maxResults.value
            this.createGroupMaxResultsDisplay?.setText(this.createGroupMaxResults.toString())
            this.createGroupMaxResultsDisplay?.error = null
        }

        builder.setPositiveButton("OK", listener)
        builder.setNegativeButton("Cancel") { _, _ -> }
        val numberPickerDialog = builder.create() as AlertDialog
        this.createGroupMaxResultsDisplay?.setOnClickListener {
            numberPickerDialog.show()
        }
    }

    /*
     * Configure number picker for number of days after vote conclusion to expire the group (deactivate)
     */
    private fun configureExpirationDialog() {
        val expirationTime = NumberPicker(activity)
        expirationTime.maxValue = 7
        expirationTime.minValue = 1
        expirationTime.value = 3

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Days After RSVP to Expire")
        builder.setView(expirationTime)

        val clickListener = DialogInterface.OnClickListener { _, _ ->
            this.createGroupExpirationDays = expirationTime.value
            this.createGroupExpirationDisplay?.setText(this.createGroupExpirationDays.toString() + " days")
            this.createGroupExpirationDisplay?.error = null
        }
        builder.setPositiveButton("OK", clickListener)
        builder.setNegativeButton("Cancel") { _, _ -> }
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
            val groupActivity: String = this.createGroupActivity?.text.toString()

            val newGroup = Group("", "restaurant", groupName, groupDescription, groupActivity, this.createGroupPlace,
                    this.createGroupMaxResults, this.createGroupVoteConclusionDateTime.time, this.createGroupExpirationDays)
            var missingFields = this.groupViewModel?.validateGroup(newGroup)

            if (missingFields == null) missingFields = ArrayList()
            if (missingFields.size > 0) {
                for (field: String in missingFields) {
                    if (field == "place") {
                        SnackbarGenerator.generateSnackbar(view, "A starting location must be chosen")?.show()
                    } else {
                        this.requiredFieldsToEditTextMap[field]?.error = "Required field!"
                    }
                }
            } else {
                this.groupViewModel?.createGroup(newGroup)
            }
        }
    }

    private fun configurePreview() {
        this.createPreviewBtn?.setOnClickListener {
            val groupActivity: String = this.createGroupActivity?.text.toString()
            if (groupActivity.isEmpty() || this.createGroupPlace == null || this.createGroupMaxResults <= 0) {
                SnackbarGenerator.generateSnackbar(view, "You must include an activity, location, and max results to preview")?.show()
            } else {
                this.groupViewModel?.previewResults(groupActivity, this.createGroupPlace?.latLng?.latitude!!,
                        this.createGroupPlace?.latLng?.longitude!!, this.createGroupMaxResults)
            }
        }
    }

    private fun clearCreateGroupForm() {
        this.createGroupName?.setText("")
        this.createGroupDescription?.setText("")
        this.createGroupActivity?.setText("")
        this.createGroupPlace = null
        this.createGroupMaxResults = -1
        this.createGroupMaxResultsDisplay?.setText("")
        this.createGroupExpirationDays = -1
        this.createGroupExpirationDisplay?.setText("")
        this.createGroupVoteConclusionDateTime = Calendar.getInstance()
        this.createGroupVoteConclusion?.setText("")
    }

    private fun displayPreviewResults(items: ArrayList<YelpResult>) {
        var builder = android.support.v7.app.AlertDialog.Builder(context!!)
        val previewResultsAdapter = GroupVoteResultsAdapter(items, mutableMapOf(), true)
        val previewResultsView = activity?.layoutInflater?.inflate(R.layout.preview_results_layout, null)
        previewResultsView!!.findViewById<RecyclerView>(R.id.preview_results_list_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = previewResultsAdapter
        }
        builder.setView(previewResultsView)
                .setPositiveButton("OK", { dialog, _ -> dialog.cancel() })
        builder.create().show()
    }
}