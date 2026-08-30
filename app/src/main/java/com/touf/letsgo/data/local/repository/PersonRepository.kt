package com.touf.letsgo.data.local.repository

import androidx.room.Transaction
import com.touf.letsgo.data.local.dao.AddressDao
import com.touf.letsgo.data.local.dao.PersonDao
import com.touf.letsgo.data.local.dao.PhoneNumberDao
import com.touf.letsgo.data.local.entity.AddressEntity
import com.touf.letsgo.data.local.entity.PersonEntity
import com.touf.letsgo.data.local.entity.PersonWithDetails
import com.touf.letsgo.data.local.entity.PhoneNumberEntity
import com.touf.letsgo.domain.model.Address
import com.touf.letsgo.domain.model.Person
import com.touf.letsgo.domain.model.PhoneNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepository(
    private val personDao: PersonDao,
    private val phoneNumberDao: PhoneNumberDao,
    private val addressDao: AddressDao
) {

    fun getAllPersons(): Flow<List<Person>> {
        return personDao.getAllWithDetails().map { entities ->
            entities.map { it.toDomainComplete() }
        }
    }

    fun getPersonFlow(personId: Long): Flow<Person?> {
        return personDao.getByIdWithDetailsFlow(personId).map { entity ->
            entity?.toDomainComplete()
        }
    }

    fun getQuickAccessPersons(): Flow<List<Person>> {
        return personDao.getQuickAccessWithDetails().map { entities ->
            entities.map { it.toDomainComplete() }
        }
    }

    private fun PersonWithDetails.toDomainComplete(): Person {
        val base = person.toDomain()
        return base.copy(
            phoneNumbers = phoneNumbers.map {
                PhoneNumber(
                    id = it.id,
                    rawNumber = it.rawNumber ?: "",
                    label = it.label ?: "",
                    isPrimary = it.isPrimary
                )
            },
            addresses = addresses.map {
                Address(
                    id = it.id,
                    rawAddress = it.rawAddress ?: "",
                    label = it.label ?: "",
                    isPrimary = it.isPrimary
                )
            }
        )
    }

    @Transaction
    suspend fun upsertPerson(
        person: Person,
        phoneNumbers: List<PhoneNumber>,
        addresses: List<Address>
    ): Long {
        val personEntity = PersonEntity.fromDomain(person)

        val personId = if (personEntity.id == 0L) {
            personDao.insert(personEntity)
        } else {
            personDao.update(personEntity)
            personEntity.id
        }

        val normalizedPhones = if (phoneNumbers.count { it.isPrimary } > 1) {
            var primaryFound = false
            phoneNumbers.map { phone ->
                if (phone.isPrimary && !primaryFound) {
                    primaryFound = true
                    phone
                } else {
                    phone.copy(isPrimary = false)
                }
            }
        } else {
            phoneNumbers
        }

        val normalizedAddresses = if (addresses.count { it.isPrimary } > 1) {
            var primaryFound = false
            addresses.map { address ->
                if (address.isPrimary && !primaryFound) {
                    primaryFound = true
                    address
                } else {
                    address.copy(isPrimary = false)
                }
            }
        } else {
            addresses
        }

        val phoneEntities = normalizedPhones.map { PhoneNumberEntity.fromDomain(it, personId) }
        val addressEntities = normalizedAddresses.map { AddressEntity.fromDomain(it, personId) }

        phoneNumberDao.insertPhones(phoneEntities)
        addressDao.insertAddresses(addressEntities)

        return personId
    }

    @Transaction
    suspend fun setPrimaryPhone(personId: Long, phoneId: Long) {
        phoneNumberDao.setPrimaryPhone(personId, phoneId)
    }

    @Transaction
    suspend fun setPrimaryAddress(personId: Long, addressId: Long) {
        addressDao.setPrimaryAddress(personId, addressId)
    }

    suspend fun deletePerson(personId: Long) {
        personDao.deleteById(personId)
    }

    suspend fun removeFromQuickAccess(personId: Long) {
        val personEntity = personDao.getById(personId)
        personEntity?.let {
            val updated = it.copy(
                isQuickAccess = false,
                quickAccessPosition = null,
                updatedAt = System.currentTimeMillis()
            )
            personDao.update(updated)
        }
    }
}