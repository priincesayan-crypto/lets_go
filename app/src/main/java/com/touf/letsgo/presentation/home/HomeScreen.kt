package com.touf.letsgo.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.touf.letsgo.LetsGoApplication
import com.touf.letsgo.domain.model.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // CORRIGÉ : on passe désormais une factory qui fournit le repository de l'Application,
    // sinon HomeViewModel (qui a un constructeur avec paramètre) ne peut pas être instancié
    // et l'app plante au lancement.
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            (LocalContext.current.applicationContext as LetsGoApplication).repository
        )
    )
) {
    val quickAccess by viewModel.quickAccessPersons.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Grille 2×3
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(quickAccess) { person ->
                if (person != null) {
                    QuickAccessCard(person) {
                        viewModel.onGoClicked(person)
                    }
                } else {
                    EmptyCard()
                }
            }
        }

        // Boutons Home / Work
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CORRIGÉ : le modifier(weight) est maintenant passé par l'appelant (ici, dans le Row),
            // car weight() n'existe que pour un Modifier reçu dans un RowScope/ColumnScope.
            HomeWorkButton(
                label = "Home",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO */ }
            )
            HomeWorkButton(
                label = "Work",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun QuickAccessCard(person: Person, onGo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo
            // TODO: afficher la photo
            Text(text = person.name)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onGo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GO !")
            }
        }
    }
}

@Composable
fun EmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", style = MaterialTheme.typography.displayLarge)
        }
    }
}

@Composable
fun HomeWorkButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(text = label)
    }
}