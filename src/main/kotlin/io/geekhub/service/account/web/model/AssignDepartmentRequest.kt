package io.geekhub.service.account.web.model

import javax.validation.constraints.NotBlank

data class AssignDepartmentRequest(@field:NotBlank val departmentId: String)
