package com.example.hanaparal.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    viewModel: GroupViewModel,
    onGroupClick: (String) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val canCreate by viewModel.isGroupCreationEnabled.collectAsState()
    val maxMembers by viewModel.maxMembersPerGroup.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    val primaryGreen = Color(0xFF2E7D32)
    val pureWhite = Color(0xFFFFFFFF)

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Study Groups",
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
            if (canCreate) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = primaryGreen,
                    contentColor = pureWhite,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New Group", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (groups.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = primaryGreen.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = primaryGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "No groups found",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        if (canCreate) "Be the first to create one!" else "Creation is currently limited.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(groups) { group ->
                        val isMember = group.members.contains(currentUserId)
                        val isCreator = group.createdBy == currentUserId

                        GroupCard(
                            groupName = group.name,
                            subject = group.subject,
                            description = group.description,
                            memberCount = group.members.size,
                            maxMembers = maxMembers,
                            isCreator = isCreator,
                            isMember = isMember,
                            primaryColor = primaryGreen,
                            onCardClick = { onGroupClick(group.id) },
                            onDeleteClick = { viewModel.deleteGroup(group.id) },
                            onJoinClick = { viewModel.joinGroup(group, currentUserId) },
                            onLeaveClick = { viewModel.leaveGroup(group.id, currentUserId) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }