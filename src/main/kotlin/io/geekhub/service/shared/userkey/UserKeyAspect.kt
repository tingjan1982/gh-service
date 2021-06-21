package io.geekhub.service.shared.userkey

import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

@Component
@Aspect
class UserKeyAspect {

    @Before(value = "saveMethods() && args(obj, ..)")
    fun intercept(obj: Any) {

        UserKeyFilter.UserKeyHolder.getUserKey()?.let {

            if (obj is UserKeyObject) {
                obj.userKey = it
                println("Assigned user key $it to object: $obj")
            }
        }
    }

    @Pointcut("execution(* save*(..))")
    fun saveMethods() {
    }
}