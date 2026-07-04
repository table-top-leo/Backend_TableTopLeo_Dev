package com.backendDev.repo;

import com.backendDev.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
    Optional<OrderPayment> findByPaymentId(String paymentId);
    Optional<OrderPayment> findByOrderId(String orderId);
    Optional<OrderPayment> findByTransactionId(String transactionId);
    List<OrderPayment> findAllByOrderId(String orderId);
}