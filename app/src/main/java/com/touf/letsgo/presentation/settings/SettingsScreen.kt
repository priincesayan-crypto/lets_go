package com.touf.letsgo.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.touf.letsgo.LetsGoApplication
import com.touf.letsgo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            repository = (LocalContext.current.applicationContext as LetsGoApplication).settingsRepository
        )
    )
) {
    var showCreditsDialog by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color(0xFFFF007F),
        unfocusedLabelColor = Color.DarkGray,
        focusedBorderColor = Color(0xFFFF007F),
        unfocusedBorderColor = Color.Gray
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher),
                            contentDescription = "Logo Let's Go !",
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Paramètres",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF007F)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.homeAddress,
                onValueChange = { viewModel.homeAddress = it },
                label = { Text("Adresse 'Go Home'") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            OutlinedTextField(
                value = viewModel.workAddress,
                onValueChange = { viewModel.workAddress = it },
                label = { Text("Adresse 'Go Work'") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            TextButton(
                onClick = { showCreditsDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFFF007F)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "À propos & Crédits",
                    color = Color(0xFFFF007F),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Annuler")
                }
                Button(
                    onClick = { viewModel.save(onSaved = onNavigateBack) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enregistrer")
                }
            }
        }

        if (showCreditsDialog) {
            AlertDialog(
                onDismissRequest = { showCreditsDialog = false },
                title = {
                    Text(
                        text = "Let's Go ! — v1.0.0",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Développement & Contact",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF007F)
                        )
                        Text(
                            text = "Développé par Touf\nSupport : slimani.toufic+letsgo@gmail.com",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Bibliothèques Open Source",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF007F)
                        )
                        Text(
                            text = "• Jetpack Compose & Material 3\n• Coil (Gestion des images)\n• AndroidX Core & Lifecycle",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ressources graphiques",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF007F)
                        )
                        Text(
                            text = "Icônes fournies par Google Material Icons sous licence Apache 2.0.",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCreditsDialog = false }) {
                        Text("Fermer", color = Color(0xFFFF007F))
                    }
                },
                containerColor = Color.White
            )
        }
    }
}