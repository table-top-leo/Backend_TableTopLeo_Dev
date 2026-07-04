package com.backendDev.repo;

import com.backendDev.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderId(String orderId);
    List<CustomerOrder> findAllByAdminIdAndBusinessIdOrderByCreatedAtDesc(String adminId, String businessId);
    List<CustomerOrder> findAllByAdminIdOrderByCreatedAtDesc(String adminId);
}