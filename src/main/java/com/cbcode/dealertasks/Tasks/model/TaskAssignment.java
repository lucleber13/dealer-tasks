package com.cbcode.dealertasks.Tasks.model;

import com.cbcode.dealertasks.Users.model.User;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "task_assignments")
@SequenceGenerator(name = "task_assignments_seq", sequenceName = "task_assignments_seq", allocationSize = 1, initialValue = 1)
public class TaskAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_assignments_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "assigned_to", nullable = false)
    private User assignedTo;

    public TaskAssignment() {
    }

    public TaskAssignment(Task task, User assignedTo) {
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
        if (!(o instanceof TaskAssignment that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(task, that.task) && Objects.equals(assignedTo, that.assignedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, task, assignedTo);
    }
}
