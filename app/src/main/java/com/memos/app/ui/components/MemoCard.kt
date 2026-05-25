package com.memos.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memos.app.data.api.models.Memo
import com.memos.app.utils.DateUtils

@Composable
fun MemoCard(
    memo: Memo,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOpen by remember { mutableStateOf(false) }

    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (memo.pinned)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp),
        border    = if (memo.pinned)
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        else null
    ) {
        Column(Modifier.padding(16.dp)) {

            // ── Header ─────────────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (memo.pinned) {
                        Icon(
                            Icons.Filled.PushPin, null,
                            modifier = Modifier.size(13.dp),
                            tint     = MaterialTheme.colorScheme.primary
                        )
                    }
                    VisibilityLabel(memo.visibility)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text  = DateUtils.formatRelativeTime(memo.createTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box {
                        IconButton(
                            onClick  = { menuOpen = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert, null,
                                modifier = Modifier.size(18.dp),
                                tint     = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded          = menuOpen,
                            onDismissRequest  = { menuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text          = { Text("Edit") },
                                leadingIcon   = { Icon(Icons.Outlined.Edit, null) },
                                onClick       = { menuOpen = false; onEdit() }
                            )
                            DropdownMenuItem(
                                text          = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                leadingIcon   = {
                                    Icon(Icons.Outlined.Delete, null,
                                        tint = MaterialTheme.colorScheme.error)
                                },
                                onClick       = { menuOpen = false; onDelete() }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Content ────────────────────────────────────────────────────────
            Text(
                text      = memo.content,
                style     = MaterialTheme.typography.bodyMedium,
                maxLines  = 8,
                overflow  = TextOverflow.Ellipsis,
                lineHeight = 21.sp
            )

            // ── Tags ───────────────────────────────────────────────────────────
            if (memo.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    memo.tags.take(5).forEach { TagChip(it) }
                }
            }

            // ── Attachments indicator ──────────────────────────────────────────
            if (!memo.resources.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.AttachFile, null,
                        modifier = Modifier.size(13.dp),
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${memo.resources!!.size} file(s)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun VisibilityLabel(visibility: String) {
    val (icon, color) = when (visibility) {
        "PUBLIC"    -> Icons.Outlined.Public to MaterialTheme.colorScheme.tertiary
        "PROTECTED" -> Icons.Outlined.Group  to MaterialTheme.colorScheme.secondary
        else        -> Icons.Outlined.Lock   to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, null, Modifier.size(12.dp), tint = color)
        Text(
            visibility.lowercase().replaceFirstChar { it.uppercase() },
            style      = MaterialTheme.typography.labelSmall,
            color      = color,
            fontWeight = FontWeight.Medium
        )
    }
}
