package com.touf.letsgo.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import com.touf.letsgo.data.local.repository.PersonRepository
import com.touf.letsgo.data.local.repository.SettingsRepository
import com.touf.letsgo.domain.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PersonRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _quickAccessPersons = MutableStateFlow<List<Person?>>(emptyList())
    val quickAccessPersons: StateFlow<List<Person?>> = _quickAccessPersons.asStateFlow()

    private val _settings = MutableStateFlow<AppSettingsEntity?>(null)
    val settings: StateFlow<AppSettingsEntity?> = _settings.asStateFlow()

    init {
        loadQuickAccess()
        loadSettings()
    }

    private fun loadQuickAccess() {
        viewModelScope.launch {
            repository.getQuickAccessPersons().collect { persons ->
                val list = mutableListOf<Person?>()
                for (i in 0..5) {
                    val person = persons.find { it.quickAccessPosition == i }
                    list.add(person)
                }
                _quickAccessPersons.value = list
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { currentSettings ->
                _settings.value = currentSettings
            }
        }
    }

    fun onGoClicked(context: Context, person: Person) {
        val address = person.primaryAddress?.rawAddress
        if (address.isNullOrEmpty()) {
            Toast.makeText(context, "Aucune adresse configurée pour ${person.name}", Toast.LENGTH_SHORT).show()
            return
        }
        launchNavigation(context, address)
    }

    fun onHomeClicked(context: Context) {
        val address = _settings.value?.homeAddress
        if (address.isNullOrEmpty()) {
            Toast.makeText(context, "Adresse Home non configurée", Toast.LENGTH_SHORT).show()
        } else {
            launchNavigation(context, address)
        }
    }

    fun onWorkClicked(context: Context) {
        val address = _settings.value?.workAddress
        if (address.isNullOrEmpty()) {
            Toast.makeText(context, "Adresse Work non configurée", Toast.LENGTH_SHORT).show()
        } else {
            launchNavigation(context, address)
        }
    }

    private fun launchNavigation(context: Context, address: String) {
        // Tentative avec un lien web (Google Maps)
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(address)}")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        if (webIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(webIntent)
            return
        }

        // Fallback sur geo:
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)
        if (geoIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(geoIntent)
            return
        }

        // Dernier recours
        Toast.makeText(context, "Aucune application de navigation trouvée.\nAdresse : $address", Toast.LENGTH_LONG).show()
    }
}