package com.vehiclemaintenance.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "ServiceRecord")
public class ServiceRecord {
    @Id
    @Column(name = "ServiceID")
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "VehicleID")
    private Vehicle vehicle;

    @Column(name = "ServiceDate")
    private Date serviceDate;

    @Column(name = "Description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "MechanicID")
    private Mechanic mechanic;

    @Column(name = "Status")
    private String status;

    // Getters and Setters
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}