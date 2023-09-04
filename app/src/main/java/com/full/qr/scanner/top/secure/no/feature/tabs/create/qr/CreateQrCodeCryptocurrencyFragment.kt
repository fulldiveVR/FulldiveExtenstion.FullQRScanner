package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.Cryptocurrency
import com.full.qr.scanner.top.secure.no.model.schema.Schema

class CreateQrCodeCryptocurrencyFragment : BaseCreateBarcodeFragment() {
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_cryptocurrency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initCryptocurrenciesSpinner()
        initAddressEditText()
        handleTextChanged()
    }

    override fun getBarcodeSchema(): Schema {
        val cryptocurrency =
            when (fragmentView.findViewById<Spinner>(R.id.spinner_cryptocurrency).selectedItemPosition) {
                0 -> "bitcoin"
                1 -> "bitcoincash"
                2 -> "ethereum"
                3 -> "litecoin"
                4 -> "dash"
                else -> "bitcoin"
            }
        return Cryptocurrency(
            cryptocurrency = cryptocurrency,
            address = fragmentView.findViewById<EditText>(R.id.edit_text_address).textString,
            label = fragmentView.findViewById<EditText>(R.id.edit_text_label).textString,
            amount = fragmentView.findViewById<EditText>(R.id.edit_text_amount).textString,
            message = fragmentView.findViewById<EditText>(R.id.edit_text_message).textString
        )
    }

    private fun initCryptocurrenciesSpinner() {
        fragmentView.findViewById<Spinner>(R.id.spinner_cryptocurrency).adapter =
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.fragment_create_qr_code_cryptocurrencies,
                R.layout.item_spinner
            ).apply {
                setDropDownViewResource(R.layout.item_spinner_dropdown)
            }
    }

    private fun initAddressEditText() {
        fragmentView.findViewById<EditText>(R.id.edit_text_address).requestFocus()
    }

    private fun handleTextChanged() {
        listOf(
            fragmentView.findViewById<EditText>(R.id.edit_text_address) ,
            fragmentView.findViewById<EditText>(R.id.edit_text_amount),
            fragmentView.findViewById<EditText>(R.id.edit_text_label),
            fragmentView.findViewById<EditText>(R.id.edit_text_message)
        ).forEach { editText ->
            editText.addTextChangedListener { toggleCreateBarcodeButton() }
        }
    }

    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = fragmentView.findViewById<EditText>(R.id.edit_text_address).isNotBlank()
    }
}