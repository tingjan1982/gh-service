package io.geekhub.service.specialization.service

import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.springframework.stereotype.Service

@Service
class SpecializationServiceImpl(val repository: SpecializationRepository,
                                val interviewRepository: InterviewRepository,
                                val questionRepository: QuestionRepository) : SpecializationService {

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
            if (interviewRepository.countBySpecialization(it) > 0 || questionRepository.countBySpecialization(it) > 0) {
                throw BusinessException("Cannot delete specialization $id, in use by at least one question or interview.")
            }

            repository.delete(it)
        }

    }
}