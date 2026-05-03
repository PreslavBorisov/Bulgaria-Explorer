package com.bulgariaexplorer.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.bulgariaexplorer.app.utils.PrefKeys

class MainActivity : AppCompatActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not, we proceed normally */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermission()

        val rootView = findViewById<View>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            windowInsets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController

        navController?.let { controller ->
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            if (bottomNav != null) {
                bottomNav.setupWithNavController(controller)

                controller.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.loginFragment, R.id.registerFragment, R.id.poiDetailsFragment, R.id.missionsFragment, R.id.photoViewerFragment, R.id.myVisitsFragment, R.id.favoritesFragment,
                        R.id.adminPanelFragment, R.id.adminUsersFragment, R.id.adminPoisFragment, R.id.adminMissionsFragment, R.id.adminAchievementsFragment, R.id.missionFormFragment, R.id.achievementFormFragment, R.id.poiFormFragment, R.id.notificationsFragment -> {
                            bottomNav.visibility = View.GONE
                        }
                        else -> {
                            bottomNav.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun applyTheme() {
        val prefs = getSharedPreferences(PrefKeys.PREFS_NAME, MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(PrefKeys.DARK_MODE, false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}