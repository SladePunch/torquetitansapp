package GarageApp.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "ReportID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq")
    @SequenceGenerator(name = "report_seq", sequenceName = "report_seq", allocationSize = 1)
    private int reportId;

    @ManyToOne
    @JoinColumn(name = "VehicleID")
    private Vehicle vehicle;

    @Column(name = "ReportType")
    private String reportType;

    @Column(name = "GeneratedDate")
    @Temporal(TemporalType.DATE)
    private Date generatedDate;

    @Column(name = "SearchableIndex")
    private String searchableIndex;

    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public Date getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Date generatedDate) { this.generatedDate = generatedDate; }
    public String getSearchableIndex() { return searchableIndex; }
    public void setSearchableIndex(String searchableIndex) { this.searchableIndex = searchableIndex; }
}