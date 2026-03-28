package com.ehealth.dermassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ehealth.dermassist.ui.navigation.AppNavGraph
import com.ehealth.dermassist.ui.theme.DermAssistTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DermAssistTheme { AppNavGraph() } }
    }
}
