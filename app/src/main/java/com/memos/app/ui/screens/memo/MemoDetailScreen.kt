package com.memos.app.ui.screens.memo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.memos.app.ui.components.TagChip
import com.memos.app.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MemoDetailScreen(
    memoName : String,
    onBack   : () -> Unit,
    onEdit   : (String) -> Unit,
    vm       : MemoViewModel = hiltViewModel()
) {
    LaunchedEffect(memoName) { vm.loadMemo(memoName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("Memo", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    vm.currentMemo?.let { m ->
                        IconButton(onClick = { onEdit(m.name) }) {
                            Icon(Icons.Outlined.Edit, null)
                        }
                    }
                }
            )
        }
    ) { pad ->
        val memo = vm.currentMemo
        if (memo == null) {
            Box(Modifier.fillMaxSize().padding(pad), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    DateUtils.formatFullDate(memo.createTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    memo.visibility.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(memo.content, style = MaterialTheme.typography.bodyLarge)

            if (memo.tags.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tags",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement   = Arrangement.spacedBy(6.dp)
                ) {
                    memo.tags.forEach { TagChip(it) }
                }
            }
        }
    }
}

