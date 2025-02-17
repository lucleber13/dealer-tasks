package com.cbcode.dealertasks.Workshop.repository;

import com.cbcode.dealertasks.Workshop.model.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {

    @Query("SELECT w FROM Workshop w WHERE w.user.id = :user_id")
    Optional<Workshop> findByUser_Id(@Param("user_id") Long user);
}
