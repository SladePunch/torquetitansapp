package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MechanicDAO {
    public List<Mechanic> getAllMechanics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Mechanic> query = session.createQuery("FROM Mechanic", Mechanic.class);
            return query.list();
        }
    }
}