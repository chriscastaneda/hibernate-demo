package com.revature;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.revature.daos.BearDao;
import com.revature.daos.CaveDao;
import com.revature.models.Bear;
import com.revature.models.Cave;

public class Launcher {
	
	private static SessionFactory sf; //reference for easy access
	
	private static BearDao bearDao;
	private static CaveDao caveDao;
	
	
	public static SessionFactory getSessionFactory() {
		return sf;
	};
	
	static Logger logger = Logger.getRootLogger(); //logger instance
	
	
	/*Loads Hibernate config, defines models(Entities), returns configured session factory*/
	static SessionFactory configureHibernate() {
		String jdbcUrl = String.format("jdbc:postgresql://%s:5432/springboot", System.getenv("SPRING_DEMO_URL")); //create  jdbcUrl url from db url to string
		
		Configuration configuration = new Configuration() //Creates config object instance
				.configure() //Load config's from xml
				.setProperty("hibernate.connection.username", System.getenv("SPRING_DEMO_ROLE"))
				.setProperty("hibernate.connection.url", jdbcUrl)
				.setProperty("hibernate.connection.password", System.getenv("SPRING_DEMO_PASSWORD"))
				.addAnnotatedClass(Bear.class) //Check bear class annotation to manage persistances
				.addAnnotatedClass(Cave.class); //Check bear class annotation to manage persistances
		
		
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder() //Interface to use config to create Service Registry
				.applySettings(configuration.getProperties()).build(); //service registry 
		
		//Use config to create sessionFactory, pass in service registry
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
		
	}
	
	
	
	//Bear CRUD Demo
	static void runCrudDemo() {	//To help closeSession exceptions
		BearDao bearDao = new BearDao();
		
		//Bear bear = new Bear(0, "female", "Panda", LocalDate.of(2020, 6, 15), 20.0);//always set new object Model id to 0
		//insertBear(bear); //PATCH
				
		Optional <Bear> bear = bearDao.selectBear(2); //GetbyId
		if(bear.isPresent()){ //Optional handling
			System.out.println(bear);
		} else {
			System.out.println("No bear with the provided id");
		}

		System.out.println("Loading bear lazily");
		Bear loadedBear = bearDao.loadBear(1);
		System.out.println(loadedBear);
				
		//Test UPDATE with lazy load 
		LocalDate birthDate = loadedBear.getDateOfBirth();
		LocalDate modifiedDate = birthDate.minus(1, ChronoUnit.DAYS);
		loadedBear.setDateOfBirth(modifiedDate);
		bearDao.updateBear(loadedBear);
				
		Bear brandNewBear = new Bear(0, "male", "Grizzly Bear", LocalDate.now(), 100.0);
		bearDao.insertBear(brandNewBear);
		bearDao.deleteBear(brandNewBear);
			
	}
	
	//Cave Demo
	public static void relationalDemo() {
		BearDao bearDao = new BearDao();
		CaveDao caveDao = new CaveDao();
		
//		Cave cave = new Cave(0, "Bear Cave", "Underground", 500);
//		caveDao.insertCave(cave); //Call PUT in dao's
//		Bear bear = bearDao.loadBear(1); //GET
//		caveDao.addBear(cave, bear); //UPDATE
		
		//Retrieve bears
//		Optional <Cave> cave = caveDao.getCave(4);
//		logger.warn(cave.get());
		
		//Retrieve bear by cave id
//		List<Bear> bears = bearDao.getBearsByCaveId(4); //cave_id = 4
//		System.out.println(bears);
		
		//Retrieve bear by cave id
//		Bear bear = bearDao.selectBear(1).get();
//		logger.warn(bear);
//		logger.warn(bear.getCave());
		
		//Create bears for Cascade
//		List<Bear> bears = new ArrayList<>();
//		bears.add(new Bear(0, "female", "Panda", LocalDate.now(), 1000));
//		bears.add(new Bear(0, "female", "Brown Bear", LocalDate.now(), 1000));
//		
//		//Cascade operations to bear list in cave
//		Cave cave = new Cave(0, "Luminous Cave", "Canada", 10000, bears);
//		caveDao.insertCave(cave);
		
		//Assign cubs to Parent Bears
		Bear bear = bearDao.loadBear(1); //load bears
		Bear cubA = bearDao.loadBear(2);
		Bear cubB = bearDao.loadBear(3);
		
		bearDao.addCubs(bear,  cubA, cubB);
		
		bearDao.updateBear(bear);
	}
	

	public static void main(String[] args) {
		sf = configureHibernate(); //startup connection pool
		
		try {
			relationalDemo();
			//runCrudDemo(); //run operations in function
		}catch(PersistenceException e) {
			e.printStackTrace(); //log for testing
			sf.close(); //close connection pool
		}
			
		
	}
}
	

//1:03:47
	
	
	
