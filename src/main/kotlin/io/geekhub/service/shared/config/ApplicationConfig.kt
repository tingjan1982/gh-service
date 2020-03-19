package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.auditing.DefaultAuditorProvider
import io.geekhub.service.specialization.repository.SpecializationRepository
import io.geekhub.service.user.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


/**
 * Reference on @EnableTransactionManagement:
 * https://stackoverflow.com/questions/40724100/enabletransactionmanagement-in-spring-boot
 *
 * Reference on @EnableSwagger2
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * https://springframework.guru/spring-boot-restful-api-documentation-with-swagger-2/
 */
@Configuration
@EnableMongoRepositories(basePackageClasses = [QuestionRepository::class, ClientAccountRepository::class, SpecializationRepository::class, InterviewRepository::class, UserRepository::class])
@EnableMongoAuditing
@EnableSwagger2
class ApplicationConfig {

    /**
     * Reference: https://docs.spring.io/spring-data/jpa/docs/2.0.6.RELEASE/reference/html/#auditing
     */
    @Bean
    fun auditorProvider(): AuditorAware<ClientAccount> {
        return DefaultAuditorProvider()
    }

    @Bean
    fun api(): Docket {
        val basicAuthScheme = BasicAuth("geekhub")
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.geekhub.service"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(listOf(basicAuthScheme))
    }

    /**
     * Information on licensing a private project:
     * https://softwareengineering.stackexchange.com/questions/312009/what-kind-of-license-to-put-a-private-project
     */
    private fun apiInfo(): ApiInfo {
        return ApiInfo(
                "GeekHub REST API",
                "GeekHub API Backend",
                "0.1.0",
                "Terms of service",
                Contact("Joe Lin", "www.geekhub.io", "tingjan1982@geekhub.io"),
                "Copyright (c) 2018 Joe Lin", "",
                setOf())
    }
}