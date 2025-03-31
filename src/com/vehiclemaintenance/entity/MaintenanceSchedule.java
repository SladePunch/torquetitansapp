package com.vehiclemaintenance.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MaintenanceSchedule")
public class MaintenanceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "maintenance_schedule_seq")
    @SequenceGenerator(name = "maintenance_schedule_seq", sequenceName = "MaintenanceSchedule_SEQ", allocationSize = 1)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "VehicleID", nullable = false)
    private Vehicle vehicle;

    @Column(name = "ScheduledDate", nullable = false)
    private Date scheduledDate;

    @Column(name = "ServiceType", nullable = false)
    private String serviceType;

    public MaintenanceSchedule() {}

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
}