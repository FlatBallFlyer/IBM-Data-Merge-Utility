package com.ibm.util.merge.persistence;

import com.ibm.util.merge.MergeException;
import com.ibm.util.merge.cache.TemplateCache;
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
public class HibernatePersistence extends AbstractPersistence {
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

    @Override
	public List<Template> loadAllTemplates() {
		// templateCache.clear();
		// TODO Load all templates from database
        throw new UnsupportedOperationException("implement load all templates from DB");
	}


    /**********************************************************************************
     * save provided template to the Template database
     *
     * @param Template template the Template to save
     * @return a cloned copy of the Template ready for Merge Processing
     * @throws MergeException on Template Clone Errors
     */
	@Override
	public void saveTemplate(Template template) {
		// TODO Delete an existing template and save the new one
        throw new UnsupportedOperationException("implement save template from DB");
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        session.delete(template); // (Where FullName=FullName)
//        session.save(template);
//        session.getTransaction().commit();
//        session.close();
//        log.info("Template Saved: " + template.getFullName());
//        return;
	}

	@Override
	public void deleteTemplate(Template template) {
		// TODO Delete the template if it exists, log errors, do not throw errors
        throw new UnsupportedOperationException("implement delete in DB");
    }
    

//  public HibernatePersistence(SessionFactory sessionFactory) {
//      this.sessionFactory = sessionFactory;
//  }
//
//  public Template getTemplateDefault(String fullName) {
//      Session session = sessionFactory.openSession();
//      List list = session.createQuery(DEFAULT_QUERY).list();
//      session.getTransaction().commit();
//      session.close();
//      if (list.size() == 1) {
//          log.info("Linked Template: " + fullName);
//          Template newTemplate = (Template) list.get(0);
//          return newTemplate;
//      }
//      throw new IllegalStateException("Could not retrieve " + fullName + " from database");
//  }
//
//  public Template getTemplateFullname(String fullName) {
//      Session session = sessionFactory.openSession();
//      List list = session.createQuery(FULLNAME_QUERY).list();
//      session.getTransaction().commit();
//      session.close();
//      if (list.size() == 1) {
//          Template newTemplate = (Template) list.get(0);
//          log.info("Constructed Template: " + fullName);
//          return newTemplate;
//      }
//      throw new IllegalStateException("Could not retrieve " + fullName + " from database");
//  }

//  /**********************************************************************************
//   * Initialize the Hibernate Connection
//   */
//  public void initilizeHibernate() {
//      constructSessionFactory();
//      log.info("Hibernate Initilized");
//  }
//
//  public static SessionFactory constructSessionFactory() {
//      Configuration conf = new Configuration();
//      conf.configure();
//      StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder().applySettings(conf.getProperties());
//      ServiceRegistry serviceRegistry = registryBuilder.build();
//      return conf.buildSessionFactory(serviceRegistry);
//  }

}
