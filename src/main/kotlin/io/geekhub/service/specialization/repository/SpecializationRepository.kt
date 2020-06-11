package io.geekhub.service.specialization.repository

import org.springframework.data.repository.CrudRepository

interface SpecializationRepository : CrudRepository<Specialization, String> {

    fun findByName(name: String): Specialization?

}
