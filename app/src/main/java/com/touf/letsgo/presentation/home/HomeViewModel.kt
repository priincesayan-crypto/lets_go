package com.touf.letsgo.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import com.touf.letsgo.data.local.repository.PersonRepository
import com.touf.letsgo.data.local.repository.SettingsRepository
import com.touf.letsgo.domain.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PersonRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _quickAccessPersons = MutableStateFlow<List<Person>>(emptyList())
    val quickAccessPersons: StateFlow<List<Person>> = _quickAccessPersons.asStateFlow()

    private val _settings = MutableStateFlow<AppSettingsEntity?>(null)
    val settings: StateFlow<AppSettingsEntity?> = _settings.asStateFlow()

    private var currentToast: Toast? = null

    private fun showUniqueToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, message, duration)
        currentToast?.show()
    }

    init {
        viewModelScope.launch {
            combine(
                repository.getQuickAccessPersons(),
                settingsRepository.getSettings()
            ) { persons, currentSettings ->
                _settings.value = currentSettings
                persons
            }.collect { personsList ->
                _quickAccessPersons.value = personsList
            }
        }
    }

    // SUPPRIMÉ : updateTargetVignetteCount / confirmReduction / dismissDeleteDialog /
    // showDeleteDialog / pendingTargetSize. Ce mécanisme de réglage manuel du nombre
    // de vignettes n'existe plus : la grille se dimensionne automatiquement
    // (cf. HomeScreen.kt), donc plus besoin de demander à l'utilisateur de choisir
    // qui retirer quand il réduit un chiffre dans les paramètres.

    fun onGoClicked(context: Context, person: Person) {
        val address = person.primaryAddress?.rawAddress
        if (address.isNullOrEmpty()) {
            showUniqueToast(context, "Aucune adresse configurée pour ${person.name}")
            return
        }
        launchNavigation(context, address)
    }

    fun onHomeClicked(context: Context) {
        val address = _settings.value?.homeAddress
        if (address.isNullOrEmpty()) {
            showUniqueToast(context, "Adresse Home non configurée")
        } else {
            launchNavigation(context, address)
        }
    }

    fun onWorkClicked(context: Context) {
        val address = _settings.value?.workAddress
        if (address.isNullOrEmpty()) {
            showUniqueToast(context, "Adresse Work non configurée")
        } else {
            launchNavigation(context, address)
        }
    }

    // RE-CORRIGÉ : le lien Google Maps codé en dur (https://www.google.com/maps/...)
    // était revenu dans le code. On revient à l'intent geo: générique, seul moyen
    // de laisser Android proposer le choix entre Maps, Waze, etc. — règle verrouillée
    // depuis la Phase 2 du cahier des charges, à ne plus réintroduire.
    private fun launchNavigation(context: Context, address: String) {
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)

        if (geoIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(geoIntent)
        } else {
            showUniqueToast(context, "Aucune application de navigation trouvée.\nAdresse : $address", Toast.LENGTH_LONG)
        }
    }
}

class HomeViewModelFactory(
    private val repository: PersonRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}