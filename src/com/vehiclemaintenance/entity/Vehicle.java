package com.vehiclemaintenance.entity;

import javax.persistence.*;

@Entity
@Table(name = "Vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_seq")
    @SequenceGenerator(name = "vehicle_seq", sequenceName = "Vehicle_SEQ", allocationSize = 1)
    private Long vehicleId;

    @Column(name = "Make", nullable = false)
    private String make;

    @Column(name = "Model", nullable = false)
    private String model;

    @Column(name = "Year", nullable = false)
    private Integer year;

    @Column(name = "LicensePlate", nullable = false, unique = true)
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "OwnerID", nullable = false)
    private Customers owner;

    public Vehicle() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public Customers getOwner() { return owner; }
    public void setOwner(Customers owner) { this.owner = owner; }
}