package GarageApp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MECHANIC")
public class Mechanic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mechanic_seq")
    @SequenceGenerator(name = "mechanic_seq", sequenceName = "mechanic_seq", allocationSize = 1)
    private Long mechanicID;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "ContactInfo")
    private String contactInfo;

    // Getters and setters
    public Long getMechanicID() {
        return mechanicID;
    }

    public void setMechanicID(Long mechanicID) {
        this.mechanicID = mechanicID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}