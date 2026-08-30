package com.touf.letsgo.data.repository

import androidx.room.withTransaction
import com.touf.letsgo.data.local.LetsGoDatabase
import com.touf.letsgo.data.local.entity.*
import com.touf.letsgo.domain.model.Address
import com.touf.letsgo.domain.model.Person
import com.touf.letsgo.domain.model.PhoneNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.Normalizer

class PersonRepository(
    private val db: LetsGoDatabase
) {
    // Récupère les DAOs
    private val personDao = db.personDao()
    private val phoneDao = db.phoneNumberDao()
    private val addressDao = db.addressDao()

    // ============================================================
    // 1. Récupération des données (Flows)
    // ============================================================

    /**
     * Récupère une personne complète (avec ses numéros et adresses) sous forme de Flow.
     * Émet null si la personne n'existe pas.
     * CORRIGÉ : utilise une requête dédiée (getByIdFlow) au lieu de charger tout le carnet
     * et de filtrer en mémoire — important dès que le carnet dépasse quelques centaines de contacts.
     */
    fun getPersonFlow(personId: Long): Flow<Person?> {
        return combine(
            personDao.getByIdFlow(personId),
            phoneDao.getByPersonId(personId).map { entities ->
                entities.map { PhoneNumber(it.id, it.rawNumber, it.label, it.isPrimary) }
            },
            addressDao.getByPersonId(personId).map { entities ->
                entities.map { Address(it.id, it.rawAddress, it.label, it.isPrimary) }
            }
        ) { personEntity, phoneNumbers, addresses ->
            personEntity?.let {
                Person(
                    id = it.id,
                    name = it.name,
                    photoUri = it.photoUri,
                    notes = it.notes,
                    isQuickAccess = it.isQuickAccess,
                    quickAccessPosition = it.quickAccessPosition,
                    phoneNumbers = phoneNumbers,
                    addresses = addresses,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }.onStart { emit(null) } // Émet null au départ pour éviter l'état vide
    }

    /**
     * Récupère la liste de toutes les personnes (sans les numéros/adresses pour l'instant).
     * Utile pour la recherche ou la liste générale.
     */
    fun getAllPersons(): Flow<List<Person>> {
        return personDao.getAll().map { entities ->
            entities.map {
                Person(
                    id = it.id,
                    name = it.name,
                    photoUri = it.photoUri,
                    notes = it.notes,
                    isQuickAccess = it.isQuickAccess,
                    quickAccessPosition = it.quickAccessPosition,
                    phoneNumbers = emptyList(),
                    addresses = emptyList(),
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    /**
     * Récupère les 6 personnes marquées "quick access".
     */
    fun getQuickAccessPersons(): Flow<List<Person>> {
        return personDao.getQuickAccess().map { entities ->
            entities.map {
                Person(
                    id = it.id,
                    name = it.name,
                    photoUri = it.photoUri,
                    notes = it.notes,
                    isQuickAccess = it.isQuickAccess,
                    quickAccessPosition = it.quickAccessPosition,
                    phoneNumbers = emptyList(),
                    addresses = emptyList(),
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    /**
     * Recherche des personnes par nom (insensible à la casse et aux accents).
     */
    fun searchPersons(query: String): Flow<List<Person>> {
        val normalizedQuery = normalizeString(query)
        return personDao.search(normalizedQuery).map { entities ->
            entities.map {
                Person(
                    id = it.id,
                    name = it.name,
                    photoUri = it.photoUri,
                    notes = it.notes,
                    isQuickAccess = it.isQuickAccess,
                    quickAccessPosition = it.quickAccessPosition,
                    phoneNumbers = emptyList(),
                    addresses = emptyList(),
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    // ============================================================
    // 2. Écriture (insert/update/delete) avec transactions
    // ============================================================

    /**
     * Insère ou met à jour une personne avec ses numéros et adresses.
     * Toute la modification est atomique (transaction).
     * CORRIGÉ : withTransaction (fourni par room-ktx) au lieu de transaction, qui n'existe pas.
     * @return L'ID de la personne (nouveau ou existant).
     */
    suspend fun upsertPerson(
        person: Person,
        phoneNumbers: List<PhoneNumber>? = null,
        addresses: List<Address>? = null
    ): Long {
        return db.withTransaction {
            val now = System.currentTimeMillis()

            // 1. Créer ou mettre à jour l'entité Person
            val personEntity = PersonEntity(
                id = person.id,
                name = person.name,
                photoUri = person.photoUri,
                notes = person.notes,
                isQuickAccess = person.isQuickAccess,
                quickAccessPosition = person.quickAccessPosition,
                searchableName = normalizeString(person.name),
                createdAt = if (person.id == 0L) now else person.createdAt,
                updatedAt = now
            )

            val personId = if (person.id == 0L) {
                // Nouvelle personne
                personDao.insert(personEntity)
            } else {
                // Mise à jour
                personDao.update(personEntity)
                person.id
            }

            // 2. Gérer les numéros de téléphone (remplacement complet)
            phoneNumbers?.let { numbers ->
                phoneDao.deleteAllForPerson(personId)
                numbers.forEach { number ->
                    phoneDao.insert(
                        PhoneNumberEntity(
                            personId = personId,
                            rawNumber = number.rawNumber,
                            label = number.label,
                            isPrimary = number.isPrimary
                        )
                    )
                }
            }

            // 3. Gérer les adresses (remplacement complet)
            addresses?.let { addrList ->
                addressDao.deleteAllForPerson(personId)
                addrList.forEach { address ->
                    addressDao.insert(
                        AddressEntity(
                            personId = personId,
                            rawAddress = address.rawAddress,
                            label = address.label,
                            isPrimary = address.isPrimary
                        )
                    )
                }
            }

            personId
        }
    }

    /**
     * Supprime une personne et toutes ses données (numéros, adresses) grâce à CASCADE.
     */
    suspend fun deletePerson(personId: Long) {
        db.withTransaction {
            phoneDao.deleteAllForPerson(personId)
            addressDao.deleteAllForPerson(personId)
            personDao.deleteById(personId)
        }
    }

    // ============================================================
    // 3. Gestion des principaux (avec transaction)
    // ============================================================

    /**
     * Change le numéro principal d'une personne.
     * Désactive l'ancien principal, active le nouveau.
     */
    suspend fun setPrimaryPhoneNumber(personId: Long, phoneNumberId: Long) {
        db.withTransaction {
            phoneDao.clearPrimaryForPerson(personId)
            val number = phoneDao.getById(phoneNumberId)
            if (number != null) {
                phoneDao.update(number.copy(isPrimary = true))
            }
        }
    }

    /**
     * Change l'adresse principale d'une personne.
     */
    suspend fun setPrimaryAddress(personId: Long, addressId: Long) {
        db.withTransaction {
            addressDao.clearPrimaryForPerson(personId)
            val addr = addressDao.getById(addressId)
            if (addr != null) {
                addressDao.update(addr.copy(isPrimary = true))
            }
        }
    }

    // ============================================================
    // 4. Utilitaire : normalisation des chaînes pour la recherche
    // ============================================================

    /**
     * Supprime les accents et met en minuscules.
     * Exemple : "Éléonore" → "eleonore"
     */
    private fun normalizeString(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "")
            .lowercase()
    }
}