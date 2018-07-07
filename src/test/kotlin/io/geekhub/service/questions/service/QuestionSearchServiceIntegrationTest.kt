package io.geekhub.service.questions.service

import assertk.assert
import assertk.assertions.isEqualTo
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.SearchRequest
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

@IntegrationTest
internal class QuestionSearchServiceIntegrationTest {

    @Autowired
    lateinit var questionSearchService: QuestionSearchService

    @Autowired
    lateinit var questionService: QuestionService

    @BeforeAll
    fun prepare() {
        this.questionService.saveQuestion(Question("this is a simple question"))
        this.questionService.saveQuestion(Question("simple"))
        this.questionService.saveQuestion(Question("simplequestion"))
        this.questionService.saveQuestion(Question("---simple---"))
    }

    @Test
    fun `search questions containing text`() {
        val searchQuestions = this.questionSearchService.searchQuestions(SearchRequest("simple", page = PageRequest.of(1, 50)))

        assert(searchQuestions.totalElements).isEqualTo(4.toLong())
        assert(searchQuestions.totalPages).isEqualTo(1)
        assert(searchQuestions.pageable.pageSize).isEqualTo(50)
    }

    @Test
    fun `search questions with pagination change`() {
        val searchQuestions = this.questionSearchService.searchQuestions(SearchRequest("simple", page = PageRequest.of(1, 1)))

        assert(searchQuestions.totalElements).isEqualTo(4.toLong())
        assert(searchQuestions.totalPages).isEqualTo(4)
        assert(searchQuestions.pageable.pageSize).isEqualTo(1)
        assert(searchQuestions.pageable.pageNumber).isEqualTo(1)
    }

    /**
     * https://stackoverflow.com/questions/40268446/junit-5-how-to-assert-an-exception-is-thrown
     */
    @Test
    fun `test pagination with negative case`() {

        assertThrows(IllegalArgumentException::class.java, {
            this.questionSearchService.searchQuestions(SearchRequest("simple", page = PageRequest.of(-1, 1)))
        })

        assertThrows(IllegalArgumentException::class.java, {
            this.questionSearchService.searchQuestions(SearchRequest("simple", page = PageRequest.of(1, -1)))
        })
    }
}