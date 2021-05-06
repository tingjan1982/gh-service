package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.binarystorage.data.BinaryFileRepository
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.likes.data.LikeRecordRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@Configuration
@EnableMongoRepositories(basePackageClasses = [
    QuestionRepository::class,
    ClientAccountRepository::class,
    SpecializationRepository::class,
    InterviewRepository::class,
    InterviewSessionRepository::class,
    LikeRecordRepository::class,
    BinaryFileRepository::class])
@EnableMongoAuditing
class DataSourceConfig {

    @Bean
    fun transactionManager(dbFactory: MongoDbFactory): MongoTransactionManager {
        return MongoTransactionManager(dbFactory)
    }
}