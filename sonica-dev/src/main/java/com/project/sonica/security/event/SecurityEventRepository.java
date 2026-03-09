package com.project.sonica.security.event;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
	Page<SecurityEvent> findByEventType(String eventType, Pageable pageable);

	Page<SecurityEvent> findByUsername(String username, Pageable pageable);

	Page<SecurityEvent> findByTimestampBetween(Instant start, Instant end, Pageable pageable);
}
