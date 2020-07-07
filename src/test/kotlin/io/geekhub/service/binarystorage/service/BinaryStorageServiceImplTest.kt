package io.geekhub.service.binarystorage.service

import assertk.assertThat
import assertk.assertions.isNotNull
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile

@IntegrationTest
internal class BinaryStorageServiceImplTest(@Autowired val binaryStorageService: BinaryStorageService) {

    @Autowired
    lateinit var clientUser: ClientUser

    @Test
    fun saveClientUserAvatar() {

        MockMultipartFile("Mock Avatar", "mockAvatar.jpg", "image/jpeg", "image binary".toByteArray()).let {

            binaryStorageService.saveClientUserAvatar(clientUser, it).run {
                assertThat(this.avatarBinary?.id.toString()).isNotNull()
            }
        }
    }
}