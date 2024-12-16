package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query("SELECT p FROM Payment p WHERE p.policy.car.user.id = :userId")
    List<Payment> findPaymentsByUserId(@Param("userId") Integer userId);

}
