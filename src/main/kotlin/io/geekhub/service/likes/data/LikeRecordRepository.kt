package io.geekhub.service.likes.data

import org.springframework.data.repository.PagingAndSortingRepository

interface LikeRecordRepository : PagingAndSortingRepository<LikeRecord, String>