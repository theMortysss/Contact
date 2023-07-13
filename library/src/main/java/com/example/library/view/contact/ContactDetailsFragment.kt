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

    private var detailsFrag: FragmentContactDetailsBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactDetailsViewModel: ContactDetailsViewModel

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
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

        detailsFrag?.addressChangeText?.setOnClickListener {
            findNavController().navigate(
                R.id.action_contactDetailsFragment_to_contactMapFragment,
                bundleOf(ContactMapFragment.CONTACT_ID to contactId)
            )
        }
        detailsFrag?.topAppBar?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        detailsFrag?.addressDeleteText?.setOnClickListener(deleteLocationDataListener)

        contactDetailsViewModel.getContactDetails(contactId)
            .observe(viewLifecycleOwner) { curContact ->
                try {
                    showContactData(curContact[0])
                    detailsFrag?.birthdaySwitch?.setOnCheckedChangeListener { _, isChecked ->
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
                detailsFrag?.apply {
                    addressText.text = curLocation.address
                    addressDeleteText.visibility = View.VISIBLE
                    addressDelete.visibility = View.VISIBLE
                }
            }
        } else {
            detailsFrag?.apply {
                addressDelete.visibility = View.GONE
                addressDeleteText.visibility = View.GONE
                addressText.text = "Адрес пока не задан"
            }
        }
    }

    private fun showContactData(curContact: Contact?) {
        if (curContact != null) {
            with(curContact) {
                detailsFrag?.apply {
                    name.text = curContact.name
                    phone.text = curContact.phone
                    email.text = curContact.email
                    noteText.text = curContact.description
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
                        Log.d(TAG, "Свитч = ${contactDetailsViewModel.isBirthdayAlarmOn(curContact)}")
                        contactDetailsViewModel.isBirthdayAlarmOn(curContact)
                    } else {
                        false
                    }
//                    birthdayButton.visibility = if (birthday != null) View.VISIBLE else View.GONE
                    if (!curContact.avatarUri.isNullOrEmpty()) {
                        photo.setImageURI(curContact.avatarUri!!.toUri())
                    } else {
                        photo.setImageResource(R.mipmap.profile_round)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        detailsFrag = null
        super.onDestroyView()
    }

    private val deleteLocationDataListener = View.OnClickListener {
        detailsFrag?.addressDelete?.visibility = View.GONE
        detailsFrag?.addressDeleteText?.visibility = View.GONE
        contactDetailsViewModel.deleteLocationData(contactId)
        detailsFrag?.addressText?.text = "Адрес пока не задан"
    }

    companion object {

        const val CONTACT_ID = "id"
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
