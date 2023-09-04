package com.full.qr.scanner.top.secure.no.feature.tabs.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.full.qr.scanner.top.secure.no.BuildConfig
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.di.barcodeDatabase
import com.full.qr.scanner.top.secure.no.di.settings
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.extension.packageManager
import com.full.qr.scanner.top.secure.no.extension.showError
import com.full.qr.scanner.top.secure.no.feature.common.dialog.DeleteConfirmationDialogFragment
import com.full.qr.scanner.top.secure.no.feature.common.view.SettingsButton
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.camera.ChooseCameraActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.formats.SupportedFormatsActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.permissions.AllPermissionsActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.search.ChooseSearchEngineActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.theme.ChooseThemeActivity
import com.fulldive.startapppopups.PopupManager
import com.fulldive.startapppopups.donation.DonationManager
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class SettingsFragment : Fragment(), DeleteConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        supportEdgeToEdge()
    }

    override fun onResume() {
        super.onResume()
        handleButtonCheckedChanged()
        handleButtonClicks()
        showSettings()
        showAppVersion()
    }

    override fun onDeleteConfirmed() {
        clearHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    fun supportEdgeToEdge() {
        fragmentView.findViewById<AppBarLayout>(R.id.app_bar_layout)
            .applySystemWindowInsets(applyTop = true)
    }

    private fun handleButtonCheckedChanged() {
        with(fragmentView) {
            findViewById<SettingsButton>(R.id.button_inverse_barcode_colors_in_dark_theme).setCheckedChangedListener {
                settings.areBarcodeColorsInversed = it
            }
            findViewById<SettingsButton>(R.id.button_open_links_automatically).setCheckedChangedListener {
                settings.openLinksAutomatically = it
            }
            findViewById<SettingsButton>(R.id.button_copy_to_clipboard).setCheckedChangedListener {
                settings.copyToClipboard = it
            }
            findViewById<SettingsButton>(R.id.button_simple_auto_focus).setCheckedChangedListener {
                settings.simpleAutoFocus = it
            }
            findViewById<SettingsButton>(R.id.button_flashlight).setCheckedChangedListener {
                settings.flash = it
            }
            findViewById<SettingsButton>(R.id.button_vibrate).setCheckedChangedListener {
                settings.vibrate = it
            }
            findViewById<SettingsButton>(R.id.button_continuous_scanning).setCheckedChangedListener {
                settings.continuousScanning = it
            }
            findViewById<SettingsButton>(R.id.button_confirm_scans_manually).setCheckedChangedListener {
                settings.confirmScansManually = it
            }
            findViewById<SettingsButton>(R.id.button_save_scanned_barcodes).setCheckedChangedListener {
                settings.saveScannedBarcodesToHistory = it
            }
            findViewById<SettingsButton>(R.id.button_save_created_barcodes).setCheckedChangedListener {
                settings.saveCreatedBarcodesToHistory = it
            }
            findViewById<SettingsButton>(R.id.button_do_not_save_duplicates).setCheckedChangedListener {
                settings.doNotSaveDuplicates = it
            }
            findViewById<SettingsButton>(R.id.button_enable_error_reports).setCheckedChangedListener {
                settings.areErrorReportsEnabled = it
            }
        }
    }

    private fun handleButtonClicks() {
        with(fragmentView) {
            findViewById<SettingsButton>(R.id.button_choose_theme).setOnClickListener {
                ChooseThemeActivity.start(
                    requireActivity()
                )
            }
            findViewById<SettingsButton>(R.id.button_choose_camera).setOnClickListener {
                ChooseCameraActivity.start(
                    requireActivity()
                )
            }
            findViewById<SettingsButton>(R.id.button_select_supported_formats).setOnClickListener {
                SupportedFormatsActivity.start(
                    requireActivity()
                )
            }
            findViewById<SettingsButton>(R.id.button_clear_history).setOnClickListener { showDeleteHistoryConfirmationDialog() }
            findViewById<SettingsButton>(R.id.button_choose_search_engine).setOnClickListener {
                ChooseSearchEngineActivity.start(
                    requireContext()
                )
            }
            findViewById<SettingsButton>(R.id.button_donate).setOnClickListener {

//            DonationManager.purchaseFromSettings(
//                    Activity,
//                    onPurchased = {
//                        PopupManager().showDonationSuccess(context)
//                    }
//            )
//            activity?.let { it1 -> PopupManager().onAppStarted(it1, BuildConfig.APPLICATION_ID) }

                DonationManager.purchaseFromSettings(
                    requireActivity(),
                    onPurchased = {
                        PopupManager().showDonationSuccess(requireContext())
                    }
                )


//            Toast.makeText(context, "Click", Toast.LENGTH_LONG).show()

            }
            findViewById<SettingsButton>(R.id.button_permissions).setOnClickListener {
                AllPermissionsActivity.start(
                    requireActivity()
                )
            }
            findViewById<SettingsButton>(R.id.button_check_updates).setOnClickListener { showAppInMarket() }
            findViewById<SettingsButton>(R.id.button_source_code).setOnClickListener { showSourceCode() }
        }
    }

    private fun clearHistory() {
        with(fragmentView) {
            findViewById<SettingsButton>(R.id.button_clear_history).isEnabled = false

            barcodeDatabase.deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        findViewById<SettingsButton>(R.id.button_clear_history).isEnabled = true
                    },
                    { error ->
                        findViewById<SettingsButton>(R.id.button_clear_history).isEnabled = true
                        showError(error)
                    }
                )
                .addTo(disposable)
        }
    }

    private fun showSettings() {
        settings.apply {
            with(fragmentView) {
                findViewById<SettingsButton>(R.id.button_inverse_barcode_colors_in_dark_theme).isChecked =
                    areBarcodeColorsInversed
                findViewById<SettingsButton>(R.id.button_open_links_automatically).isChecked =
                    openLinksAutomatically
                findViewById<SettingsButton>(R.id.button_copy_to_clipboard).isChecked =
                    copyToClipboard
                findViewById<SettingsButton>(R.id.button_simple_auto_focus).isChecked =
                    simpleAutoFocus
                findViewById<SettingsButton>(R.id.button_flashlight).isChecked = flash
                findViewById<SettingsButton>(R.id.button_vibrate).isChecked = vibrate
                findViewById<SettingsButton>(R.id.button_continuous_scanning).isChecked =
                    continuousScanning
                findViewById<SettingsButton>(R.id.button_confirm_scans_manually).isChecked =
                    confirmScansManually
                findViewById<SettingsButton>(R.id.button_save_scanned_barcodes).isChecked =
                    saveScannedBarcodesToHistory
                findViewById<SettingsButton>(R.id.button_save_created_barcodes).isChecked =
                    saveCreatedBarcodesToHistory
                findViewById<SettingsButton>(R.id.button_do_not_save_duplicates).isChecked =
                    doNotSaveDuplicates
                findViewById<SettingsButton>(R.id.button_enable_error_reports).isChecked =
                    areErrorReportsEnabled
            }
        }
    }

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog =
            DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_clear_history_message)
        dialog.show(childFragmentManager, "")
    }

    private fun showAppInMarket() {
        val uri = Uri.parse("market://details?id=" + requireContext().packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags =
                Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showSourceCode() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/wewewe718/QrAndBarcodeScanner")
        )
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showAppVersion() {
        fragmentView.findViewById<SettingsButton>(R.id.button_app_version).hint =
            BuildConfig.VERSION_NAME
    }
}