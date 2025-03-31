package com.torquetitans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @Column(name = "JOB_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne
    @JoinColumn(name = "MECHANIC_ID") // Link to the mechanics table
    private Mechanic mechanic;

    @ManyToOne
    @JoinColumn(name = "VEHICLE_ID") // Link to the vehicles table
    private Vehicle vehicle;

    // Getters and Setters
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Mechanic getMechanic() { return mechanic; }
    public void setMechanic(Mechanic mechanic) { this.mechanic = mechanic; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}