package com.touf.letsgo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touf.letsgo.data.repository.PersonRepository
import com.touf.letsgo.domain.model.Person
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PersonRepository
) : ViewModel() {

    private val _quickAccessPersons = MutableStateFlow<List<Person?>>(emptyList())
    val quickAccessPersons: StateFlow<List<Person?>> = _quickAccessPersons.asStateFlow()

    init {
        loadQuickAccess()
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

    fun onGoClicked(person: Person) {
        // TODO: lancer l'intent de navigation
    }
}