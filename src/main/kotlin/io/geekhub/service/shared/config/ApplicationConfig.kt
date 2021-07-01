package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.auth0.service.Auth0ApiProperties
import io.geekhub.service.auth0.service.Auth0ManagementApiProperties
import io.geekhub.service.shared.auditing.DefaultAuditorProvider
import io.geekhub.service.shared.web.filter.ClientAccountFilter
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.IndexDefinition
import org.springframework.data.mongodb.core.index.IndexResolver
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


/**
 * Reference on @EnableTransactionManagement:
 * https://stackoverflow.com/questions/40724100/enabletransactionmanagement-in-spring-boot
 *
 * Reference on @EnableSwagger2
 * http://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
 * https://springframework.guru/spring-boot-restful-api-documentation-with-swagger-2/
 */
@Configuration
@EnableCaching
@EnableSwagger2
@EnableConfigurationProperties(Auth0ManagementApiProperties::class, Auth0ApiProperties::class)
class ApplicationConfig(
    val mongoTemplate: MongoTemplate,
    val mongoMappingContext: MongoMappingContext,
    val clientAccountFilter: ClientAccountFilter
) {

    @Bean
    fun cacheManager(): CacheManager? {
        val cacheManager = ConcurrentMapCacheManager()
        cacheManager.setCacheNames(listOf("managementToken", "test"))

        return cacheManager
    }

    @Bean
    fun loggingFilter(): FilterRegistrationBean<ClientAccountFilter> {
        val registrationBean = FilterRegistrationBean<ClientAccountFilter>();
        registrationBean.filter = clientAccountFilter;
        registrationBean.addUrlPatterns(
            "/users/*",
            "/organizations/*",
            "/departments/*",
            "/questions/*",
            "/interviews/*",
            "/publishedInterviews/*",
            "/interviewSessions/*"
        )

        return registrationBean;
    }

    /**
     * Ensures MongoDB indexes are created after application is ready.
     *
     * @See MongoConfigurationSupport.autoIndexCreation()
     */
    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {

        val resolver: IndexResolver = MongoPersistentEntityIndexResolver(mongoMappingContext)
        // consider only entities that are annotated with @Document
        mongoMappingContext.persistentEntities
            .stream()
            .filter {
                it.isAnnotationPresent(Document::class.java)
            }
            .forEach { it: MongoPersistentEntity<*>? ->
                val indexOps = mongoTemplate.indexOps(it!!.type)
                resolver.resolveIndexFor(it.type).forEach { indexDefinition: IndexDefinition ->
                    indexOps.ensureIndex(indexDefinition)
                }
            }
    }

    /**
     * Reference: https://docs.spring.io/spring-data/jpa/docs/2.0.6.RELEASE/reference/html/#auditing
     */
    @Bean
    fun auditorProvider(): AuditorAware<String> {
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
            .ignoredParameterTypes(ClientAccount::class.java)
            .ignoredParameterTypes(ClientUser::class.java)
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
            setOf()
        )
    }
}