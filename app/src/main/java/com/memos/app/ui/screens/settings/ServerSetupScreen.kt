package com.memos.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ServerSetupScreen(
    onSetupComplete: () -> Unit,
    vm: ServerSetupViewModel = hiltViewModel()
) {
    LaunchedEffect(vm.setupComplete) {
        if (vm.setupComplete) onSetupComplete()
    }

    Column(
        modifier              = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Cloud, null,
            modifier = Modifier.size(72.dp),
            tint     = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))

        Text(
            "Connect to Memos",
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Enter your Memos server URL",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value          = vm.serverUrl,
            onValueChange  = { vm.serverUrl = it; vm.urlError = null },
            label          = { Text("Server URL") },
            placeholder    = { Text("https://demo.usememos.com") },
            leadingIcon    = { Icon(Icons.Outlined.Link, null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { vm.save() }),
            modifier        = Modifier.fillMaxWidth(),
            shape           = MaterialTheme.shapes.medium,
            singleLine      = true,
            isError         = vm.urlError != null,
            supportingText  = {
                vm.urlError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        )

        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { vm.serverUrl = "https://demo.usememos.com" }) {
            Text("Use demo server")
        }
        Spacer(Modifier.height(20.dp))

        Button(
            onClick  = { vm.save() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = MaterialTheme.shapes.medium
        ) {
            Text("Connect", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
