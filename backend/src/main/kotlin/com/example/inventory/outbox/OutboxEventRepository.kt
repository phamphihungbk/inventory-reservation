package com.example.inventory.outbox

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {
    @Query(
        """
        select event from OutboxEvent event
        where event.status in :statuses
          and event.nextAttemptAt <= :now
        order by event.createdAt asc
        """,
    )
    fun findPublishable(
        @Param("statuses") statuses: Collection<OutboxEventStatus>,
        @Param("now") now: Instant,
        pageable: Pageable,
    ): List<OutboxEvent>
}
