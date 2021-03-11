package io.geekhub.service.account.repository

import io.geekhub.service.account.web.model.ClientDepartmentResponse
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
@CompoundIndexes(CompoundIndex(name = "unique_department_index", def = "{'name': 1, 'clientAccount': 1}", unique = true))
data class ClientDepartment(
    @Id
    var id: String? = null,
    var name: String,
    @DBRef
    val clientAccount: ClientAccount) {

    fun toDTO() = ClientDepartmentResponse(
        id = this.id.toString(),
        departmentName = this.name)
}
