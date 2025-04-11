package com.vehiclemaintenance.dao;

import com.vehiclemaintenance.entity.Mechanic;
import com.vehiclemaintenance.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

public class MechanicDAO {

    public void addMechanic(Mechanic mechanic) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageMechanic(?, ?, ?, ?)}");
            stmt.setString(1, "CREATE");
            stmt.setNull(2, java.sql.Types.NUMERIC);
            stmt.setString(3, mechanic.getName());
            stmt.setString(4, mechanic.getContactInfo());
            stmt.execute();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to add mechanic", e);
        }
    }

    public void updateMechanic(Mechanic mechanic) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageMechanic(?, ?, ?, ?)}");
            stmt.setString(1, "UPDATE");
            stmt.setLong(2, mechanic.getMechanicId());
            stmt.setString(3, mechanic.getName());
            stmt.setString(4, mechanic.getContactInfo());
            stmt.execute();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update mechanic", e);
        }
    }

    public void deleteMechanic(Long mechanicId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Connection connection = session.doReturningWork(conn -> conn);
            CallableStatement stmt = connection.prepareCall("{call ManageMechanic(?, ?, ?, ?)}");
            stmt.setString(1, "DELETE");
            stmt.setLong(2, mechanicId);
            stmt.setNull(3, java.sql.Types.VARCHAR);
            stmt.setNull(4, java.sql.Types.VARCHAR);
            stmt.execute();
            stmt.close();
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete mechanic", e);
        }
    }

    public Mechanic getMechanicById(Long mechanicId) {
        System.out.println("Attempting to fetch Mechanic for MechanicID: " + mechanicId);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Mechanic mechanic = session.get(Mechanic.class, mechanicId);
            if (mechanic == null) {
                System.out.println("No mechanic found for MechanicID: " + mechanicId);
            } else {
                System.out.println("Fetched mechanic: MechanicID=" + mechanic.getMechanicId() + 
                                  ", Name=" + mechanic.getName());
            }
            return mechanic;
        } catch (Exception e) {
            System.err.println("Error fetching mechanic for MechanicID: " + mechanicId);
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch mechanic by ID: " + mechanicId, e);
        }
    }

    public List<Mechanic> getAllMechanics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Mechanic> query = session.createQuery("FROM Mechanic", Mechanic.class);
            query.setCacheable(false); // Disable caching to ensure fresh data
            List<Mechanic> mechanics = query.list();
            System.out.println("MechanicDAO.getAllMechanics returned: " + mechanics.size() + " mechanics");
            if (!mechanics.isEmpty()) {
                mechanics.forEach(mechanic -> System.out.println("Fetched Mechanic: ID=" + mechanic.getMechanicId() + ", Name=" + mechanic.getName()));
            }
            return mechanics;
        } catch (Exception e) {
            System.err.println("Error retrieving mechanics via Hibernate: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve mechanics", e);
        }
    }	
}