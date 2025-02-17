package com.cbcode.dealertasks.Tasks.repository;

import com.cbcode.dealertasks.Tasks.model.TaskAssignment;
import com.cbcode.dealertasks.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.assignedTo = :assigned_to")
    List<TaskAssignment> findByAssignedTo(@Param("assigned_to") User user);
}
