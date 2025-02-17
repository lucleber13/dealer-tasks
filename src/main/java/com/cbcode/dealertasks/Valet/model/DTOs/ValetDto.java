package com.cbcode.dealertasks.Valet.model.DTOs;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Valet.model.Enums.ValetEnum;
import com.cbcode.dealertasks.Valet.model.Enums.ValetStatus;
import jakarta.persistence.*;

import java.util.Objects;

public class ValetDto {
    private Long id;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ValetStatus status = ValetStatus.PENDING;

    @Column(name = "valeter_enum")
    @Enumerated(EnumType.STRING)
    private ValetEnum valetEnum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;

    public ValetDto() {
    }

    public ValetDto(String comments, ValetStatus status, ValetEnum valetEnum) {
        this.comments = comments;
        this.status = status;
        this.valetEnum = valetEnum;
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

    public ValetStatus getStatus() {
        return status;
    }

    public void setStatus(ValetStatus status) {
        this.status = status;
    }

    public ValetEnum getValetEnum() {
        return valetEnum;
    }

    public void setValetEnum(ValetEnum valetEnum) {
        this.valetEnum = valetEnum;
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
        ValetDto valetDto = (ValetDto) o;
        return Objects.equals(getId(), valetDto.getId())
                && Objects.equals(getComments(), valetDto.getComments())
                && getStatus() == valetDto.getStatus()
                && getValetEnum() == valetDto.getValetEnum()
                && Objects.equals(getUser(), valetDto.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getComments(), getStatus(), getValetEnum(), getUser());
    }
}
