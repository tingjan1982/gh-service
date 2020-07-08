package io.geekhub.service.binarystorage.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.binarystorage.data.BinaryFile
import io.geekhub.service.binarystorage.data.BinaryFileRepository
import org.bson.types.Binary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import javax.transaction.Transactional

/**
 * https://www.baeldung.com/spring-boot-mongodb-upload-file
 * https://stackoverflow.com/questions/4796914/store-images-in-a-mongodb-database/4800186
 */
@Service
@Transactional
class BinaryStorageServiceImpl(val binaryFileRepository: BinaryFileRepository,
                               val clientUserService: ClientUserService) : BinaryStorageService {

    /**
     * Encountered SSLHandshakeException: Received fatal alert: record_overflow while persisting BinaryFile.
     *
     * https://stackoverflow.com/questions/54119613/sslhandshakeexception-received-fatal-alert-record-overflow
     *
     * todo: find opportunity to upgrade to latest jdk 13
     */
    override fun saveClientUserAvatar(clientUser: ClientUser, multipartFile: MultipartFile): ClientUser {

        clientUser.avatarBinary?.let {
            deleteBinary(it)
        }
        
        BinaryFile(title = multipartFile.name, binary = Binary(multipartFile.bytes)).let {
            saveBinaryFile(it).let { saved ->
                clientUser.avatarBinary = saved

                return clientUserService.saveClientUser(clientUser)
            }
        }
    }

    override fun saveBinaryFile(binaryFile: BinaryFile): BinaryFile {
        return binaryFileRepository.save(binaryFile)
    }

    override fun deleteBinary(binaryFile: BinaryFile) {
        binaryFileRepository.delete(binaryFile)
    }
}