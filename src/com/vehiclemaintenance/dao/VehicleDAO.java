package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Vehicle;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;

public class VehicleDAO {
    public void addVehicle(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(vehicle);
            session.getTransaction().commit();
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