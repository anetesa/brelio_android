package com.brelio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brelio.core.navigation.BrelioNavHost
import com.brelio.core.theme.BrelioTheme
import com.brelio.data.local.PreferencesManager
import com.brelio.domain.model.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode = preferencesManager.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.Female)
            BrelioTheme(themeMode = themeMode.value) {
                BrelioNavHost()
            }
        }
    }
}
