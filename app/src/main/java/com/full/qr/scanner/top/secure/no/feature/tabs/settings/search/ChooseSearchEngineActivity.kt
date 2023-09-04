package com.full.qr.scanner.top.secure.no.feature.tabs.settings.search

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
import com.full.qr.scanner.top.secure.no.model.SearchEngine

class ChooseSearchEngineActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseSearchEngineActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val buttons by unsafeLazy {
        listOf(
            findViewById<SettingsRadioButton>(R.id.button_none),
            findViewById<SettingsRadioButton>(R.id.button_ask_every_time),
            findViewById<SettingsRadioButton>(R.id.button_bing),
            findViewById<SettingsRadioButton>(R.id.button_duck_duck_go),
            findViewById<SettingsRadioButton>(R.id.button_google),
            findViewById<SettingsRadioButton>(R.id.button_qwant),
            findViewById<SettingsRadioButton>(R.id.button_startpage),
            findViewById<SettingsRadioButton>(R.id.button_yahoo),
            findViewById<SettingsRadioButton>(R.id.button_yandex)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_search_engine)
        supportEdgeToEdge()
        initToolbar()
        showInitialValue()
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

    private fun showInitialValue() {
        when (settings.searchEngine) {
            SearchEngine.NONE -> findViewById<SettingsRadioButton>(R.id.button_none).isChecked =
                true

            SearchEngine.ASK_EVERY_TIME -> findViewById<SettingsRadioButton>(R.id.button_ask_every_time).isChecked =
                true

            SearchEngine.BING -> findViewById<SettingsRadioButton>(R.id.button_bing).isChecked =
                true

            SearchEngine.DUCK_DUCK_GO -> findViewById<SettingsRadioButton>(R.id.button_duck_duck_go).isChecked =
                true

            SearchEngine.GOOGLE -> findViewById<SettingsRadioButton>(R.id.button_google).isChecked =
                true

            SearchEngine.QWANT -> findViewById<SettingsRadioButton>(R.id.button_qwant).isChecked =
                true

            SearchEngine.STARTPAGE -> findViewById<SettingsRadioButton>(R.id.button_startpage).isChecked =
                true

            SearchEngine.YAHOO -> findViewById<SettingsRadioButton>(R.id.button_yahoo).isChecked =
                true

            SearchEngine.YANDEX -> findViewById<SettingsRadioButton>(R.id.button_yandex).isChecked =
                true
        }
    }

    private fun handleSettingsChanged() {
        findViewById<SettingsRadioButton>(R.id.button_none).setCheckedChangedListener(SearchEngine.NONE)
        findViewById<SettingsRadioButton>(R.id.button_ask_every_time).setCheckedChangedListener(
            SearchEngine.ASK_EVERY_TIME
        )
        findViewById<SettingsRadioButton>(R.id.button_bing).setCheckedChangedListener(SearchEngine.BING)
        findViewById<SettingsRadioButton>(R.id.button_duck_duck_go).setCheckedChangedListener(
            SearchEngine.DUCK_DUCK_GO
        )
        findViewById<SettingsRadioButton>(R.id.button_google).setCheckedChangedListener(SearchEngine.GOOGLE)
        findViewById<SettingsRadioButton>(R.id.button_qwant).setCheckedChangedListener(SearchEngine.QWANT)
        findViewById<SettingsRadioButton>(R.id.button_startpage).setCheckedChangedListener(
            SearchEngine.STARTPAGE
        )
        findViewById<SettingsRadioButton>(R.id.button_yahoo).setCheckedChangedListener(SearchEngine.YAHOO)
        findViewById<SettingsRadioButton>(R.id.button_yandex).setCheckedChangedListener(SearchEngine.YANDEX)
    }

    private fun SettingsRadioButton.setCheckedChangedListener(searchEngine: SearchEngine) {
        setCheckedChangedListener { isChecked ->
            if (isChecked) {
                uncheckOtherButtons(this)
                settings.searchEngine = searchEngine
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
