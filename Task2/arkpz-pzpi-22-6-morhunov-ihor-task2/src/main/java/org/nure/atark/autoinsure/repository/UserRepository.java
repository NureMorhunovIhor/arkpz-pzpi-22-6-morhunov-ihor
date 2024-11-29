package org.nure.atark.autoinsure.repository;

import org.nure.atark.autoinsure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    default User findByIdOrThrow(Integer id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found."));
    }
    Optional<User> findByEmail(String email);
}
