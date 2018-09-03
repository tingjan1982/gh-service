package io.geekhub.service.accesscontrol.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.user.model.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
internal class AccessControlAdminServiceTest {

    @Autowired
    private lateinit var accessControlAdminService: AccessControlAdminService

    @Test
    @WithMockUser
    @Transactional
    fun createAccessControl() {

        accessControlAdminService.createAccessControl("abc-user-id", User::class, "abc-owner").let {
            assert(it.owner).isNotNull()
            assert(it.id).isNotNull()
            assert(it.entries.size).isEqualTo(2)
        }
    }
}