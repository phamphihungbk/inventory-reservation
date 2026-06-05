package com.example.notification.repository

import com.example.notification.entity.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedEventRepository : JpaRepository<ProcessedEvent, String>
