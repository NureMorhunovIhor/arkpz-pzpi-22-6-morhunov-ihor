package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
    @Query("SELECT p FROM Policy p WHERE p.car.user.id = :userId")
    List<Policy> findByUserId(@Param("userId") Integer userId);

}
