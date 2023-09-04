package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.text.Selection.setSelection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Phone
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodePhoneFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        initEditText()
        handleTextChanged()
    }

    override fun showPhone(phone: String) {
        fragmentView.findViewById<EditText>(R.id.edit_text).apply {
            setText(phone)
            setSelection(phone.length)
        }
    }

    override fun getBarcodeSchema(): Schema {
        return Phone(fragmentView.findViewById<EditText>(R.id.edit_text).textString)
    }

    private fun initEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text).addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = fragmentView.findViewById<EditText>(R.id.edit_text).isNotBlank()
        }
    }
}