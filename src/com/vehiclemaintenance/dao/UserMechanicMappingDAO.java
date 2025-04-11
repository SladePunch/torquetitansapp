package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserMechanicMappingDAO {
    public Long getMechanicIdByUserId(Long userId) {
        System.out.println("Attempting to fetch MechanicID for UserID: " + userId);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT um.mechanicId FROM UserMechanicMapping um WHERE um.userId = :userId", Long.class
            );
            query.setParameter("userId", userId);
            Long mechanicId = query.uniqueResult();
            if (mechanicId == null) {
                System.out.println("No mapping found for UserID: " + userId);
            } else {
                System.out.println("Fetched MechanicID: " + mechanicId + " for UserID: " + userId);
            }
            return mechanicId;
        } catch (Exception e) {
            System.err.println("Error fetching MechanicID for UserID: " + userId);
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch MechanicID for UserID: " + userId, e);
        }
    }
}