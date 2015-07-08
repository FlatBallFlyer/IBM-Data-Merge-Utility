package com.ibm.util.merge.persistence;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.template.Template;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

/**
 *
 */
public class HibernatePersistence {
    private Logger log = Logger.getLogger(HibernatePersistence.class);
    private final String FULLNAME_QUERY = "SELECT ID_TEMPLATE " +
            "FROM TEMPLATE " +
            "WHERE TEMPLATE.COLLECTION = :collection " +
            "AND TEMPLATE.NAME = :name" +
            "AND TEMPLATE.COLUMN = :column";
    private final String DEFAULT_QUERY = "SELECT ID_TEMPLATE" +
            "FROM TEMPLATE " +
            "WHERE TEMPLATE.COLLECTION = :collection " +
            "AND TEMPLATE.NAME = :name" +
            "AND TEMPLATE.COLUMN = ''";
    private SessionFactory sessionFactory;

    public HibernatePersistence(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    //    private ServiceRegistry serviceRegistry;

    public Template getTemplateDefault(String fullName) {
        Session session = sessionFactory.openSession();
        List list = session.createQuery(DEFAULT_QUERY).list();
        session.getTransaction().commit();
        session.close();
        if (list.size() == 1) {
            log.info("Linked Template: " + fullName);
            Template newTemplate = (Template) list.get(0);
            return newTemplate;
        }
        throw new IllegalStateException("Could not retrieve " + fullName + " from database");
    }

    public Template getTemplateFullname(String fullName) {
        Session session = sessionFactory.openSession();
        List list = session.createQuery(FULLNAME_QUERY).list();
        session.getTransaction().commit();
        session.close();
        if (list.size() == 1) {
            Template newTemplate = (Template) list.get(0);
            log.info("Constructed Template: " + fullName);
            return newTemplate;
        }
        throw new IllegalStateException("Could not retrieve " + fullName + " from database");
    }

    /**********************************************************************************
     * save provided template to the Template database
     *
     * @param Template template the Template to save
     * @return a cloned copy of the Template ready for Merge Processing
     * @throws MergeException on Template Clone Errors
     */
    public Template saveTemplateToDatabase(Template template) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(template); // (Where FullName=FullName)
        session.save(template);
        session.getTransaction().commit();
        session.close();
        log.info("Template Saved: " + template.getFullName());
        return template;
    }

    /**********************************************************************************
     * Initialize the Hibernate Connection
     */
    public void initilizeHibernate() {
        constructSessionFactory();
        log.info("Hibernate Initilized");
    }

    public static SessionFactory constructSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public void deleteTemplate(Template template) {
        throw new UnsupportedOperationException("implement delete in hibernate");
    }
}
