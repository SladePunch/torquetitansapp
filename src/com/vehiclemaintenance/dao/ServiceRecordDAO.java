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
            query.setMaxResults(4); // Recent 4 services
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
}