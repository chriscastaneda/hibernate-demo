package com.revature;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class Launcher {
	
	static SessionFactory sf; //reference for easy access
	static Logger logger = Logger.getRootLogger(); //logger instance
	
	
	/*Loads Hibernate config, defines models(Entities), returns configured session factory*/
	static SessionFactory configureHibernate() {
		String jdbcUrl = String.format("jdbc:postgresql://%s:5432/springboot", System.getenv("SPRING_DEMO_URL")); //convert to jdbcUrl string
		
		logger.info(jdbcUrl);
		
		Configuration configuration = new Configuration() //Creates object
				.configure() //Load config's from xml
				.setProperty("hibernate.connection.username", System.getenv("SPRING_DEMO_ROLE"))
				.setProperty("hibernate.connection.url", jdbcUrl)
				.setProperty("hibernate.connection.password", System.getenv("SPRING_DEMO_PASSWORD"));
		
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder() //Interface
				.applySettings(configuration.getProperties()).build(); //service registry 
		
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
		
	}
	
	public static void main(String[] args) {
		sf = configureHibernate(); //startup connection pool
		sf.close(); //close connection pool
		//1:20:55
	}

}
