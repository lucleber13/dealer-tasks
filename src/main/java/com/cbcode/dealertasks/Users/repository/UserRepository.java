package com.cbcode.dealertasks.Users.repository;

import com.cbcode.dealertasks.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    Boolean existsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :first_name, '%'))")
    Optional<User> findUserByFirstNameContainingIgnoreCase(@Param("first_name") String firstName);

    @Query("SELECT u FROM User u WHERE u.resetToken = :reset_token")
    Optional<User> findByResetToken(@Param("reset_token") String resetToken);
}
