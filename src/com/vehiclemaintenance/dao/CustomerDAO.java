package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Customer;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CustomerDAO {
    public List<Customer> getAllCustomers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer", Customer.class);
            return query.list();
        }
    }

    public void addCustomer(Customer customer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(customer);
            session.getTransaction().commit();
        }
    }

    public void updateCustomer(Customer customer) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(customer);
            session.getTransaction().commit();
        }
    }

    public void deleteCustomer(Long customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, customerId);
            if (customer != null) {
                session.delete(customer);
            }
            session.getTransaction().commit();
        }
    }
}