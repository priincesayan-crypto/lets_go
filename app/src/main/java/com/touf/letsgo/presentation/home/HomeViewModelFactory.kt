package com.touf.letsgo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.touf.letsgo.data.repository.PersonRepository

/**
 * Factory nécessaire car HomeViewModel a un constructeur avec paramètre (PersonRepository).
 * Sans cette factory, viewModel() ne sait pas comment créer HomeViewModel
 * et l'application plante au lancement avec :
 * "Cannot create an instance of class HomeViewModel".
 */
class HomeViewModelFactory(
    private val repository: PersonRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}