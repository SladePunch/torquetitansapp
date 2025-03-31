package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDAO {
    public Users authenticate(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Users> query = session.createQuery(
                "FROM Users u WHERE u.username = :username AND u.password = :password", Users.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        }
    }
}