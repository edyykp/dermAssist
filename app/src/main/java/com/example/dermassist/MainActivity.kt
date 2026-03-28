package com.example.dermassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dermassist.ui.navigation.AppNavGraph
import com.example.dermassist.ui.theme.DermAssistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DermAssistTheme {
                AppNavGraph()
            }
        }
    }
}
