package io.geekhub.service.accesscontrol.service

import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.domain.ObjectIdentityImpl
import org.springframework.security.acls.domain.PrincipalSid
import org.springframework.security.acls.jdbc.JdbcMutableAclService
import org.springframework.security.acls.model.MutableAcl
import org.springframework.stereotype.Service
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * todo: create an interface for this class and add some logging.
 */
@Service
class AccessControlAdminService(val aclService: JdbcMutableAclService) {

    fun createAccessControl(id: Serializable, businessObject: KClass<*>, owner: String? = null): MutableAcl {

        val objectIdentity = ObjectIdentityImpl(businessObject.java, id)

        val mutableAcl = try {
            this.aclService.readAclById(objectIdentity) as MutableAcl

        } catch (ex: Exception) {
            this.aclService.createAcl(objectIdentity)
        }

        mutableAcl.let {
            it.insertAce(it.entries.size, BasePermission.ADMINISTRATION, it.owner, true)

            owner?.let { owner ->
                it.insertAce(it.entries.size, BasePermission.ADMINISTRATION, PrincipalSid(owner), true)
            }

            this.aclService.updateAcl(it)
        }

        return mutableAcl
    }
}