package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.MultipleChoice
import io.geekhub.service.questions.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/multiplechoices")
class MultipleChoiceController(service: QuestionService<MultipleChoice>) : AbstractQuestionController<MultipleChoice>(service)