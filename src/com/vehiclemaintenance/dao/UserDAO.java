package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Users;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDAO {
    public Users validateUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Users> query = session.createQuery(
                "FROM Users WHERE username = :username AND password = :password", Users.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            Users user = query.uniqueResult();
            if (user == null) {
                System.err.println("No user found for username: " + username);
            } else if (user.getRoleId() == null) {
                System.err.println("RoleID is null for user: " + username);
            }
            return user;
        } catch (Exception e) {
            System.err.println("Error validating user: " + username);
            e.printStackTrace();
            return null;
        }
    }
}