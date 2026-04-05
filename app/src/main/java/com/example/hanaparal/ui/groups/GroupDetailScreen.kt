package com.example.hanaparal.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupDetailViewModel,
    onNavigateToAnnouncement: (String) -> Unit
) {
    val group by viewModel.currentGroup.collectAsState()
    val members by viewModel.members.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid }

    val primaryGreen = Color(0xFF2E7D32)
    val pureWhite = Color(0xFFFFFFFF)

    LaunchedEffect(groupId) {
        viewModel.loadGroupAndMembers(groupId)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        group?.name ?: "Group Details",
                        fontWeight = FontWeight.ExtraBold,
                        color = pureWhite,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryGreen
                )
            )
        },
        floatingActionButton = {
            if (group?.createdBy == currentUserId) {
                ExtendedFloatingActionButton(
                    onClick = { onNavigateToAnnouncement(groupId) },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null) },
                    text = { Text("Announcement", fontWeight = FontWeight.Bold) },
                    containerColor = primaryGreen,
                    contentColor = pureWhite,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && group == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryGreen
                )
            } else if (group == null) {
                Text(
                    text = "Group not found.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            } else {
                val currentGroup = group!!

                Column(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = pureWhite,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = primaryGreen
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentGroup.subject.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = primaryGreen,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentGroup.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.DarkGray,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Study Buddies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                        Surface(
                            color = primaryGreen.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${members.size} joined",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = primaryGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isLoading && members.isEmpty()) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            color = primaryGreen,
                            trackColor = primaryGreen.copy(alpha = 0.1f)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(members) { member ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = pureWhite),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                                ) {
                                    ListItem(
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                        headlineContent = {
                                            Text(
                                                member.name,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                member.course,
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        },
                                        leadingContent = {
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = primaryGreen.copy(alpha = 0.1f),
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = member.name.take(1).uppercase(),
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Black,
                                                        color = primaryGreen
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            item { Spacer(modifier = Modifier.height(100.dp)) }
                        }
                    }
                }
            }
        }
    }
}

