package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Customers;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CustomerDAO {
    public List<Customers> getAllCustomers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customers> query = session.createQuery("FROM Customers", Customers.class);
            return query.list();
        }
    }
}