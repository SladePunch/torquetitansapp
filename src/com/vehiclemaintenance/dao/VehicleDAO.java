package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Customer;
import com.vehiclemaintenance.entity.Vehicle;
import com.vehiclemaintenance.HibernateUtil;
import oracle.jdbc.OracleTypes;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public List<Vehicle> getVehiclesByOwner(Long ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageVehicles.Manage_Vehicle(?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, "READ_BY_OWNER");
            stmt.setNull(2, java.sql.Types.NUMERIC);
            stmt.setNull(3, java.sql.Types.VARCHAR);
            stmt.setNull(4, java.sql.Types.VARCHAR);
            stmt.setNull(5, java.sql.Types.NUMERIC);
            stmt.setNull(6, java.sql.Types.VARCHAR);
            stmt.setLong(7, ownerId);
            stmt.registerOutParameter(8, OracleTypes.CURSOR);
            stmt.execute();
            ResultSet rs = (ResultSet) stmt.getObject(8);
            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleId(rs.getLong("VehicleID"));
                vehicle.setMake(rs.getString("Make"));
                vehicle.setModel(rs.getString("Model"));
                vehicle.setYear(rs.getInt("Year"));
                vehicle.setLicensePlate(rs.getString("LicensePlate"));
                Customer owner = session.get(Customer.class, rs.getLong("OwnerID"));
                vehicle.setOwner(owner);
                vehicles.add(vehicle);
            }
            rs.close();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch vehicles by owner", e);
        }
        return vehicles;
    }
    
    public void addVehicle(Vehicle vehicle) {
        // Validate required fields
        if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("License Plate is required.");
        }
        if (vehicle.getOwner() == null) {
            throw new IllegalArgumentException("Owner is required.");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(vehicle);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error adding vehicle via Hibernate: " + e);
            e.printStackTrace();
            throw new RuntimeException("Failed to add vehicle", e);
        }
    }
    
    // For other CRUD operations, you might need to add a Manage_Vehicle procedure with UPDATE and DELETE actions
    public void updateVehicle(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(vehicle);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating vehicle via Hibernate: " + e);
            e.printStackTrace();
            throw new RuntimeException("Failed to update vehicle", e);
        }
    }

    public void deleteVehicle(Long vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Vehicle vehicle = session.get(Vehicle.class, vehicleId);
            if (vehicle != null) {
                session.delete(vehicle);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete vehicle", e);
        }
    }

    public Vehicle getVehicleById(Long vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Vehicle.class, vehicleId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch vehicle", e);
        }
    }
    
    public List<Vehicle> getVehiclesByCustomer(Customer customer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery(
                "FROM Vehicle v JOIN FETCH v.owner WHERE v.owner = :customer", 
                Vehicle.class
            );
            query.setParameter("customer", customer);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error retrieving vehicles via Hibernate: " + e);
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve vehicles", e);
        }
    }
}