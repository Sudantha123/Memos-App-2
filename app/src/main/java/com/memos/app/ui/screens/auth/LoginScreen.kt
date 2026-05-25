package com.memos.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onServerChange: () -> Unit,
    vm: LoginViewModel = hiltViewModel()
) {
    val focus = LocalFocusManager.current
    var pwVisible   by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(vm.loginSuccess) { if (vm.loginSuccess) onLoginSuccess() }
    // Reset error on tab switch
    LaunchedEffect(selectedTab) { vm.errorMessage = null }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo + title
        Icon(
            Icons.Outlined.StickyNote2, null,
            modifier = Modifier.size(72.dp),
            tint     = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Welcome Back",
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            vm.serverUrl() ?: "",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onServerChange) {
            Icon(Icons.Outlined.Edit, null, Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Change server")
        }
        Spacer(Modifier.height(24.dp))

        // Tabs
        TabRow(
            selectedTabIndex  = selectedTab,
            modifier          = Modifier.fillMaxWidth(),
            containerColor    = MaterialTheme.colorScheme.surfaceVariant,
            contentColor      = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick  = { selectedTab = 0 },
                text     = { Text("Password") },
                icon     = { Icon(Icons.Outlined.Lock, null, Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick  = { selectedTab = 1 },
                text     = { Text("Access Token") },
                icon     = { Icon(Icons.Outlined.Key, null, Modifier.size(16.dp)) }
            )
        }

        Spacer(Modifier.height(24.dp))

        // Tab content
        when (selectedTab) {

            // ── Password tab ──────────────────────────────────────
            0 -> {
                OutlinedTextField(
                    value         = vm.username,
                    onValueChange = { vm.username = it; vm.errorMessage = null },
                    label         = { Text("Username") },
                    leadingIcon   = { Icon(Icons.Outlined.Person, null) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        focus.moveFocus(FocusDirection.Down)
                    }),
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value         = vm.password,
                    onValueChange = { vm.password = it; vm.errorMessage = null },
                    label         = { Text("Password") },
                    leadingIcon   = { Icon(Icons.Outlined.Lock, null) },
                    trailingIcon  = {
                        IconButton(onClick = { pwVisible = !pwVisible }) {
                            Icon(
                                if (pwVisible) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility, null
                            )
                        }
                    },
                    visualTransformation = if (pwVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focus.clearFocus(); vm.loginWithPassword()
                    }),
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }

            // ── Token tab ─────────────────────────────────────────
            1 -> {
                OutlinedTextField(
                    value         = vm.accessToken,
                    onValueChange = { vm.accessToken = it; vm.errorMessage = null },
                    label         = { Text("Access Token") },
                    leadingIcon   = { Icon(Icons.Outlined.Key, null) },
                    placeholder   = { Text("Paste your Memos access token") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focus.clearFocus(); vm.loginWithToken()
                    }),
                    modifier   = Modifier.fillMaxWidth(),
                    shape      = MaterialTheme.shapes.medium,
                    singleLine = false,
                    minLines   = 2
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Memos → Settings → My Account → Access Tokens",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Error banner
        AnimatedVisibility(vm.errorMessage != null) {
            vm.errorMessage?.let {
                Spacer(Modifier.height(10.dp))
                Card(
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ErrorOutline, null,
                            tint     = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                focus.clearFocus()
                if (selectedTab == 0) vm.loginWithPassword() else vm.loginWithToken()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = MaterialTheme.shapes.medium,
            enabled  = !vm.isLoading
        ) {
            if (vm.isLoading)
                CircularProgressIndicator(
                    Modifier.size(22.dp),
                    color       = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            else
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

