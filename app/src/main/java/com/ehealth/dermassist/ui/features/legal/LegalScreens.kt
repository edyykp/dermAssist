package com.ehealth.dermassist.ui.features.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ehealth.dermassist.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms and Conditions", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.dimens.md)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Welcome to DermAssist",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = MaterialTheme.dimens.sm)
            )
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n" +
                        "Section 1: User Obligations\n" +
                        "Curabitur pretium tincidunt lacus. Nulla gravida orci a odio. Nullam varius, turpis et commodo pharetra, est eros bibendum elit, nec luctus magna felis sollicitudin mauris. Integer in mauris eu nibh euismod gravida. Duis ac tellus et risus vulputate vehicula. Donec lobortis risus a elit. Etiam tempor. Ut ullamcorper, ligula eu tempor congue, eros est euismod turpis, id tincidunt sapien risus a quam. Maecenas fermentum consequat mi. Donec fermentum. Pellentesque malesuada nulla a mi. Duis sapien sem, aliquet nec, commodo eget, consequat quis, neque.\n\n" +
                        "Section 2: Privacy and Data\n" +
                        "Aliquam faucibus, elit iaculis dictum enim, diam mauris egestas quam, at pretium elit ante quis urna. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.\n\n" +
                        "Section 3: Termination\n" +
                        "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.dimens.md)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "DermAssist Privacy Policy",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = MaterialTheme.dimens.sm)
            )
            Text(
                text = "Your privacy is important to us.\n\n" +
                        "1. Data Collection\n" +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus imperdiet, nulla et dictum interdum, nisi lorem egestas odio, vitae scelerisque enim ligula venenatis dolor. Maecenas nisl est, ultrices nec congue eget, auctor vitae massa. Fusce luctus vestibulum augue ut aliquet. Nunc sagittis dictum nisi, sed ullamcorper ipsum dignissim ac.\n\n" +
                        "2. How We Use Your Data\n" +
                        "In hac habitasse platea dictumst. Etiam sit amet lectus quis est congue mollis. Phasellus vitae nisi iaculis, efficitur sem at, interdum nisl. Donec efficitur, sem nec imperdiet rhoncus, nulla elementum ante, ac finibus enim eros nec mi. Curabitur vel neque vel magna pellentesque egestas. Aliquam ac lacus id lacus dictum pellentesque. Sed pretium pretium erat, sed scelerisque enim.\n\n" +
                        "3. Data Sharing\n" +
                        "Vivamus vitae eleifend sem. Maecenas a lectus metus. Vivamus ac diam vulputate, pretium eros at, finibus lectus. Aenean non vestibulum arcu. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec vel metus eu lectus condimentum dictum. Nam at facilisis tellus. Suspendisse potenti.\n\n" +
                        "4. Your Rights\n" +
                        "Nullam id nibh eget metus rhoncus eleifend. Proin vel congue eros. Donec non diam felis. Suspendisse pulvinar auctor nibh, vitae consequat tellus ullamcorper ac. Curabitur vel dui tincidunt, eleifend mi eu, scelerisque ex. Mauris nec eros ultrices, porttitor dolor sit amet, eleifend lorem. Duis sollicitudin magna lectus, nec efficitur sem pharetra non.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}
