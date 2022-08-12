package com.full.qr.scanner.pvt.no

import androidx.multidex.MultiDexApplication
import com.full.qr.scanner.pvt.no.di.settings
import com.full.qr.scanner.pvt.no.usecase.Logger
import io.reactivex.plugins.RxJavaPlugins

class App : MultiDexApplication() {

    override fun onCreate() {
        handleUnhandledRxJavaErrors()
        applyTheme()
        super.onCreate()
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }

    private fun handleUnhandledRxJavaErrors() {
        RxJavaPlugins.setErrorHandler { error ->
            Logger.log(error)
        }
    }
}