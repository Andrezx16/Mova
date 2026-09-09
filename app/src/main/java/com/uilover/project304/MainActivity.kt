package com.uilover.project304

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.uilover.project304.ui.navigation.AppNavHost
import com.uilover.project304.ui.theme.Project304Theme
import com.uilover.project304.util.LocaleHelper

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Aplica el idioma guardado antes de crear la Activity
        super.attachBaseContext(LocaleHelper.applyLocaleFromPreferences(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project304Theme {
                AppNavHost()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppNavHostPreview() {
    Project304Theme {
        AppNavHost()
    }
}