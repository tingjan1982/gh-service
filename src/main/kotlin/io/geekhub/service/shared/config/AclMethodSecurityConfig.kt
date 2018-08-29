package io.geekhub.service.shared.config

import org.springframework.cache.ehcache.EhCacheFactoryBean
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.support.GenericConversionService
import org.springframework.core.env.Environment
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.acls.AclPermissionEvaluator
import org.springframework.security.acls.domain.*
import org.springframework.security.acls.jdbc.BasicLookupStrategy
import org.springframework.security.acls.jdbc.JdbcMutableAclService
import org.springframework.security.acls.jdbc.LookupStrategy
import org.springframework.security.acls.model.PermissionGrantingStrategy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.util.ReflectionUtils
import javax.sql.DataSource


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class AclMethodSecurityConfig(val dataSource: DataSource, val environment: Environment) : GlobalMethodSecurityConfiguration() {

    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return defaultMethodSecurityExpressionHandler()
    }

    @Bean
    fun defaultMethodSecurityExpressionHandler(): MethodSecurityExpressionHandler {
        val expressionHandler = DefaultMethodSecurityExpressionHandler()
        val permissionEvaluator = AclPermissionEvaluator(aclService())
        expressionHandler.setPermissionEvaluator(permissionEvaluator)

        return expressionHandler
    }

    // todo: somehow remove this duplicate method
    fun determineDataSourceType(): SecurityConfig.DataSourceType {

        return when {
            this.environment.activeProfiles.any { it == "test" || it == "embedded" } -> SecurityConfig.DataSourceType.EMBEDDED
            else -> SecurityConfig.DataSourceType.POSTGRESQL
        }
    }

    @Bean
    fun aclService(): JdbcMutableAclService {
        return JdbcMutableAclService(this.dataSource, lookupStrategy(), aclCache()).also {
            it.setAclClassIdSupported(true)

            if (this.determineDataSourceType() == SecurityConfig.DataSourceType.POSTGRESQL) {
                it.setSidIdentityQuery("select lastval()")
                it.setClassIdentityQuery("select lastval()")
            }
        }
    }

    @Bean
    fun lookupStrategy(): LookupStrategy {

        return BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                ConsoleAuditLogger()
        ).also {
            it.setAclClassIdSupported(true)

            this.veryDirtyInjectConversionServiceToAclClassIdUtilsViaReflection(it)
        }
    }

    fun veryDirtyInjectConversionServiceToAclClassIdUtilsViaReflection(it: BasicLookupStrategy) {

        val aclClassIdUtilsType = Class.forName("org.springframework.security.acls.jdbc.AclClassIdUtils")
        val setConversionServiceMethod = aclClassIdUtilsType.getMethod("setConversionService", ConversionService::class.java).also {
            it.isAccessible = true
        }

        val aclClassIdUtilsField = ReflectionUtils.findField(it.javaClass, "aclClassIdUtils")!!.also {
            it.isAccessible = true
        }
        
        setConversionServiceMethod.invoke(aclClassIdUtilsField.get(it), GenericConversionService())
    }


    @Bean
    fun aclCache(): EhCacheBasedAclCache {
        return EhCacheBasedAclCache(
                aclEhCacheFactoryBean().getObject(),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        )
    }

    @Bean
    fun aclEhCacheFactoryBean(): EhCacheFactoryBean {
        val ehCacheFactoryBean = EhCacheFactoryBean()
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject()!!)
        ehCacheFactoryBean.setCacheName("aclCache")
        return ehCacheFactoryBean
    }

    @Bean
    fun aclCacheManager(): EhCacheManagerFactoryBean {
        return EhCacheManagerFactoryBean()
    }

    @Bean
    fun aclAuthorizationStrategy(): AclAuthorizationStrategy {
        return AclAuthorizationStrategyImpl(SimpleGrantedAuthority("ROLE_ADMIN"))
    }

    @Bean
    fun permissionGrantingStrategy(): PermissionGrantingStrategy {
        return DefaultPermissionGrantingStrategy(ConsoleAuditLogger())
    }
}