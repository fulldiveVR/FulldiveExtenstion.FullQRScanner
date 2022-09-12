package com.full.qr.scanner.top.secure.no.usecase

import com.full.qr.scanner.top.secure.no.BuildConfig

object Logger {
    var isEnabled = BuildConfig.ERROR_REPORTS_ENABLED_BY_DEFAULT

    fun log(error: Throwable) {
    }
}