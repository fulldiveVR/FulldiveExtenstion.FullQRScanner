package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Email
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodeEmailFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initTitleEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        return Email(
            email = fragmentView.findViewById<EditText>(R.id.edit_text_email).textString,
            subject = fragmentView.findViewById<EditText>(R.id.edit_text_subject).textString,
            body = fragmentView.findViewById<EditText>(R.id.edit_text_message).textString
        )
    }

    private fun initTitleEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_email).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text_email)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_subject)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_message)
            .addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled =
            fragmentView.findViewById<EditText>(R.id.edit_text_email)
                .isNotBlank() || fragmentView.findViewById<EditText>(
                R.id.edit_text_subject
            ).isNotBlank() || fragmentView.findViewById<EditText>(R.id.edit_text_message)
                .isNotBlank()
    }
}