package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {
}
