package com.example.library.view.contacts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.library.R
import com.example.library.databinding.FragmentContactsListBinding
import com.example.library.di.HasAppComponent
import com.example.library.utils.Constants.TAG
import com.example.library.utils.injectViewModel
import com.example.library.view.contact.ContactDetailsFragment
import com.example.library.viewmodel.ContactListViewModel
import javax.inject.Inject

class ContactListFragment : Fragment(R.layout.fragment_contacts_list) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var contactListViewModel: ContactListViewModel

    private var listFrag: FragmentContactsListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusContactListContainer()
            ?.inject(this)
        contactListViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFrag = FragmentContactsListBinding.bind(view)

        val contactListAdapter = ContactListAdapter { contactId ->
            findNavController().navigate(
                R.id.action_contactListFragment_to_contactDetailsFragment,
                bundleOf(ContactDetailsFragment.CONTACT_ID to contactId)
            )
        }

        listFrag?.recyclerView?.apply {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        contactListViewModel.loadContactList(EMPTY_QUERY)
            .observe(viewLifecycleOwner) { contactList ->
                if (!contactList.isNullOrEmpty()) {
                    Log.d(TAG, "ContactListFragment обзервер сработал")
                    try {
                        contactListAdapter.submitList(contactList)
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, e.stackTraceToString())
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Contacts Not Found",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        listFrag!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchContacts(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchContacts(query)
                }
                return true
            }
        })
    }

    private fun searchContacts(query: String) {
        contactListViewModel.loadContactList(query)
    }

    override fun onDestroyView() {
        listFrag = null
        super.onDestroyView()
    }

    companion object {
        const val EMPTY_QUERY = ""
    }
}
