package com.caseyquinn.personal_website.repository;

import com.caseyquinn.personal_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds user by username.
     *
     * @param username the username
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds user by email.
     *
     * @param email the email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if username exists.
     *
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if email exists.
     *
     * @param email the email
     * @return true if exists
     */
    boolean existsByEmail(String email);
}
