package io.geekhub.service.specialization.service

import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.springframework.stereotype.Service

@Service
class SpecializationServiceImpl(val repository: SpecializationRepository) : SpecializationService {

    override fun saveSpecialization(specialization: Specialization): Specialization {
        return repository.save(specialization)
    }

    override fun getSpecialization(id: String): Specialization {
        return repository.findById(id).orElseThrow()
    }
}