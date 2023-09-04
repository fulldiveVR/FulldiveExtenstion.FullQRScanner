package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import com.full.qr.scanner.top.secure.no.model.schema.Wifi
import com.google.android.material.textfield.TextInputLayout

class CreateQrCodeWifiFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_wifi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        initEncryptionTypesSpinner()
        initNetworkNameEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        val encryption = when (fragmentView.findViewById<Spinner>(R.id.spinner_encryption).selectedItemPosition) {
            0 -> "WPA"
            1 -> "WEP"
            2 -> "nopass"
            else -> "nopass"
        }
        return Wifi(
            encryption = encryption,
            name = fragmentView.findViewById<EditText>(R.id.edit_text_network_name).textString,
            password = fragmentView.findViewById<EditText>(R.id.edit_text_password).textString,
            isHidden = fragmentView.findViewById<CheckBox>(R.id.check_box_is_hidden).isChecked
        )
    }

    private fun initEncryptionTypesSpinner() {
        fragmentView.findViewById<Spinner>(R.id.spinner_encryption).adapter =
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.fragment_create_qr_code_wifi_encryption_types,
                R.layout.item_spinner
            ).apply {
                setDropDownViewResource(R.layout.item_spinner_dropdown)
            }

        fragmentView.findViewById<Spinner>(R.id.spinner_encryption).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    fragmentView.findViewById<TextInputLayout>(R.id.text_input_layout_password).isVisible =
                        position != 2
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun initNetworkNameEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_network_name).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text_network_name)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_password)
            .addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled =
            fragmentView.findViewById<EditText>(R.id.edit_text_network_name).isNotBlank()
    }
}