package com.full.qr.scanner.top.secure.no.feature.tabs.settings.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.settings
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.unsafeLazy
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.common.view.SettingsRadioButton
import com.full.qr.scanner.top.secure.no.usecase.Settings

class ChooseThemeActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseThemeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val buttons by unsafeLazy {
        listOf(
            findViewById<SettingsRadioButton>(R.id.button_system_theme),
            findViewById<SettingsRadioButton>(R.id.button_light_theme),
            findViewById<SettingsRadioButton>(R.id.button_dark_theme)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_theme)
        supportEdgeToEdge()
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        showInitialSettings()
        handleSettingsChanged()
    }

    private fun supportEdgeToEdge() {
        findViewById<CoordinatorLayout>(R.id.root_view).applySystemWindowInsets(
            applyTop = true,
            applyBottom = true
        )
    }

    private fun initToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
    }

    private fun showInitialSettings() {
        val theme = settings.theme
        findViewById<SettingsRadioButton>(R.id.button_system_theme).isChecked =
            theme == Settings.THEME_SYSTEM
        findViewById<SettingsRadioButton>(R.id.button_light_theme).isChecked =
            theme == Settings.THEME_LIGHT
        findViewById<SettingsRadioButton>(R.id.button_dark_theme).isChecked =
            theme == Settings.THEME_DARK
    }

    private fun handleSettingsChanged() {
        findViewById<SettingsRadioButton>(R.id.button_system_theme)?.let { button_system_theme ->
            button_system_theme.setCheckedChangedListener { isChecked ->
                if (isChecked.not()) {
                    return@setCheckedChangedListener
                }

                uncheckOtherButtons(button_system_theme)
                settings.theme = Settings.THEME_SYSTEM
            }

            findViewById<SettingsRadioButton>(R.id.button_light_theme).setCheckedChangedListener { isChecked ->
                if (isChecked.not()) {
                    return@setCheckedChangedListener
                }

                uncheckOtherButtons(findViewById<SettingsRadioButton>(R.id.button_light_theme))
                settings.theme = Settings.THEME_LIGHT
            }

            findViewById<SettingsRadioButton>(R.id.button_dark_theme).setCheckedChangedListener { isChecked ->
                if (isChecked.not()) {
                    return@setCheckedChangedListener
                }

                uncheckOtherButtons(findViewById<SettingsRadioButton>(R.id.button_dark_theme))
                settings.theme = Settings.THEME_DARK
            }
        }
    }

    private fun uncheckOtherButtons(checkedButton: View) {
        buttons.forEach { button ->
            if (checkedButton !== button) {
                button.isChecked = false
            }
        }
    }
}