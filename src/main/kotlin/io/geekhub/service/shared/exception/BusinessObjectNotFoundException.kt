package io.geekhub.service.shared.exception

import javax.persistence.EntityNotFoundException

class BusinessObjectNotFoundException : EntityNotFoundException {

    lateinit var businessObjectType: Any

    constructor(businessObjectType: Any, id: Any): super("Business object $businessObjectType with [$id] cannot be found") {
        this.businessObjectType = businessObjectType
    }
}