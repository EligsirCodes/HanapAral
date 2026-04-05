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
                        if (canCreate) "Create your Study Group" else "Creation has been disabled.",
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

        if (showDialog) {
            CreateGroupDialog(
                primaryColor = primaryGreen,
                onDismiss = { showDialog = false },
                onConfirm = { name, sub, desc ->
                    viewModel.createGroup(name, sub, desc, currentUserId)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
    primaryColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                "Create Study Group",
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject / Course") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, subject, desc) },
                enabled = name.isNotBlank() && subject.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Composable
fun GroupCard(
    groupName: String,
    subject: String,
    description: String,
    memberCount: Int,
    maxMembers: Int,
    isCreator: Boolean,
    isMember: Boolean,
    primaryColor: Color,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    val isFull = memberCount >= maxMembers

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = primaryColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subject.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = primaryColor,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                if (isCreator) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFC62828))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isFull) Icons.Default.Lock else Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isFull) Color(0xFFC62828) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$memberCount / $maxMembers",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isFull) Color(0xFFC62828) else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isMember) {
                    if (isCreator) {
                        Surface(
                            color = primaryColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Stars, null, Modifier.size(14.dp), tint = primaryColor)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "LEADER",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                        }
                    } else {
                        TextButton(onClick = onLeaveClick) {
                            Text("Leave", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onJoinClick,
                        enabled = !isFull,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = Color(0xFFEEEEEE)
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        Text(if (isFull) "Full" else "Join", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}