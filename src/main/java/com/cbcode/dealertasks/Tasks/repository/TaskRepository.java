package com.cbcode.dealertasks.Tasks.repository;

import com.cbcode.dealertasks.Tasks.model.Task;
import com.cbcode.dealertasks.Users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.createdBy = :created_by")
    List<Task> findByCreatedBy(@Param("created_by") User user);

    @Query("SELECT t FROM Task t WHERE t.car.id = :car_id")
    List<Task> findByCarId(@Param("car_id") Long carId);

    @Query("SELECT t FROM Task t WHERE t.taskStatus = :task_status")
    List<Task> findByTaskStatus(@Param("task_status") String taskStatus);

    @Query("SELECT t FROM Task t WHERE t.taskPriority = :task_priority")
    List<Task> findByTaskPriority(@Param("task_priority") String taskPriority);
}
