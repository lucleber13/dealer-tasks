package com.cbcode.dealertasks.Workshop.model.DTOs;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopEnum;
import com.cbcode.dealertasks.Workshop.model.Enums.WorkshopStatusEnum;
import jakarta.persistence.*;

import java.util.Objects;

public class WorkshopDto {

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
    private UserDto user;

    public WorkshopDto() {
    }

    public WorkshopDto(String comments, WorkshopStatusEnum workshopStatusEnum, WorkshopEnum workshopEnum) {
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WorkshopDto that = (WorkshopDto) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getComments(), that.getComments())
                && getWorkshopStatusEnum() == that.getWorkshopStatusEnum()
                && getWorkshopEnum() == that.getWorkshopEnum()
                && Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getComments(), getWorkshopStatusEnum(), getWorkshopEnum(), getUser());
    }
}
