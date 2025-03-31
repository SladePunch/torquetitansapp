package com.torquetitans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mechanics")
public class Mechanic {
    @Id
    @Column(name = "MECHANIC_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mechanics_seq")
    @SequenceGenerator(name = "mechanics_seq", sequenceName = "mechanics_seq", allocationSize = 1)
    private int mechanicId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE")
    private String phone;

    // Getters và Setters
    public int getMechanicId() { return mechanicId; }
    public void setMechanicId(int mechanicId) { this.mechanicId = mechanicId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}