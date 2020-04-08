package io.geekhub.service.specialization.service

import io.geekhub.service.specialization.repository.Specialization

interface SpecializationService {

    fun saveSpecialization(specialization: Specialization): Specialization

    fun getSpecialization(id: String): Specialization

    fun lookupSpecialization(id: String): Specialization?

    fun getSpecializations(): List<Specialization>
}