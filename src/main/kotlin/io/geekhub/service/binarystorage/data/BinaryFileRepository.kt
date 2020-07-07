package io.geekhub.service.binarystorage.data

import org.springframework.data.mongodb.repository.MongoRepository

interface BinaryFileRepository : MongoRepository<BinaryFile, String>