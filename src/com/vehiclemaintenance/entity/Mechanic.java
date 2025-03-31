package com.vehiclemaintenance.entity;

import javax.persistence.*;

@Entity
@Table(name = "Mechanic")
public class Mechanic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mechanic_seq")
    @SequenceGenerator(name = "mechanic_seq", sequenceName = "Mechanic_SEQ", allocationSize = 1)
    private Long mechanicId;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "ContactInfo")
    private String contactInfo;

    public Mechanic() {}

    public Long getMechanicId() { return mechanicId; }
    public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}