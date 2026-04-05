package com.example.hanaparal.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onProfileSaved: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val user = remember { FirebaseAuth.getInstance().currentUser }
    val context = LocalContext.current

    val biometricManager = remember(context) {
        (context as? FragmentActivity)?.let { BiometricPromptManager(it) }
    }

    val yearLevels = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    var yearExpanded by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF2E7D32)
    val pureWhite = Color(0xFFFFFFFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(75.dp),
                tint = pureWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Setup Profile",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = pureWhite
            )

            user?.email?.let { email ->
                Text(
                    text = "Logged in as $email",
                    style = MaterialTheme.typography.bodySmall,
                    color = pureWhite.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = pureWhite,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Student Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. John Doe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGreen,
                            focusedLabelColor = primaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.course,
                        onValueChange = { viewModel.course = it },
                        label = { Text("Course") },
                        placeholder = { Text("e.g. BSIT") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGreen,
                            focusedLabelColor = primaryGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = yearExpanded,
                        onExpandedChange = { if (!isLoading) yearExpanded = !yearExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.year,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Year Level") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryGreen,
                                focusedLabelColor = primaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            yearLevels.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year) },
                                    onClick = {
                                        viewModel.year = year
                                        yearExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = primaryGreen)
                    } else {
                        Button(
                            onClick = {
                                val onSaveAction = {
                                    user?.let {
                                        viewModel.saveProfile(it.uid, it.email ?: "") { success ->
                                            if (success) onProfileSaved()
                                        }
                                    }
                                }

                                if (biometricManager != null && biometricManager.isBiometricAvailable()) {
                                    biometricManager.showBiometricPrompt(
                                        onSuccess = { onSaveAction() },
                                        onError = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                } else {
                                    onSaveAction()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = viewModel.name.isNotBlank() &&
                                    viewModel.course.isNotBlank() &&
                                    viewModel.year.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryGreen,
                                contentColor = pureWhite
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save and Continue", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}