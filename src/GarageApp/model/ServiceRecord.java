package GarageApp.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SERVICERECORD")
public class ServiceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servicerecord_seq")
    @SequenceGenerator(name = "servicerecord_seq", sequenceName = "servicerecord_seq", allocationSize = 1)
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "VehicleID", nullable = false)
    private Vehicle vehicle;

    @Column(name = "ServiceDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date serviceDate;

    @Column(name = "Description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "MechanicID", nullable = false)
    private Mechanic mechanic;

    @Column(name = "Status", nullable = false)
    private String status;

    // Getters and setters
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