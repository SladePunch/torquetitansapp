package com.vehiclemaintenance.entity;

import javax.persistence.*;

@Entity
@Table(name = "Mechanic")
public class Mechanic {
    @Id
    @Column(name = "MechanicID")
    private Long mechanicId;

    @Column(name = "Name")
    private String name;

    @Column(name = "ContactInfo")
    private String contactInfo;

    // Constructors
    public Mechanic() {}

    // Getters and Setters
    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}