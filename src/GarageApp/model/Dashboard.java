package GarageApp.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dashboard")
public class Dashboard {
    @Id
    @Column(name = "ActivityID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_seq")
    @SequenceGenerator(name = "dashboard_seq", sequenceName = "dashboard_seq", allocationSize = 1)
    private int activityId;

    @Column(name = "ActivityType")
    private String activityType;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @Column(name = "Description")
    private String description;

    @Column(name = "Status")
    private String status;

    @Column(name = "ActivityDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activityDate;

    // Getters and Setters
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getActivityDate() { return activityDate; }
    public void setActivityDate(Date activityDate) { this.activityDate = activityDate; }
}