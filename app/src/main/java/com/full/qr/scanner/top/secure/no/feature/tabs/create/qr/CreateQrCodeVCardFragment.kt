package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.Contact
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import com.full.qr.scanner.top.secure.no.model.schema.VCard

class CreateQrCodeVCardFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_vcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        fragmentView.findViewById<EditText>(R.id.edit_text_first_name).requestFocus()
        parentActivity.isCreateBarcodeButtonEnabled = true
    }

    override fun getBarcodeSchema(): Schema {
       return VCard(
           firstName = fragmentView.findViewById<EditText>(R.id.edit_text_first_name).textString,
           lastName = fragmentView.findViewById<EditText>(R.id.edit_text_last_name).textString,
           organization = fragmentView.findViewById<EditText>(R.id.edit_text_organization).textString,
           title = fragmentView.findViewById<EditText>(R.id.edit_text_job).textString,
           email = fragmentView.findViewById<EditText>(R.id.edit_text_email).textString,
           phone = fragmentView.findViewById<EditText>(R.id.edit_text_phone).textString,
           secondaryPhone = fragmentView.findViewById<EditText>(R.id.edit_text_fax).textString,
           url = fragmentView.findViewById<EditText>(R.id.edit_text_website).textString
       )
    }

    override fun showContact(contact: Contact) {
        fragmentView.findViewById<EditText>(R.id.edit_text_first_name).setText(contact.firstName)
        fragmentView.findViewById<EditText>(R.id.edit_text_last_name).setText(contact.lastName)
        fragmentView.findViewById<EditText>(R.id.edit_text_email).setText(contact.email)
        fragmentView.findViewById<EditText>(R.id.edit_text_phone).setText(contact.phone)
    }
}