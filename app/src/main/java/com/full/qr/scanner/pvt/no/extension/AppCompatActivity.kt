package com.full.qr.scanner.pvt.no.extension

import androidx.appcompat.app.AppCompatActivity
import com.full.qr.scanner.pvt.no.feature.common.dialog.ErrorDialogFragment

fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog =
        ErrorDialogFragment.newInstance(
            this,
            error
        )
    errorDialog.show(supportFragmentManager, "")
}