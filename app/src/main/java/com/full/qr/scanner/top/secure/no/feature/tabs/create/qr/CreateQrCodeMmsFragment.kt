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
import com.full.qr.scanner.top.secure.no.model.schema.Mms
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodeMmsFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_mms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initTitleEditText()
        handleTextChanged()
    }

    override fun showPhone(phone: String) {
        fragmentView.findViewById<EditText>(R.id.edit_text_phone).apply {
            setText(phone)
            setSelection(phone.length)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return Mms(
            phone = fragmentView.findViewById<EditText>(R.id.edit_text_phone).textString,
            subject = fragmentView.findViewById<EditText>(R.id.edit_text_subject).textString,
            message = fragmentView.findViewById<EditText>(R.id.edit_text_message).textString
        )
    }

    private fun initTitleEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_phone).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text_phone)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_subject)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_message)
            .addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled =
            fragmentView.findViewById<EditText>(R.id.edit_text_phone).isNotBlank() ||
                fragmentView.findViewById<EditText>(R.id.edit_text_subject).isNotBlank() ||
                fragmentView.findViewById<EditText>(R.id.edit_text_message).isNotBlank()
    }
}