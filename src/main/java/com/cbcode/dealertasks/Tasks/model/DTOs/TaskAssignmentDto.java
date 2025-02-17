package com.cbcode.dealertasks.Tasks.model.DTOs;

import com.cbcode.dealertasks.Tasks.model.Task;
import com.cbcode.dealertasks.Users.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

public class TaskAssignmentDto {

    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    public TaskAssignmentDto() {
    }

    public TaskAssignmentDto(Long id, Task task, User assignedTo) {
        this.id = id;
        this.task = task;
        this.assignedTo = assignedTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskAssignmentDto that = (TaskAssignmentDto) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getTask(), that.getTask())
                && Objects.equals(getAssignedTo(), that.getAssignedTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTask(), getAssignedTo());
    }
}
