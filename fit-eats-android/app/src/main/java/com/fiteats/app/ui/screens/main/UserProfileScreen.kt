package com.fiteats.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiteats.app.models.UserModel
import com.fiteats.app.ui.custom.OutlinedSpinner
import com.fiteats.app.ui.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(onLogout: () -> Unit) {
    val mainViewModel: MainViewModel = viewModel()
    val userState by mainViewModel.user.observeAsState()

    LaunchedEffect(Unit) { mainViewModel.getProfile() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(Icons.AutoMirrored.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                windowInsets = WindowInsets(0)
            )
        }
    ) { padding ->
        userState?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileHeader(user)
                Spacer(modifier = Modifier.height(16.dp))
                ProfileDetails(user)
                Spacer(modifier = Modifier.height(16.dp))
                EditProfileButton(mainViewModel, user)
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSection()
            }
        }
    }

}

@Composable
fun ProfileHeader(user: UserModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.first().uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileDetails(user: UserModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileDetailItem("Age", user.age ?: "N/A")
            ProfileDetailItem("Sex", user.sex ?: "N/A")
            ProfileDetailItem("Height", (user.heightInCm ?: "N/A").toString())
            ProfileDetailItem("Country", user.country ?: "N/A")
            ProfileDetailItem("Meal Preference", user.dietPreference ?: "N/A")
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EditProfileButton(mainViewModel: MainViewModel, user: UserModel) {
    var showDialog by remember { mutableStateOf(false) }
    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(5.dp)
    ) {
        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Edit Profile", color = Color.White)
    }

    if (showDialog) {
        EditUserProfileDialog(
            user = user,
            onDismiss = { showDialog = false },
            onSave = {
                mainViewModel.update(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun SettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            SettingsItem("Notifications", Icons.Default.Notifications)
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsItem("Privacy", Icons.Default.Lock)
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsItem("Help & Support", Icons.AutoMirrored.Default.Help)
        }
    }
}

@Composable
fun SettingsItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle setting click */ },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Add this line
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp)) // Add some space between icon and text
            Text(
                title,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        Icon(
            Icons.AutoMirrored.Default.ArrowForwardIos,
            contentDescription = "Go to $title",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
fun EditUserProfileDialog(user: UserModel, onDismiss: () -> Unit, onSave: (UserModel) -> Unit) {
    var name by remember { mutableStateOf(user.name.toString()) }
    var age by remember { mutableStateOf(user.age?.toString() ?: "") }
    var sex by remember { mutableStateOf(user.sex ?: "") }
    var height by remember {
        mutableStateOf(user.heightInCm?.let { user.heightInCm.toString() } ?: "")
    }
    var country by remember { mutableStateOf(user.country ?: "") }
    val selectedDietPreference: MutableState<String?> =
        remember { mutableStateOf(user.dietPreference) }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(5.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sex,
                    onValueChange = { sex = it },
                    label = { Text("Sex") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
                val dietPreferences =
                    listOf("Vegetarian", "Vegan", "Pescatarian", "Flexitarian", "Omnivore")

                OutlinedSpinner(
                    label = "Diet Preference",
                    items = dietPreferences,
                    modifier = Modifier.fillMaxWidth(),
                    selectedItem = selectedDietPreference,
                    itemToString = { it -> it },
                    onItemSelected = { item -> selectedDietPreference.value = item }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    UserModel(
                        id = user.id,
                        email = user.email,
                        name = name,
                        age = age,
                        sex = sex,
                        heightInCm = height.toDoubleOrNull(),
                        country = country,
                        dietPreference = selectedDietPreference.value
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}