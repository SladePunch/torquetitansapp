package com.vehiclemaintenance.entity;

import javax.persistence.*;

@Entity
@Table(name = "USERMECHANICMAPPING")
public class UserMechanicMapping {
    @Id
    @Column(name = "USERID")
    private Long userId;

    @Column(name = "MECHANICID")
    private Long mechanicId;

    // Constructors
    public UserMechanicMapping() {}

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
}