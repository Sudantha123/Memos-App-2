package com.memos.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.memos.app.data.api.models.Memo
import com.memos.app.ui.components.MemoCard
import com.memos.app.ui.components.ShimmerList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateMemo : () -> Unit,
    onMemoClick  : (String) -> Unit,
    onEditMemo   : (String) -> Unit,
    onLogout     : () -> Unit,
    vm           : HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbar       = remember { SnackbarHostState() }

    var showSearch      by remember { mutableStateOf(false) }
    var showProfileMenu by remember { mutableStateOf(false) }
    var deleteTarget    by remember { mutableStateOf<Memo?>(null) }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it) }
    }

    // Delete dialog
    deleteTarget?.let { memo ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title   = { Text("Delete Memo") },
            text    = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteMemo(memo); deleteTarget = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        modifier     = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Column {
                TopAppBar(
                    title  = { if (!showSearch) Text("Memos", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(
                                if (showSearch) Icons.Default.Close else Icons.Outlined.Search,
                                contentDescription = null
                            )
                        }
                        Box {
                            IconButton(onClick = { showProfileMenu = true }) {
                                Icon(Icons.Outlined.AccountCircle, null)
                            }
                            DropdownMenu(
                                expanded         = showProfileMenu,
                                onDismissRequest = { showProfileMenu = false }
                            ) {
                                vm.currentUser?.let { u ->
                                    DropdownMenuItem(
                                        text    = {
                                            Column {
                                                Text(
                                                    u.displayName.ifEmpty { u.username },
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    "@${u.username}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {},
                                        enabled = false
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text        = { Text("Logout") },
                                    leadingIcon = { Icon(Icons.Outlined.Logout, null) },
                                    onClick     = {
                                        showProfileMenu = false
                                        vm.logout()
                                        onLogout()
                                    }
                                )
                            }
                        }
                    }
                )

                // Search bar
                AnimatedVisibility(
                    visible = showSearch,
                    enter   = expandVertically() + fadeIn(),
                    exit    = shrinkVertically() + fadeOut()
                ) {
                    OutlinedTextField(
                        value         = state.searchQuery,
                        onValueChange = { vm.search(it) },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder   = { Text("Search…") },
                        leadingIcon   = { Icon(Icons.Outlined.Search, null) },
                        trailingIcon  = {
                            if (state.searchQuery.isNotEmpty())
                                IconButton(onClick = { vm.search("") }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                        },
                        shape      = MaterialTheme.shapes.extraLarge,
                        singleLine = true
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick          = onCreateMemo,
                icon             = { Icon(Icons.Filled.Add, null) },
                text             = { Text("New Memo") },
                containerColor   = MaterialTheme.colorScheme.primary,
                contentColor     = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { pad ->

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh    = { vm.refresh() },
            modifier     = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            when {
                // Skeleton on very first load
                state.isFirstLoad -> ShimmerList()

                state.memos.isEmpty() -> EmptyState(onCreateMemo)

                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp)
                ) {
                    items(state.memos, key = { it.id }) { memo ->
                        MemoCard(
                            memo     = memo,
                            onClick  = { onMemoClick(memo.name) },
                            onEdit   = { onEditMemo(memo.name) },
                            onDelete = { deleteTarget = memo },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onCreateMemo: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.NoteAdd, null,
            modifier = Modifier.size(80.dp),
            tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text("No memos yet", style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Start capturing your thoughts!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onCreateMemo) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Create first memo")
        }
    }
}
