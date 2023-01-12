package com.example.library.view.map

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.library.view.map.route.SELECTED_CONTACT_ID

const val DIALOG_REQUEST = "dialog_request"

class ContactDialogFragment : DialogFragment() {

    private val contactList: ArrayList<String>? by lazy {
        requireArguments().getStringArrayList(CONTACT_LIST)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contactListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            contactList ?: emptyList()
        )

        return AlertDialog.Builder(requireContext())
            .setTitle("Выберите второй контакт")
            .setAdapter(contactListAdapter) { _, i ->
                setFragmentResult(
                    DIALOG_REQUEST, bundleOf(SELECTED_CONTACT_ID to i)
                )
            }
            .create()
    }

    companion object {
        private const val CONTACT_LIST = "list"

        @JvmStatic
        fun newInstance(contactList: ArrayList<String>) =
            ContactDialogFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(CONTACT_LIST, contactList)
                }
            }
    }
}
