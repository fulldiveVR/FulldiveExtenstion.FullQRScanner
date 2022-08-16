package com.full.qr.scanner.top.secure.no.extension

import androidx.appcompat.app.AppCompatActivity
import com.full.qr.scanner.top.secure.no.feature.common.dialog.ErrorDialogFragment

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
        ErrorDialogFragment.newInstance(
            this,
            error
        )
    errorDialog.show(supportFragmentManager, "")
}