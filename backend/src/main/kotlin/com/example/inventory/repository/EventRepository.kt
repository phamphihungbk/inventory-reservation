package com.example.inventory.repository

import com.example.inventory.entity.Event
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EventRepository : JpaRepository<Event, Long> {
    @Query(
        value = """
            SELECT
                e.id AS id,
                e.name AS name,
                e.venue AS venue,
                e.city AS city,
                e.country AS country,
                e.event_date AS eventDate,
                (
                    ts_rank(
                        to_tsvector('simple', coalesce(e.name, '') || ' ' || coalesce(e.venue, '') || ' ' || coalesce(e.city, '') || ' ' || coalesce(e.country, '')),
                        plainto_tsquery('simple', :query)
                    )
                    + greatest(
                        similarity(e.name, :query),
                        similarity(e.venue, :query),
                        similarity(e.city, :query),
                        similarity(e.country, :query)
                    )
                ) AS rank
            FROM events e
            WHERE
                to_tsvector('simple', coalesce(e.name, '') || ' ' || coalesce(e.venue, '') || ' ' || coalesce(e.city, '') || ' ' || coalesce(e.country, ''))
                    @@ plainto_tsquery('simple', :query)
                OR similarity(e.name, :query) > 0.2
                OR similarity(e.venue, :query) > 0.2
                OR similarity(e.city, :query) > 0.2
                OR similarity(e.country, :query) > 0.2
                OR e.name ILIKE concat('%', :query, '%')
                OR e.venue ILIKE concat('%', :query, '%')
                OR e.city ILIKE concat('%', :query, '%')
                OR e.country ILIKE concat('%', :query, '%')
            ORDER BY rank DESC, e.event_date ASC
        """,
        countQuery = """
            SELECT count(*)
            FROM events e
            WHERE
                to_tsvector('simple', coalesce(e.name, '') || ' ' || coalesce(e.venue, '') || ' ' || coalesce(e.city, '') || ' ' || coalesce(e.country, ''))
                    @@ plainto_tsquery('simple', :query)
                OR similarity(e.name, :query) > 0.2
                OR similarity(e.venue, :query) > 0.2
                OR similarity(e.city, :query) > 0.2
                OR similarity(e.country, :query) > 0.2
                OR e.name ILIKE concat('%', :query, '%')
                OR e.venue ILIKE concat('%', :query, '%')
                OR e.city ILIKE concat('%', :query, '%')
                OR e.country ILIKE concat('%', :query, '%')
        """,
        nativeQuery = true,
    )
    fun search(@Param("query") query: String, pageable: Pageable): Page<EventSearchProjection>
}
