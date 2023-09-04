package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Bookmark
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodeBookmarkFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initTitleEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        return Bookmark(
            title = fragmentView.findViewById<EditText>(R.id.edit_text_title).textString,
            url = fragmentView.findViewById<EditText>(R.id.edit_text_url).textString
        )
    }

    private fun initTitleEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_title).requestFocus()
    }

    private fun handleTextChanged() {
        fragmentView.findViewById<EditText>(R.id.edit_text_title).addTextChangedListener { toggleCreateBarcodeButton() }
        fragmentView.findViewById<EditText>(R.id.edit_text_url).addTextChangedListener { toggleCreateBarcodeButton() }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = fragmentView.findViewById<EditText>(R.id.edit_text_title).isNotBlank() || fragmentView.findViewById<EditText>(R.id.edit_text_url).isNotBlank()
    }
}