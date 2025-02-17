package com.cbcode.dealertasks.Workshop.model;

import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopEnum;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopStatusEnum;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "workshops")
@SequenceGenerator(name = "workshops_seq", sequenceName = "workshops_seq", allocationSize = 1, initialValue = 1)
public class Workshop implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workshops_seq")
    private Long id;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "workshop_name")
    @Enumerated(EnumType.STRING)
    private WorkshopStatusEnum workshopStatusEnum = WorkshopStatusEnum.PENDING;

    @Column(name = "workshop_enum")
    @Enumerated(EnumType.STRING)
    private WorkshopEnum workshopEnum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Workshop() {
    }

    public Workshop(String comments, WorkshopStatusEnum workshopStatusEnum, WorkshopEnum workshopEnum) {
        this.comments = comments;
        this.workshopStatusEnum = workshopStatusEnum;
        this.workshopEnum = workshopEnum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public WorkshopStatusEnum getWorkshopStatusEnum() {
        return workshopStatusEnum;
    }

    public void setWorkshopStatusEnum(WorkshopStatusEnum workshopStatusEnum) {
        this.workshopStatusEnum = workshopStatusEnum;
    }

    public WorkshopEnum getWorkshopEnum() {
        return workshopEnum;
    }

    public void setWorkshopEnum(WorkshopEnum workshopEnum) {
        this.workshopEnum = workshopEnum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Workshop workshop = (Workshop) o;
        return Objects.equals(getId(), workshop.getId())
                && Objects.equals(getComments(), workshop.getComments())
                && getWorkshopStatusEnum() == workshop.getWorkshopStatusEnum()
                && getWorkshopEnum() == workshop.getWorkshopEnum()
                && Objects.equals(getUser(), workshop.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getComments(), getWorkshopStatusEnum(), getWorkshopEnum(), getUser());
    }
}
