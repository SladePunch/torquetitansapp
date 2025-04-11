package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.entity.Vehicle;
import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.HibernateUtil;
import oracle.jdbc.OracleTypes;
import org.hibernate.Session;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {

	public void addServiceRecord(ServiceRecord serviceRecord) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        session.beginTransaction();
	        Connection connection = session.doReturningWork(conn -> conn);
	        CallableStatement stmt = connection.prepareCall("{call ManageServiceRecord(?, ?, ?, ?, ?, ?, ?)}");
	        
	        System.out.println("Adding ServiceRecord: " +
	            "VehicleID=" + serviceRecord.getVehicle().getVehicleId() +
	            ", ServiceDate=" + serviceRecord.getServiceDate() +
	            ", Description=" + serviceRecord.getDescription() +
	            ", MechanicID=" + serviceRecord.getMechanic().getMechanicId() +
	            ", Status=" + serviceRecord.getStatus());

	        stmt.setString(1, "CREATE");
	        stmt.setNull(2, java.sql.Types.NUMERIC);
	        stmt.setLong(3, serviceRecord.getVehicle().getVehicleId());
	        stmt.setDate(4, new Date(serviceRecord.getServiceDate().getTime()));
	        stmt.setString(5, serviceRecord.getDescription());
	        stmt.setLong(6, serviceRecord.getMechanic().getMechanicId());
	        stmt.setString(7, serviceRecord.getStatus());
	        stmt.execute();
	        stmt.close();
	        session.getTransaction().commit();
	        System.out.println("Service record added successfully: " + serviceRecord.getDescription());
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to add service record", e);
	    }
	}

    public void updateServiceRecord(ServiceRecord serviceRecord) {
        System.out.println("Updating ServiceRecord: ServiceID=" + serviceRecord.getServiceId() + 
                          ", Status=" + serviceRecord.getStatus() + 
                          ", VehicleID=" + serviceRecord.getVehicle().getVehicleId() + 
                          ", MechanicID=" + serviceRecord.getMechanic().getMechanicId() + 
                          ", Description=" + serviceRecord.getDescription() + 
                          ", ServiceDate=" + serviceRecord.getServiceDate());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageServiceRecord(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, "UPDATE");
            stmt.setLong(2, serviceRecord.getServiceId());
            stmt.setLong(3, serviceRecord.getVehicle().getVehicleId());
            stmt.setDate(4, new Date(serviceRecord.getServiceDate().getTime()));
            stmt.setString(5, serviceRecord.getDescription());
            stmt.setLong(6, serviceRecord.getMechanic().getMechanicId());
            stmt.setString(7, serviceRecord.getStatus());
            stmt.execute();
            System.out.println("Stored procedure ManageServiceRecord executed for ServiceID=" + serviceRecord.getServiceId());
            stmt.close();
            session.getTransaction().commit();
            System.out.println("Transaction committed for ServiceID=" + serviceRecord.getServiceId());
        } catch (Exception e) {
            System.err.println("Failed to update ServiceRecord: ServiceID=" + serviceRecord.getServiceId());
            e.printStackTrace();
            throw new RuntimeException("Failed to update service record", e);
        }
    }

    public void deleteServiceRecord(Long serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageServiceRecord(?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, "DELETE");
            stmt.setLong(2, serviceId);
            stmt.setNull(3, java.sql.Types.NUMERIC);
            stmt.setNull(4, java.sql.Types.DATE);
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.setNull(6, java.sql.Types.NUMERIC);
            stmt.setNull(7, java.sql.Types.VARCHAR);
            stmt.execute();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete service record", e);
        }
    }

    public List<ServiceRecord> getRecentServices() {
        List<ServiceRecord> serviceRecords = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ServiceRecord_Pkg.Get_Recent_Services(?, ?)}");
            stmt.setInt(1, 10);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                ServiceRecord sr = new ServiceRecord();
                sr.setServiceId(rs.getLong("ServiceID"));
                sr.setServiceDate(rs.getDate("ServiceDate"));
                sr.setDescription(rs.getString("Description"));
                sr.setStatus(rs.getString("Status"));

                Vehicle vehicle = session.createQuery(
                    "SELECT v FROM Vehicle v LEFT JOIN FETCH v.owner WHERE v.vehicleId = :vehicleId",
                    Vehicle.class
                ).setParameter("vehicleId", rs.getLong("VehicleID")).getSingleResult();
                sr.setVehicle(vehicle);

                Mechanic mechanic = session.get(Mechanic.class, rs.getLong("MechanicID"));
                sr.setMechanic(mechanic);

                serviceRecords.add(sr);
            }
            rs.close();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch recent services", e);
        }
        return serviceRecords;
    }

    public List<ServiceRecord> getAssignedTasks(Long mechanicId) {
        List<ServiceRecord> tasks = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ServiceRecord_Pkg.Get_Assigned_Tasks(?, ?)}");
            stmt.setLong(1, mechanicId);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                ServiceRecord sr = new ServiceRecord();
                sr.setServiceId(rs.getLong("ServiceID"));
                sr.setServiceDate(rs.getDate("ServiceDate"));
                sr.setDescription(rs.getString("Description"));
                sr.setStatus(rs.getString("Status"));

                Vehicle vehicle = session.createQuery(
                    "SELECT v FROM Vehicle v LEFT JOIN FETCH v.owner WHERE v.vehicleId = :vehicleId",
                    Vehicle.class
                ).setParameter("vehicleId", rs.getLong("VehicleID")).getSingleResult();
                sr.setVehicle(vehicle);

                Mechanic mechanic = session.get(Mechanic.class, rs.getLong("MechanicID"));
                sr.setMechanic(mechanic);

                tasks.add(sr);
            }
            rs.close();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch assigned tasks", e);
        }
        return tasks;
    }

    public List<ServiceRecord> getServiceRecordsByVehicle(Long vehicleId) {
        List<ServiceRecord> serviceRecords = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ServiceRecord_Pkg.Get_Service_Records_By_Vehicle(?, ?)}");
            stmt.setLong(1, vehicleId);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(2);
            while (rs.next()) {
                ServiceRecord sr = new ServiceRecord();
                sr.setServiceId(rs.getLong("ServiceID"));
                sr.setServiceDate(rs.getDate("ServiceDate"));
                sr.setDescription(rs.getString("Description"));
                sr.setStatus(rs.getString("Status"));

                Vehicle vehicle = session.createQuery(
                    "SELECT v FROM Vehicle v LEFT JOIN FETCH v.owner WHERE v.vehicleId = :vehicleId",
                    Vehicle.class
                ).setParameter("vehicleId", rs.getLong("VehicleID")).getSingleResult();
                sr.setVehicle(vehicle);

                Mechanic mechanic = session.get(Mechanic.class, rs.getLong("MechanicID"));
                sr.setMechanic(mechanic);

                serviceRecords.add(sr);
            }
            rs.close();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch service records by vehicle", e);
        }
        return serviceRecords;
    }
}