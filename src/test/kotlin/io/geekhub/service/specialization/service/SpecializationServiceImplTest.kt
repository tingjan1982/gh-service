package io.geekhub.service.specialization.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class SpecializationServiceImplTest(@Autowired val specializationService: SpecializationService) {

    @Test
    fun saveSpecialization() {

        specializationService.saveSpecialization(Specialization(name = "developer")).let {

            assertThat(it.id).isNotNull()

            assertThat(specializationService.getSpecialization(it.id.toString())).isNotNull()
            assertThat(specializationService.getSpecializationByName("developer")).isNotNull()
            assertThat(specializationService.getSpecializations()).hasSize(2)
            assertThat(specializationService.lookupSpecialization(it.id.toString())).isNotNull()

            specializationService.deleteSpecialization(it.id.toString())

            assertThat(specializationService.getSpecializations()).hasSize(1)
        }
    }
}