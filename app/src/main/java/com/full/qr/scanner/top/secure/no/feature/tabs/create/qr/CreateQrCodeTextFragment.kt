package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.text.Selection.setSelection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.barcodeParser
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import com.google.zxing.BarcodeFormat

class CreateQrCodeTextFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    companion object {
        private const val DEFAULT_TEXT_KEY = "DEFAULT_TEXT_KEY"

        fun newInstance(defaultText: String): CreateQrCodeTextFragment {
            return CreateQrCodeTextFragment().apply {
                arguments = Bundle().apply {
                    putString(DEFAULT_TEXT_KEY, defaultText)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        handleTextChanged()
        initEditText()
    }

    override fun getBarcodeSchema(): Schema {
        return barcodeParser.parseSchema(
            BarcodeFormat.QR_CODE,
            fragmentView.findViewById<EditText>(R.id.edit_text).textString
        )
    }

    private fun initEditText() {
        val defaultText = arguments?.getString(DEFAULT_TEXT_KEY).orEmpty()
        fragmentView.findViewById<EditText>(R.id.edit_text).apply {
            setText(defaultText)
            setSelection(defaultText.length)
            requestFocus()
        }
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text).addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled =
                fragmentView.findViewById<EditText>(R.id.edit_text).isNotBlank()
        }
    }
}