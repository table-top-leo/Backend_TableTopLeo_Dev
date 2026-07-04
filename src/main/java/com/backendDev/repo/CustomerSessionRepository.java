package com.backendDev.repo;

import com.backendDev.model.CustomerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {
    Optional<CustomerSession> findBySessionId(String sessionId);
    Optional<CustomerSession> findBySessionIdAndBusinessId(String sessionId, String businessId);
}