package GarageApp.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "maintenanceschedule")
public class MaintenanceSchedule {
    @Id
    @Column(name = "ScheduleID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_seq")
    @SequenceGenerator(name = "schedule_seq", sequenceName = "schedule_seq", allocationSize = 1)
    private int scheduleId;

    @ManyToOne
    @JoinColumn(name = "VehicleID")
    private Vehicle vehicle;

    @Column(name = "ScheduledDate")
    @Temporal(TemporalType.DATE)
    private Date scheduledDate;

    @Column(name = "ServiceType")
    private String serviceType;

    // Getters and Setters
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
}