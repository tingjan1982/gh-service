package io.geekhub.service.binarystorage.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.binarystorage.data.BinaryFile
import org.springframework.web.multipart.MultipartFile

interface BinaryStorageService {

    fun saveClientUserAvatar(clientUser: ClientUser, multipartFile: MultipartFile): ClientUser

    fun saveBinaryFile(binaryFile: BinaryFile): BinaryFile

    fun deleteBinary(binaryFile: BinaryFile)
}