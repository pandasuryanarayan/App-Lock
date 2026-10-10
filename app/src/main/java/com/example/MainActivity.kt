package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.example.ui.navigation.AppLockerMainNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppLockerViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: AppLockerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the animated system splash up until persisted settings load,
        // so finished setups land straight on home with no setup flash.
        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isInitializing }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppLockerMainNavigation(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPermissions()
    }
}
