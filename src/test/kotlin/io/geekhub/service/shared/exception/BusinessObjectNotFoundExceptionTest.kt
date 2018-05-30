package io.geekhub.service.shared.exception

import io.geekhub.service.questions.model.Question
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class BusinessObjectNotFoundExceptionTest {

    companion object {
        val logger : Logger = LoggerFactory.getLogger(BusinessObjectNotFoundExceptionTest::class.java)
    }

    @Test
    fun test() {

        val businessObjectNotFoundException = BusinessObjectNotFoundException(Question::class, "questionID")
        logger.debug("{}", businessObjectNotFoundException)

        assert(businessObjectNotFoundException.message!!.contains("questionID"))
    }
}