package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findByUserId(Integer userId);
    @Query("SELECT c FROM Car c WHERE LOWER(c.brand) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.model) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Car> searchCarsIgnoreCase(@Param("query") String query);

}
