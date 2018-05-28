package io.geekhub.service.shared.exception

import javax.persistence.EntityNotFoundException
import kotlin.reflect.KClass

class BusinessObjectNotFoundException(businessObjectType: KClass<*>, id: Any) : EntityNotFoundException("Business object $businessObjectType with [$id] cannot be found")