package io.geekhub.service.specialization.service

import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.springframework.stereotype.Service

@Service
class SpecializationServiceImpl(val repository: SpecializationRepository) : SpecializationService {

    override fun saveSpecialization(specialization: Specialization): Specialization {
        return repository.save(specialization)
    }

    override fun getSpecialization(id: String): Specialization {
        return repository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(Specialization::class, id)
        }
    }

    override fun lookupSpecialization(id: String): Specialization? {
        return repository.findById(id).orElse(null)
    }

    override fun getSpecializationByName(name: String): Specialization? {
        return repository.findByName(name)
    }

    override fun getSpecializations(): List<Specialization> {
        return repository.findAll().distinct()
    }

    override fun deleteSpecialization(id: String) {

        this.getSpecialization(id).let {
            repository.delete(it)
        }

    }
}