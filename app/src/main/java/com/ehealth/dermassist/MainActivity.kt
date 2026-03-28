package com.ehealth.dermassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ehealth.dermassist.ui.navigation.AppNavGraph
import com.ehealth.dermassist.ui.theme.DermAssistTheme

class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { _root_ide_package_.com.ehealth.dermassist.ui.theme.DermAssistTheme { _root_ide_package_.com.ehealth.dermassist.ui.navigation.AppNavGraph() } }
    }
}
