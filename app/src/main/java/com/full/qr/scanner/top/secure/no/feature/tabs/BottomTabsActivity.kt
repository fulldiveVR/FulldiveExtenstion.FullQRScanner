package com.full.qr.scanner.top.secure.no.feature.tabs

import android.os.Bundle
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.BuildConfig
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.feature.BaseActivity
import com.full.qr.scanner.top.secure.no.feature.tabs.create.CreateBarcodeFragment
import com.full.qr.scanner.top.secure.no.feature.tabs.history.BarcodeHistoryFragment
import com.full.qr.scanner.top.secure.no.feature.tabs.scan.ScanBarcodeFromCameraFragment
import com.full.qr.scanner.top.secure.no.feature.tabs.settings.SettingsFragment
import com.fulldive.startapppopups.PopupManager
import com.fulldive.startapppopups.donation.DonationManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomTabsActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val ACTION_CREATE_BARCODE = "${BuildConfig.APPLICATION_ID}.CREATE_BARCODE"
        private const val ACTION_HISTORY = "${BuildConfig.APPLICATION_ID}.HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tabs)

        supportEdgeToEdge()
        initBottomNavigationView()

        PopupManager().onAppStarted(this, BuildConfig.APPLICATION_ID)


        if (savedInstanceState == null) {
            showInitialFragment()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == findViewById<BottomNavigationView>(R.id.bottom_navigation_view).selectedItemId) {
            return false
        }
        showFragment(item.itemId)
        return true
    }

    override fun onBackPressed() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.let { bottom_navigation_view ->
            if (bottom_navigation_view.selectedItemId == R.id.item_scan) {
                super.onBackPressed()
            } else {
                bottom_navigation_view.selectedItemId = R.id.item_scan
            }
        }
    }

    private fun supportEdgeToEdge() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view).applySystemWindowInsets(applyBottom = true)
    }

    private fun initBottomNavigationView() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation_view).apply {
            setOnNavigationItemSelectedListener(this@BottomTabsActivity)
        }
    }

    private fun showInitialFragment() {
        when (intent?.action) {
            ACTION_CREATE_BARCODE -> findViewById<BottomNavigationView>(R.id.bottom_navigation_view).selectedItemId = R.id.item_create
            ACTION_HISTORY -> findViewById<BottomNavigationView>(R.id.bottom_navigation_view).selectedItemId = R.id.item_history
            else -> showFragment(R.id.item_scan)
        }
    }

    private fun showFragment(bottomItemId: Int) {
        val fragment = when (bottomItemId) {
            R.id.item_scan -> {
                ScanBarcodeFromCameraFragment()

            }

            R.id.item_create -> CreateBarcodeFragment()
            R.id.item_history -> BarcodeHistoryFragment()
            R.id.item_settings -> {
                DonationManager.purchaseFromSettings(
                        this,
                        onPurchased = {
                            PopupManager().showDonationSuccess(this)
                        }
                )
                SettingsFragment()
            }
            else -> null
        }
        fragment?.apply(::replaceFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        DonationManager.destroy()
    }
}