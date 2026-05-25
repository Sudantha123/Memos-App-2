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
    val focus  = LocalFocusManager.current
    var pwVisible by remember { mutableStateOf(false) }

    LaunchedEffect(vm.loginSuccess) { if (vm.loginSuccess) onLoginSuccess() }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.StickyNote2, null,
            modifier = Modifier.size(72.dp),
            tint     = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
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
        Spacer(Modifier.height(28.dp))

        // Username
        OutlinedTextField(
            value         = vm.username,
            onValueChange = { vm.username = it; vm.errorMessage = null },
            label         = { Text("Username") },
            leadingIcon   = { Icon(Icons.Outlined.Person, null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
            modifier    = Modifier.fillMaxWidth(),
            shape       = MaterialTheme.shapes.medium,
            singleLine  = true
        )
        Spacer(Modifier.height(12.dp))

        // Password
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
                focus.clearFocus(); vm.login()
            }),
            modifier   = Modifier.fillMaxWidth(),
            shape      = MaterialTheme.shapes.medium,
            singleLine = true
        )

        // Error
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
                        Icon(Icons.Outlined.ErrorOutline, null,
                            tint     = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick  = { focus.clearFocus(); vm.login() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = MaterialTheme.shapes.medium,
            enabled  = !vm.isLoading
        ) {
            if (vm.isLoading)
                CircularProgressIndicator(Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            else
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
