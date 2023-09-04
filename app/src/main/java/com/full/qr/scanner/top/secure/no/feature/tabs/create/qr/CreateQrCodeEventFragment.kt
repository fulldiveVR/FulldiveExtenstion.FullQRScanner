package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.common.view.DateTimePickerButton
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import com.full.qr.scanner.top.secure.no.model.schema.VEvent

class CreateQrCodeEventFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_vevent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        fragmentView.findViewById<EditText>(R.id.edit_text_title).requestFocus()
        parentActivity.isCreateBarcodeButtonEnabled = true
    }

    override fun getBarcodeSchema(): Schema {
        with(fragmentView) {
            return VEvent(
                uid = findViewById<EditText>(R.id.edit_text_title).textString,
                organizer = findViewById<EditText>(R.id.edit_text_organizer).textString,
                summary = findViewById<EditText>(R.id.edit_text_summary).textString,
                startDate = findViewById<DateTimePickerButton>(R.id.button_date_time_start).dateTime,
                endDate = findViewById<DateTimePickerButton>(R.id.button_date_time_end).dateTime
            )
        }
    }
}