package io.geekhub.service.accesscontrol.aspect

import io.geekhub.service.accesscontrol.service.AccessControlAdminService
import io.geekhub.service.user.model.User
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.persistence.Entity
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@Aspect
@Component
class DomainSecurityAspect(val accessControlAdminService: AccessControlAdminService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DomainSecurityAspect::class.java)
        val supportedEntities = listOf(User::class)
    }

    @Pointcut("@within(org.springframework.stereotype.Service) && within(io.geekhub.service..*) && execution(* create*(..))")
    fun serviceClassExpression() {

    }

    @Around("serviceClassExpression()")
    fun createAclForBusinessObject(pjp: ProceedingJoinPoint): Any {

        return pjp.proceed().let {
            it::class.findAnnotation<Entity>()?.let { _ ->
                if (isSupportedEntities(it::class)) {
                    logger.info("Creating ACL record for business object: $it")

                    val id = it::class.java.getMethod("getId").invoke(it) as Serializable
                    val owner = (it as? User)?.username

                    accessControlAdminService.createAccessControl(id, it::class, owner)
                }
            }

            return@let it
        }
    }

    private fun isSupportedEntities(clazz: KClass<*>): Boolean {
        return supportedEntities.any { it -> it == clazz }
    }
}