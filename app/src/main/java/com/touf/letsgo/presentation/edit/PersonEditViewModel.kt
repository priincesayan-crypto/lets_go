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
    var photoUri by mutableStateOf<String?>(null)

    private var existingPerson: Person? = null

    init {
        if (personId != -1L) {
            viewModelScope.launch {
                existingPerson = repository.getPersonFlow(personId).firstOrNull()
                existingPerson?.let {
                    name = it.name
                    phone = it.primaryPhoneNumber?.rawNumber ?: ""
                    address = it.primaryAddress?.rawAddress ?: ""
                    photoUri = it.photoUri
                }
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        if (name.isBlank()) return

        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()

            val phoneNumbersList = if (phone.isNotBlank()) {
                val phoneId = existingPerson?.primaryPhoneNumber?.id ?: 0L
                val phoneLabel = existingPerson?.primaryPhoneNumber?.label
                listOf(PhoneNumber(id = phoneId, rawNumber = phone.trim(), label = phoneLabel, isPrimary = true))
            } else emptyList()

            val addressesList = if (address.isNotBlank()) {
                val addrId = existingPerson?.primaryAddress?.id ?: 0L
                val addrLabel = existingPerson?.primaryAddress?.label
                listOf(Address(id = addrId, rawAddress = address.trim(), label = addrLabel, isPrimary = true))
            } else emptyList()

            val personToSave = if (personId == -1L) {
                // On récupère la liste actuelle pour placer le nouveau contact tout à la fin
                val currentList = repository.getQuickAccessPersons().firstOrNull() ?: emptyList()
                val nextPosition = currentList.size

                Person(
                    id = 0L,
                    name = name.trim(),
                    photoUri = photoUri,
                    notes = null,
                    isQuickAccess = true,
                    quickAccessPosition = nextPosition,
                    searchableName = name.trim().lowercase(),
                    phoneNumbers = phoneNumbersList,
                    addresses = addressesList,
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
            } else {
                existingPerson?.copy(
                    name = name.trim(),
                    photoUri = photoUri,
                    searchableName = name.trim().lowercase(),
                    phoneNumbers = phoneNumbersList,
                    addresses = addressesList,
                    updatedAt = timestamp
                ) ?: return@launch
            }

            repository.upsertPerson(
                person = personToSave,
                phoneNumbers = phoneNumbersList,
                addresses = addressesList
            )

            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        if (personId != -1L) {
            viewModelScope.launch {
                repository.deletePerson(personId)
                onDeleted()
            }
        } else {
            onDeleted()
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