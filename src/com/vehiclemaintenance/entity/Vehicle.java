package com.vehiclemaintenance.entity;

import javax.persistence.*;

@Entity
@Table(name = "Vehicle")
public class Vehicle {
    @Id
    @Column(name = "VehicleID")
    private Long vehicleId;

    @Column(name = "Make")
    private String make;

    @Column(name = "Model")
    private String model;

    @Column(name = "Year")
    private int year;

    @Column(name = "LicensePlate")
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "OwnerID")
    private Customer owner;

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }
}