package com.example.library.view.contact

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.java.entities.Contact
import com.example.java.entities.LocationData
import com.example.library.R
import com.example.library.databinding.FragmentContactDetailsBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.injectViewModel
import com.example.library.view.map.contact.ContactMapFragment
import com.example.library.viewmodel.ContactDetailsViewModel
import java.util.*
import javax.inject.Inject

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    private lateinit var detailsFrag: FragmentContactDetailsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactDetailsViewModel: ContactDetailsViewModel

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
    private val startByNotification: Boolean by lazy {
        requireArguments().getBoolean(START_TYPE, false)
    }
    private val check: Boolean by lazy {
        requireArguments().getBoolean(CHECK, false)
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

        detailsFrag.addressChangeCard.setOnClickListener {
            if (startByNotification and check) {
                findNavController().navigate(
                    R.id.action_contactDetailsFragment2_to_contactMapFragment2,
                    bundleOf(ContactMapFragment.CONTACT_ID to contactId)
                )
            } else {
                findNavController().navigate(
                    R.id.action_contactDetailsFragment_to_contactMapFragment,
                    bundleOf(ContactMapFragment.CONTACT_ID to contactId)
                )
            }
        }
        detailsFrag.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        detailsFrag.addressDeleteCard.setOnClickListener(deleteLocationDataListener)

        contactDetailsViewModel.getContactDetails(contactId)
            .observe(viewLifecycleOwner) { curContact ->
                try {
                    showContactData(curContact[0])
                    detailsFrag.birthdaySwitch.setOnCheckedChangeListener { _, isChecked ->
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

    private fun showLocationData(curLocation: LocationData?) {
        if (curLocation != null) {
            with(curLocation) {
                detailsFrag.apply {
                    addressText.text = curLocation.address
                    addressDeleteCard.visibility = View.VISIBLE
//                    addressDeleteText.visibility = View.VISIBLE
//                    addressDelete.visibility = View.VISIBLE
                }
            }
        } else {
            detailsFrag.apply {
                addressDeleteCard.visibility = View.GONE
//                addressDelete.visibility = View.GONE
//                addressDeleteText.visibility = View.GONE
                addressText.text = "Адрес пока не задан"
            }
        }
    }

    private fun showContactData(curContact: Contact?) {
        if (curContact != null) {
            with(curContact) {
                detailsFrag.apply {
                    name.text = if (curContact.name != "") { curContact.name } else { "Пока не задан" }
                    phone.text = if (curContact.phone != "") { curContact.phone } else { "Пока не задан" }
                    email.text = if (curContact.email != "") { curContact.email } else { "Пока не задан" }
                    noteText.text = if (curContact.description != "") {
                        curContact.description } else { "Пока не задан"
                    }
                    birthdayText.text = if (birthday != null) {
                        birthday!!.get(Calendar.DAY_OF_MONTH).toString() + " " +
                            birthday!!.getDisplayName(
                                Calendar.MONTH,
                                Calendar.LONG,
                                Locale.getDefault()
                            )
                    } else {
                        "День рождения пока не задан"
                    }
                    birthdaySwitch.isChecked = if (birthday != null) {
                        contactDetailsViewModel.isBirthdayAlarmOn(curContact)
                    } else {
                        false
                    }
                    if (!curContact.avatarUri.isNullOrEmpty()) {
                        photo.setImageURI(curContact.avatarUri!!.toUri())
                    } else {
                        photo.setImageResource(R.mipmap.profile_round)
                    }
                }
            }
        }
    }

    private val deleteLocationDataListener = View.OnClickListener {
        detailsFrag.addressDeleteCard.visibility = View.GONE
//        detailsFrag?.addressDelete?.visibility = View.GONE
//        detailsFrag?.addressDeleteText?.visibility = View.GONE
        contactDetailsViewModel.deleteLocationData(contactId)
        detailsFrag.addressText.text = "Адрес пока не задан"
    }

    companion object {
        const val CONTACT_ID = "id"
        const val START_TYPE = "type"
        const val CHECK = "check"

        @JvmStatic
        fun newInstance(contactId: String, startType: Boolean, check: Boolean) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                    putBoolean(START_TYPE, startType)
                    putBoolean(CHECK, check)
                }
            }
    }
}
