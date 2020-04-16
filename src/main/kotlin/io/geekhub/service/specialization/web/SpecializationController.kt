package io.geekhub.service.specialization.web

import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.specialization.service.SpecializationService
import io.geekhub.service.specialization.web.model.SpecializationRequest
import io.geekhub.service.specialization.web.model.SpecializationResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/specializations")
class SpecializationController(val specializationService: SpecializationService) {

    @PostMapping
    fun saveSpecialization(@Valid @RequestBody request: SpecializationRequest): SpecializationResponse {

        request.toEntity().let {
            return specializationService.saveSpecialization(it).toDTO()
        }
    }

    @GetMapping("/{id}")
    fun getSpecialization(@PathVariable id: String): SpecializationResponse {
        return specializationService.getSpecialization(id).toDTO()
    }

    @GetMapping
    fun getSpecializations(): List<SpecializationResponse> {
        return specializationService.getSpecializations().map { it.toDTO() }.toList()
    }

    @PostMapping("/{id}")
    fun updateSpecialization(@PathVariable id: String, @Valid @RequestBody request: SpecializationRequest): SpecializationResponse {

        specializationService.getSpecialization(id).let {
            it.name = request.name

            return specializationService.saveSpecialization(it).toDTO()
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteSpecialization(@PathVariable id: String) {

        specializationService.deleteSpecialization(id)
    }

}

