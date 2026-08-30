package com.touf.letsgo.presentation.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.touf.letsgo.LetsGoApplication
import com.touf.letsgo.domain.model.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEdit: (Long) -> Unit, // <-- Nouveau paramètre pour la navigation
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            repository = (LocalContext.current.applicationContext as LetsGoApplication).repository,
            settingsRepository = (LocalContext.current.applicationContext as LetsGoApplication).settingsRepository
        )
    )
) {
    val context = LocalContext.current
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
                    QuickAccessCard(
                        person = person,
                        context = context,
                        onGo = { viewModel.onGoClicked(context, person) },
                        onEdit = { onNavigateToEdit(person.id) } // <-- Déclenche le changement d'écran
                    )
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
            HomeWorkButton(
                label = "Home",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.onHomeClicked(context) }
            )
            HomeWorkButton(
                label = "Work",
                modifier = Modifier.weight(1f),
                onClick = { viewModel.onWorkClicked(context) }
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    person: Person,
    context: Context,
    onGo: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier ${person.name}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = person.photoUri,
                    contentDescription = "Photo de ${person.name}",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
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