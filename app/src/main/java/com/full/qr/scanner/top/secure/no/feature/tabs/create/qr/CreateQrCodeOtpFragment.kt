package com.full.qr.scanner.top.secure.no.feature.tabs.create.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.encodeBase32
import com.full.qr.scanner.top.secure.no.extension.isNotBlank
import com.full.qr.scanner.top.secure.no.extension.textString
import com.full.qr.scanner.top.secure.no.extension.toHmacAlgorithm
import com.full.qr.scanner.top.secure.no.feature.tabs.create.BaseCreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.model.schema.OtpAuth
import com.full.qr.scanner.top.secure.no.model.schema.Schema
import com.google.android.material.textfield.TextInputLayout
import dev.turingcomplete.kotlinonetimepassword.RandomSecretGenerator
import java.util.*

class CreateQrCodeOtpFragment : BaseCreateBarcodeFragment() {
    private val randomGenerator = RandomSecretGenerator()
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view

        initOtpTypesSpinner()
        initAlgorithmsSpinner()
        initEditTexts()
        initGenerateRandomSecretButton()
        showRandomSecret()
    }

    override fun getBarcodeSchema(): Schema {
        return OtpAuth(
            type = fragmentView.findViewById<Spinner>(R.id.spinner_opt_types).selectedItem?.toString()
                ?.toLowerCase(Locale.ENGLISH),
            algorithm = fragmentView.findViewById<Spinner>(R.id.spinner_algorithms).selectedItem?.toString(),
            label = if (fragmentView.findViewById<EditText>(R.id.edit_text_issuer).isNotBlank()) {
                "${fragmentView.findViewById<EditText>(R.id.edit_text_issuer).textString}:${
                    fragmentView.findViewById<EditText>(
                        R.id.edit_text_account
                    ).textString
                }"
            } else {
                fragmentView.findViewById<EditText>(R.id.edit_text_account).textString
            },
            issuer = fragmentView.findViewById<EditText>(R.id.edit_text_issuer).textString,
            digits = fragmentView.findViewById<EditText>(R.id.edit_text_digits).textString.toIntOrNull(),
            period = fragmentView.findViewById<EditText>(R.id.edit_text_period).textString.toLongOrNull(),
            counter = fragmentView.findViewById<EditText>(R.id.edit_text_counter).textString.toLongOrNull(),
            secret = fragmentView.findViewById<EditText>(R.id.edit_text_secret).textString
        )
    }

    private fun initOtpTypesSpinner() {
        with(fragmentView) {
            findViewById<Spinner>(R.id.spinner_opt_types).adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.fragment_create_qr_code_otp_types, R.layout.item_spinner
            ).apply {
                setDropDownViewResource(R.layout.item_spinner_dropdown)
            }

            findViewById<Spinner>(R.id.spinner_opt_types).onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        findViewById<TextInputLayout>(R.id.text_input_layout_counter).isVisible =
                            position == 0
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
        }
    }

    private fun initAlgorithmsSpinner() {
        with(fragmentView) {
            findViewById<Spinner>(R.id.spinner_algorithms).adapter =
                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.fragment_create_qr_code_otp_algorithms,
                    R.layout.item_spinner
                ).apply {
                    setDropDownViewResource(R.layout.item_spinner_dropdown)
                }
        }
    }

    private fun initEditTexts() {
        with(fragmentView) {
            findViewById<EditText>(R.id.edit_text_account).addTextChangedListener { toggleCreateBarcodeButton() }
            findViewById<EditText>(R.id.edit_text_secret).addTextChangedListener { toggleCreateBarcodeButton() }
            findViewById<EditText>(R.id.edit_text_period).addTextChangedListener { toggleCreateBarcodeButton() }
            findViewById<EditText>(R.id.edit_text_counter).addTextChangedListener { toggleCreateBarcodeButton() }
        }
    }

    private fun initGenerateRandomSecretButton() {
        fragmentView.findViewById<Button>(R.id.button_generate_random_secret).setOnClickListener {
            showRandomSecret()
        }
    }

    private fun toggleCreateBarcodeButton() {
        with(fragmentView) {
            val isHotp = findViewById<Spinner>(R.id.spinner_opt_types).selectedItemPosition == 0
            val areGeneralFieldsNotBlank =
                fragmentView.findViewById<EditText>(R.id.edit_text_account).isNotBlank() &&
                    fragmentView.findViewById<EditText>(R.id.edit_text_secret).isNotBlank()
            val areHotpFieldsNotBlank =
                fragmentView.findViewById<EditText>(R.id.edit_text_counter).isNotBlank() &&
                    fragmentView.findViewById<EditText>(R.id.edit_text_period).isNotBlank()
            parentActivity.isCreateBarcodeButtonEnabled =
                areGeneralFieldsNotBlank && (isHotp.not() || isHotp && areHotpFieldsNotBlank)
        }
    }

    private fun showRandomSecret() {
        fragmentView.findViewById<EditText>(R.id.edit_text_secret).setText(generateRandomSecret())
    }

    private fun generateRandomSecret(): String {
        val algorithm =
            fragmentView.findViewById<Spinner>(R.id.spinner_algorithms).selectedItem?.toString()
                .toHmacAlgorithm()
        val secret = randomGenerator.createRandomSecret(algorithm)
        return secret.encodeBase32()
    }
}