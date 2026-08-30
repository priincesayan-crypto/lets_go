package com.touf.letsgo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import com.touf.letsgo.domain.model.Address
import com.touf.letsgo.domain.model.Person
import com.touf.letsgo.domain.model.PhoneNumber
import com.touf.letsgo.presentation.edit.PersonEditScreen
import com.touf.letsgo.presentation.home.HomeScreen
import com.touf.letsgo.ui.theme.LetsGoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as LetsGoApplication
        val repository = app.repository
        val settingsRepository = app.settingsRepository

        lifecycleScope.launch {
            try {
                // Gestion des contacts de test
                val existingPersons = repository.getAllPersons().first()
                if (existingPersons.isEmpty()) {
                    Log.d("MainActivity", "Base vide, création des 6 fiches de test")
                    val fichesFictives = listOf(
                        creerFiche("Jean Test", "jean test", 0, "10 Rue de Paris, 75001 Paris", "0612345678"),
                        creerFiche("Alice Dupont", "alice dupont", 1, "5 Avenue de la République, 75011 Paris", "0611223344"),
                        creerFiche("Marc Leblanc", "marc leblanc", 2, "15 Rue de la Paix, 75002 Paris", "0699887766"),
                        creerFiche("Sophie Martin", "sophie martin", 3, "2 Place de la Bourse, 33000 Bordeaux", "0655443322"),
                        creerFiche("Lucas Petit", "lucas petit", 4, "10 Boulevard Carnot, 06400 Cannes", "0677889900"),
                        creerFiche("Emma Roux", "emma roux", 5, "8 Rue Victor Hugo, 69002 Lyon", "0612349876")
                    )
                    for (fiche in fichesFictives) {
                        repository.upsertPerson(fiche, fiche.phoneNumbers, fiche.addresses)
                    }
                }

                // Gestion des paramètres Home / Work
                val currentSettings = settingsRepository.getSettings().first()
                if (currentSettings == null) {
                    settingsRepository.saveSettings(
                        AppSettingsEntity(
                            id = 0,
                            homeAddress = "Tour Eiffel, Paris",
                            workAddress = "Musée du Louvre, Paris"
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ Erreur lors de l'initialisation", e)
            }
        }

        setContent {
            LetsGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // NavHost gère le passage entre les écrans
                    NavHost(navController = navController, startDestination = "home") {

                        // Écran 1 : Accueil
                        composable("home") {
                            HomeScreen(
                                onNavigateToEdit = { personId ->
                                    navController.navigate("edit/$personId")
                                }
                            )
                        }

                        // Écran 2 : Formulaire de modification
                        composable(
                            route = "edit/{personId}",
                            arguments = listOf(navArgument("personId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
                            PersonEditScreen(
                                personId = personId,
                                onNavigateBack = { navController.popBackStack() } // Retour en arrière après sauvegarde
                            )
                        }
                    }
                }
            }
        }
    }

    private fun creerFiche(nom: String, nomRecherche: String, positionAccueil: Int, adresse: String, telephone: String): Person {
        return Person(
            id = 0,
            name = nom,
            photoUri = null,
            notes = "Contact généré automatiquement",
            isQuickAccess = true,
            quickAccessPosition = positionAccueil,
            searchableName = nomRecherche,
            phoneNumbers = listOf(PhoneNumber(id = 0, rawNumber = telephone, label = "Mobile", isPrimary = true)),
            addresses = listOf(Address(id = 0, rawAddress = adresse, label = "Domicile", isPrimary = true)),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}