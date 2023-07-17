package com.example.library.repository.contacts

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.example.java.entities.Contact
import com.example.java.entities.ShortContact
import com.example.java.interfaces.IContactsRepository
import com.example.library.utils.Constants.EMPTY_VALUE
import com.example.library.utils.Constants.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

private val CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI
private val DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI
private val EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI
private val PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

private val CONTACTS_PROJECTION = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.Contacts.PHOTO_URI
)
private const val SELECTION_CONTACTS = ContactsContract.Contacts._ID + " =?"

private val PHONE_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
private const val SELECTION_PHONE = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?"

private val EMAIL_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS)
private const val SELECTION_MAIL = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " =?"

private val DESCRIPTION_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Note.NOTE)
private const val SELECTION_DESCRIPTION = ContactsContract.Data.RAW_CONTACT_ID + "=?" +
    " AND " + ContactsContract.Data.MIMETYPE + "='" +
    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE + "'"

private val BIRTHDAY_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE)
private const val SELECTION_BIRTHDAY = ContactsContract.Data.CONTACT_ID + "=?" +
    " AND " + ContactsContract.Data.MIMETYPE + " = '" +
    ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE +
    "' AND " + ContactsContract.CommonDataKinds.Event.TYPE +
    " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY

private const val ALPHABET_SORT = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
private const val CONTACT_ID = ContactsContract.Contacts._ID
private const val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME
private const val PHOTO_URI = ContactsContract.Contacts.PHOTO_URI
private const val CONTACT_PHONE = ContactsContract.CommonDataKinds.Phone.NUMBER
private const val EMAIL_ADDRESS = ContactsContract.CommonDataKinds.Email.ADDRESS

class ContactsRepository(private val contentResolver: ContentResolver) :
    IContactsRepository {

    override suspend fun getContacts(query: String): List<ShortContact> =
        withContext(Dispatchers.IO) {
            val shortContactList = mutableListOf<ShortContact>()
            contentResolver.query(
                CONTACTS_CONTENT_URI,
                CONTACTS_PROJECTION,
                null,
                null,
                ALPHABET_SORT
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_ID))
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
                        val phone = getContactPhone(id)
                        val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_URI))
                        if (name.uppercase().contains(query.uppercase())) {
                            shortContactList.add(
                                ShortContact(
                                    id = id,
                                    name = name,
                                    phone = phone,
                                    photoUri = photoUri
                                )
                            )
                        }
                    } while (cursor.moveToNext())
                }
            }
            return@withContext shortContactList
        }

    override suspend fun getContactDetails(contactId: String): List<Contact> =
        withContext(Dispatchers.IO) {
            val contact = mutableListOf<Contact>()
            contentResolver.query(
                CONTACTS_CONTENT_URI,
                CONTACTS_PROJECTION,
                SELECTION_CONTACTS,
                arrayOf(contactId),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
                        val birthday = getContactBirthday(contactId)
                        Log.d(TAG, "birthday = $birthday")
                        val phone = getContactPhone(contactId)
                        val email = getContactEmail(contactId)
                        val description = getContactDescription(contactId)
                        val avatarUri = cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_URI))
                        contact.add(
                            Contact(
                                id = contactId,
                                name = name,
                                birthday = birthday,
                                phone = phone,
                                email = email,
                                description = description,
                                avatarUri = avatarUri
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
            return@withContext contact
        }

    override suspend fun getShortContact(contactId: String): ShortContact? =
        withContext(Dispatchers.IO) {
            contentResolver.query(
                CONTACTS_CONTENT_URI,
                CONTACTS_PROJECTION,
                SELECTION_CONTACTS,
                arrayOf(contactId),
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    ShortContact(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME)),
                        phone = EMPTY_VALUE,
                        photoUri = cursor.getString(cursor.getColumnIndexOrThrow(PHOTO_URI))
                    )
                } else {
                    null
                }
            }
        }

    private fun getContactEmail(contactId: String): String {
        var emails = EMPTY_VALUE
        contentResolver.query(
            EMAIL_CONTENT_URI,
            EMAIL_PROJECTION,
            SELECTION_MAIL,
            arrayOf(contactId),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    emails = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_ADDRESS))
                } while (cursor.moveToNext())
            }
        }
        return emails
    }

    private fun getContactPhone(contactId: String): String {
        var phone = EMPTY_VALUE
        contentResolver.query(
            PHONE_CONTENT_URI,
            PHONE_PROJECTION,
            SELECTION_PHONE,
            arrayOf(contactId),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_PHONE))
                } while (cursor.moveToNext())
            }
        }
        return phone
    }

    private fun getContactBirthday(id: String): Calendar? {
        var birthday: Calendar? = null
        contentResolver.query(
            DATA_CONTENT_URI,
            BIRTHDAY_PROJECTION,
            SELECTION_BIRTHDAY,
            arrayOf(id),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                birthday = cursor.getString(0)
                    .let { date ->
                        Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_MONTH, date.toString().substring(6,8).toInt())
                            set(Calendar.MONTH, date.toString().substring(4,6).toInt() - 1)
                            set(Calendar.YEAR, date.toString().substring(0,4).toInt())
                        }
                    }
            }
        }
        return birthday
    }

    private fun getContactDescription(contactId: String): String {
        var note = EMPTY_VALUE
        contentResolver.query(
            DATA_CONTENT_URI,
            DESCRIPTION_PROJECTION,
            SELECTION_DESCRIPTION,
            arrayOf(contactId),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getType(0) == Cursor.FIELD_TYPE_STRING) {
                        note = cursor.getString(0)
                    }
                } while (cursor.moveToNext())
            }
        }
        Log.d(TAG, "note = $note")
        return note
    }
}
