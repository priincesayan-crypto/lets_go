package com.touf.letsgo.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.touf.letsgo.LetsGoApplication

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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.homeAddress,
                onValueChange = { viewModel.homeAddress = it },
                label = { Text("Adresse 'Go Home'") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.workAddress,
                onValueChange = { viewModel.workAddress = it },
                label = { Text("Adresse 'Go Work'") },
                modifier = Modifier.fillMaxWidth()
            )

            // SUPPRIMÉ : le champ "Nombre de vignettes sur l'accueil".
            // La grille se dimensionne maintenant toute seule (6 minimum,
            // toujours une case vide en plus du dernier contact rempli).

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
    }
}