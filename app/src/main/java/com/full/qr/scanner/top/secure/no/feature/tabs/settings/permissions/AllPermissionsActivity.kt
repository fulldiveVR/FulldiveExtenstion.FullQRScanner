package com.full.qr.scanner.top.secure.no.feature.tabs.settings.permissions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.full.qr.scanner.top.secure.no.R
import com.full.qr.scanner.top.secure.no.extension.applySystemWindowInsets
import com.full.qr.scanner.top.secure.no.feature.BaseActivity

class AllPermissionsActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AllPermissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_permissions)
        findViewById<CoordinatorLayout>(R.id.root_view).applySystemWindowInsets(
            applyTop = true,
            applyBottom = true
        )
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
    }
}