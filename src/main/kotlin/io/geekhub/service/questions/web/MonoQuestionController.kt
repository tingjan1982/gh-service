package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.MonoQuestion
import io.geekhub.service.questions.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monoquestions")
class MonoQuestionController(service: QuestionService<MonoQuestion>) : AbstractQuestionController<MonoQuestion>(service)