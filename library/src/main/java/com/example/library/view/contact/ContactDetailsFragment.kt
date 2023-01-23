package com.example.library.view.contact

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.java.entities.Contact
import com.example.java.entities.LocationData
import com.example.library.R
import com.example.library.databinding.FragmentContactDetailsBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.injectViewModel
import com.example.library.view.map.contact.OnContactMapCallback
import com.example.library.viewmodel.ContactDetailsViewModel
import java.util.*
import javax.inject.Inject

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    private var detailsFrag: FragmentContactDetailsBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactDetailsViewModel: ContactDetailsViewModel

    private var navigateMapCallback: OnContactMapCallback? = null

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactMapCallback) {
            navigateMapCallback = context
        } else {
            throw ClassCastException(
                context.toString() +
                    " must implement OnMapCallback!"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusContactDetailsContainer()
            ?.inject(this)
        contactDetailsViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsFrag = FragmentContactDetailsBinding.bind(view)

        detailsFrag?.toContactMapFragmentFab?.setOnClickListener {
            navigateMapCallback?.navigateToContactMapFragment(contactId)
        }
        detailsFrag?.deleteLocationDataFab?.setOnClickListener(deleteLocationDataFabListener)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            contactDetailsViewModel.getContactDetails(contactId)
                .observe(viewLifecycleOwner) { curContact ->
                    try {
                        showContactData(curContact[0])
                        detailsFrag?.birthdayButton?.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                contactDetailsViewModel.setBirthdayAlarm(curContact[0])
                            } else {
                                contactDetailsViewModel.cancelBirthdayAlarm(curContact[0])
                            }
                        }
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, "Исключение в ContactDetailsFragment: ")
                        Log.d(TAG, e.stackTraceToString())
                    }
                }
            contactDetailsViewModel.getContactLocation(contactId)
                .observe(viewLifecycleOwner) { curLocation ->
                    showLocationData(curLocation)
                }
        }
    }

    private fun showLocationData(curLocation: LocationData?) {
        if (curLocation != null) {
            with(curLocation) {
                detailsFrag?.apply {
                    addressTV.text = curLocation.address
                    deleteLocationDataFab.visibility = View.VISIBLE
                }
            }
        } else {
            detailsFrag?.apply {
                addressTV.text = "Адрес пока не задан"
            }
        }
    }

    private fun showContactData(curContact: Contact?) {
        if (curContact != null) {
            with(curContact) {
                detailsFrag?.apply {
                    nameTV.text = curContact.name
                    phoneTV.text = curContact.phone
                    emailTV.text = curContact.email
                    descriptionTV.text = curContact.description
                    birthdayTV.text = if (birthday != null) {
                        birthday!!.get(Calendar.DAY_OF_MONTH).toString() + " " +
                            birthday!!.getDisplayName(
                                Calendar.MONTH,
                                Calendar.LONG,
                                Locale.getDefault()
                            )
                    } else {
                        "День рождения пока не задан"
                    }
                    birthdayButton.isChecked = if (birthday != null) {
                        contactDetailsViewModel.isBirthdayAlarmOn(curContact)
                    } else {
                        false
                    }
//                    birthdayButton.visibility = if (birthday != null) View.VISIBLE else View.GONE
                    if (!curContact.avatarUri.isNullOrEmpty()) {
                        avatarIV.setImageURI(curContact.avatarUri!!.toUri())
                    } else {
                        avatarIV.setImageResource(R.mipmap.ic_launcher_round)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        detailsFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        navigateMapCallback = null
        super.onDetach()
    }

    private val deleteLocationDataFabListener = View.OnClickListener {
        detailsFrag?.deleteLocationDataFab?.visibility = View.GONE
        contactDetailsViewModel.deleteLocationData(contactId)
        detailsFrag?.addressTV?.text = "Адрес пока не задан"
    }

    companion object {

        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactDetailsFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
