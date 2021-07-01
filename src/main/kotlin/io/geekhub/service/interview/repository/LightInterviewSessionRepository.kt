package io.geekhub.service.interview.repository

import io.geekhub.service.interview.model.LightInterviewSession
import org.springframework.data.mongodb.repository.MongoRepository

interface LightInterviewSessionRepository : MongoRepository<LightInterviewSession, String>