package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.ServiceRecord;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ServiceRecordDAO {
    public List<ServiceRecord> getRecentServices() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceRecord> query = session.createQuery(
                "FROM ServiceRecord sr ORDER BY sr.serviceDate DESC", ServiceRecord.class);
            query.setMaxResults(4);
            return query.list();
        }
    }

    public List<ServiceRecord> getAssignedTasks(Long mechanicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceRecord> query = session.createQuery(
                "FROM ServiceRecord sr WHERE sr.mechanic.mechanicId = :mechanicId", ServiceRecord.class);
            query.setParameter("mechanicId", mechanicId);
            return query.list();
        }
    }

    public void updateServiceRecord(ServiceRecord serviceRecord) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(serviceRecord);
            session.getTransaction().commit();
        }
    }

    public void addServiceRecord(ServiceRecord serviceRecord) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(serviceRecord);
            session.getTransaction().commit();
        }
    }

    public List<ServiceRecord> getServiceRecordsByVehicle(Long vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ServiceRecord> query = session.createQuery(
                "FROM ServiceRecord sr WHERE sr.vehicle.vehicleId = :vehicleId", ServiceRecord.class);
            query.setParameter("vehicleId", vehicleId);
            return query.list();
        }
    }
}