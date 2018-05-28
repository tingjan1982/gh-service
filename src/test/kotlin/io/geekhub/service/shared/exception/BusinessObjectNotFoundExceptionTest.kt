package io.geekhub.service.shared.exception

import io.geekhub.service.questions.model.Question
import org.junit.jupiter.api.Test

internal class BusinessObjectNotFoundExceptionTest {


    @Test
    fun test() {

        val businessObjectNotFoundException = BusinessObjectNotFoundException(Question::class, "id")
        println(businessObjectNotFoundException)
    }
}