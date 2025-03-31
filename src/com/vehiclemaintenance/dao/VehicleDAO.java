package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Vehicle;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class VehicleDAO {
    public void createVehicle(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(vehicle);
            session.getTransaction().commit();
        }
    }

    public Vehicle getVehicle(Long vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Vehicle.class, vehicleId);
        }
    }

    public List<Vehicle> getAllVehicles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery("FROM Vehicle", Vehicle.class);
            return query.list();
        }
    }

    public List<Vehicle> searchVehiclesByCustomerName(String customerName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery(
                "FROM Vehicle v WHERE v.owner.name LIKE :name", Vehicle.class);
            query.setParameter("name", "%" + customerName + "%");
            return query.list();
        }
    }

    public void updateVehicle(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(vehicle);
            session.getTransaction().commit();
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
        }
    }
}