package com.example.library.view.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.java.entities.ShortContact
import com.example.library.R
import com.example.library.databinding.ContactListRowBinding

class ContactListAdapter(
    private val navigateTo: (String) -> Unit
) : ListAdapter<ShortContact, ContactListAdapter.ContactViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(
        contactRow = ContactListRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        navigateTo = navigateTo
    )

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactViewHolder(
        private val contactRow: ContactListRowBinding,
        private val navigateTo: (String) -> Unit
    ) : RecyclerView.ViewHolder(contactRow.root) {

        fun bind(item: ShortContact) = with(contactRow) {
            personName.text = item.name
            phoneNumber.text = item.phone
            if (!item.photoUri.isNullOrEmpty()) {
                avatar.setImageURI(item.photoUri!!.toUri())
            } else {
                avatar.setImageResource(R.mipmap.profile)
            }
            itemView.setOnClickListener {
                // absoluteAdapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) navigateTo(item.id)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ShortContact>() {

        override fun areItemsTheSame(oldItem: ShortContact, newItem: ShortContact) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ShortContact, newItem: ShortContact) =
            oldItem.name == newItem.name && oldItem.phone == newItem.phone
    }
}
