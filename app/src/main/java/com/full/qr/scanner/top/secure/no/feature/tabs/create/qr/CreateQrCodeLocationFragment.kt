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
import com.full.qr.scanner.top.secure.no.model.schema.Geo
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodeLocationFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override val latitude: Double?
        get() = fragmentView.findViewById<EditText>(R.id.edit_text_latitude).textString.toDoubleOrNull()

    override val longitude: Double?
        get() = fragmentView.findViewById<EditText>(R.id.edit_text_longitude).textString.toDoubleOrNull()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initLatitudeEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        return Geo(
            latitude = fragmentView.findViewById<EditText>(R.id.edit_text_latitude).textString,
            longitude = fragmentView.findViewById<EditText>(R.id.edit_text_longitude).textString,
            altitude = fragmentView.findViewById<EditText>(R.id.edit_text_altitude).textString
        )
    }

    override fun showLocation(latitude: Double?, longitude: Double?) {
        latitude?.apply {
            fragmentView.findViewById<EditText>(R.id.edit_text_latitude)
                .setText(latitude.toString())
        }
        longitude?.apply {
            fragmentView.findViewById<EditText>(R.id.edit_text_longitude)
                .setText(longitude.toString())
        }
    }

    private fun initLatitudeEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_latitude).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text_latitude)
            .addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_longitude)
            .addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled =
            fragmentView.findViewById<EditText>(R.id.edit_text_latitude)
                .isNotBlank() && fragmentView.findViewById<EditText>(R.id.edit_text_longitude)
                .isNotBlank()
    }
}