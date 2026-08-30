package com.touf.letsgo.presentation.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.touf.letsgo.data.local.repository.PersonRepository
import com.touf.letsgo.domain.model.Address
import com.touf.letsgo.domain.model.Person
import com.touf.letsgo.domain.model.PhoneNumber
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PersonEditViewModel(
    private val repository: PersonRepository,
    private val personId: Long
) : ViewModel() {

    var name by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")

    private var loadedPerson: Person? = null

    init {
        viewModelScope.launch {
            // Récupère la personne par son ID lors de l'ouverture de l'écran
            loadedPerson = repository.getPersonFlow(personId).firstOrNull()
            loadedPerson?.let {
                name = it.name
                // On récupère le premier numéro et la première adresse de la liste
                phone = it.phoneNumbers.firstOrNull()?.rawNumber ?: ""
                address = it.addresses.firstOrNull()?.rawAddress ?: ""
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        viewModelScope.launch {
            val currentPerson = loadedPerson ?: return@launch

            // Mise à jour du nom
            val updatedPerson = currentPerson.copy(
                name = name,
                updatedAt = System.currentTimeMillis()
            )

            // Mise à jour du téléphone
            val updatedPhones = listOf(
                PhoneNumber(
                    id = currentPerson.phoneNumbers.firstOrNull()?.id ?: 0,
                    rawNumber = phone,
                    label = "Mobile",
                    isPrimary = true
                )
            )

            // Mise à jour de l'adresse
            val updatedAddresses = listOf(
                Address(
                    id = currentPerson.addresses.firstOrNull()?.id ?: 0,
                    rawAddress = address,
                    label = "Domicile",
                    isPrimary = true
                )
            )

            // Sauvegarde dans la base de données via le Repository
            repository.upsertPerson(updatedPerson, updatedPhones, updatedAddresses)

            // Indique à l'interface que c'est fini pour revenir à l'accueil
            onSaved()
        }
    }
}

class PersonEditViewModelFactory(
    private val repository: PersonRepository,
    private val personId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonEditViewModel(repository, personId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}