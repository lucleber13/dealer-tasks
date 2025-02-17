package com.cbcode.dealertasks.Tasks.model;

import com.cbcode.dealertasks.Cars.model.Car;
import com.cbcode.dealertasks.Tasks.model.Enums.TaskPriority;
import com.cbcode.dealertasks.Tasks.model.Enums.TaskStatus;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Valet.model.Valet;
import com.cbcode.dealertasks.Workshop.model.Workshop;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tasks")
@SequenceGenerator(name = "tasks_seq", sequenceName = "tasks_seq", allocationSize = 1, initialValue = 1)
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_seq")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.PENDING;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority = TaskPriority.MEDIUM;

    private Timestamp deadline;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "valet_id")
    private Valet valet;



    public Task() {
    }

    public Task(String title, String description, TaskPriority taskPriority, Timestamp deadline) {
        this.title = title;
        this.description = description;
        this.taskPriority = taskPriority;
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public Valet getValet() {
        return valet;
    }

    public void setValet(Valet valet) {
        this.valet = valet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId())
                && Objects.equals(getTitle(), task.getTitle())
                && Objects.equals(getDescription(), task.getDescription())
                && getTaskStatus() == task.getTaskStatus()
                && getTaskPriority() == task.getTaskPriority()
                && Objects.equals(getDeadline(), task.getDeadline())
                && Objects.equals(getCreatedBy(), task.getCreatedBy())
                && Objects.equals(getCar(), task.getCar())
                && Objects.equals(getWorkshop(), task.getWorkshop())
                && Objects.equals(getValet(), task.getValet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getTaskStatus(), getTaskPriority(),
                getDeadline(), getCreatedBy(), getCar(), getWorkshop(), getValet());
    }
}