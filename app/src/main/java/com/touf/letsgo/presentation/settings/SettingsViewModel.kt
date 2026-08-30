package com.touf.letsgo.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.touf.letsgo.data.local.entity.AppSettingsEntity
import com.touf.letsgo.data.local.repository.SettingsRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private var currentId = 1

    var homeAddress by mutableStateOf("")
    var workAddress by mutableStateOf("")

    // SUPPRIMÉ : gridSize / updateGridSize(). Le nombre de vignettes n'est plus
    // un réglage manuel, il se calcule automatiquement dans HomeScreen.

    init {
        viewModelScope.launch {
            repository.getSettings().firstOrNull()?.let {
                currentId = it.id
                homeAddress = it.homeAddress ?: ""
                workAddress = it.workAddress ?: ""
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val settings = AppSettingsEntity(
                id = currentId,
                homeAddress = homeAddress,
                workAddress = workAddress
            )
            repository.saveSettings(settings)
            onSaved()
        }
    }
}

class SettingsViewModelFactory(
    private val repository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}