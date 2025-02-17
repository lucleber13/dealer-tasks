package com.cbcode.dealertasks.Valet.model;

import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Valet.model.Enums.ValetEnum;
import com.cbcode.dealertasks.Valet.model.Enums.ValetStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "valets")
@SequenceGenerator(name = "valets_seq", sequenceName = "valets_seq", allocationSize = 1, initialValue = 1)
public class Valet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "valets_seq")
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
    private User user;

    public Valet() {
    }

    public Valet(String comments, ValetStatus status, ValetEnum valetEnum) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Valet valet = (Valet) o;
        return Objects.equals(getId(), valet.getId())
                && Objects.equals(getComments(), valet.getComments())
                && getStatus() == valet.getStatus()
                && getValetEnum() == valet.getValetEnum()
                && Objects.equals(getUser(), valet.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getComments(), getStatus(), getValetEnum(), getUser());
    }
}
