package com.memos.app.ui.screens.memo

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemoScreen(
    memoName : String? = null,
    onBack   : () -> Unit,
    onSaved  : () -> Unit,
    vm       : MemoViewModel = hiltViewModel()
) {
    LaunchedEffect(memoName) { memoName?.let { vm.loadMemo(it) } }
    LaunchedEffect(vm.isSaved) { if (vm.isSaved) onSaved() }

    var menuOpen by remember { mutableStateOf(false) }
    val visibilities = listOf("PRIVATE", "PROTECTED", "PUBLIC")

    Scaffold(
        topBar = {
            TopAppBar(
                title          = {
                    Text(if (memoName != null) "Edit Memo" else "New Memo",
                        fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.Close, null)
                    }
                },
                actions = {
                    // Visibility picker
                    Box {
                        TextButton(onClick = { menuOpen = true }) {
                            Icon(
                                when (vm.visibility) {
                                    "PUBLIC"    -> Icons.Outlined.Public
                                    "PROTECTED" -> Icons.Outlined.Group
                                    else        -> Icons.Outlined.Lock
                                },
                                null, Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(vm.visibility.lowercase()
                                .replaceFirstChar { it.uppercase() })
                        }
                        DropdownMenu(menuOpen, { menuOpen = false }) {
                            visibilities.forEach { v ->
                                DropdownMenuItem(
                                    text        = { Text(v.lowercase().replaceFirstChar { it.uppercase() }) },
                                    leadingIcon = {
                                        Icon(when (v) {
                                            "PUBLIC"    -> Icons.Outlined.Public
                                            "PROTECTED" -> Icons.Outlined.Group
                                            else        -> Icons.Outlined.Lock
                                        }, null)
                                    },
                                    onClick = { vm.visibility = v; menuOpen = false }
                                )
                            }
                        }
                    }
                    Button(
                        onClick  = { vm.save(memoName) },
                        enabled  = !vm.isLoading && vm.content.isNotBlank(),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (vm.isLoading)
                            CircularProgressIndicator(
                                Modifier.size(16.dp), strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        else
                            Text(if (memoName != null) "Update" else "Save")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            AnimatedVisibility(vm.error != null) {
                vm.error?.let {
                    Card(
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) { Text(it, Modifier.padding(12.dp), color = MaterialTheme.colorScheme.error) }
                }
            }

            TextField(
                value         = vm.content,
                onValueChange = { vm.content = it; vm.error = null },
                modifier      = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                placeholder   = {
                    Text("What's on your mind?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
            )
        }
    }
}
