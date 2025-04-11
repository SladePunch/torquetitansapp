package com.vehiclemaintenance.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SERVICERECORD")
public class ServiceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servicerecord_seq")
    @SequenceGenerator(name = "servicerecord_seq", sequenceName = "SERVICERECORD_SEQ", allocationSize = 1)
    @Column(name = "SERVICEID")
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "VEHICLEID")
    private Vehicle vehicle;

    @Column(name = "SERVICEDATE")
    private Date serviceDate;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "ASSIGNEDMECHANICID")
    private Mechanic mechanic;

    @Column(name = "STATUS")
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