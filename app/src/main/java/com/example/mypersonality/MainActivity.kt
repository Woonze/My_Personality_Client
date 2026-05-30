package com.example.mypersonality

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.example.mypersonality.core.model.ThemeMode
import com.example.mypersonality.core.navigation.MyPersonalityRoot
import com.example.mypersonality.ui.theme.MyPersonalityTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState by viewModel.themeMode.collectAsState()
            val useDarkTheme = when (themeState) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            MyPersonalityTheme(darkTheme = useDarkTheme) {
                MyPersonalityRoot(viewModel = viewModel)
            }
        }
    }
}
