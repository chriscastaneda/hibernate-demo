package com.revature.daos;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.revature.Launcher;
import com.revature.models.Bear;
import com.revature.models.Cave;

public class CaveDao {
	
	SessionFactory sf; //reference to SessionFactory

	Logger logger = Logger.getRootLogger();
	
	//Retrieve SessionFactory
	public CaveDao() { 
		super();
		sf = Launcher.getSessionFactory();
	}
	
	//PUT
	public void insertCave(Cave cave) {
		try(Session session = sf.openSession()){
			Transaction tx = session.beginTransaction(); //used in certain crud's
			session.persist(cave);
			tx.commit();
		}
	}
	
	//UPDATE: add bear to list of bears
	public void addBear(Cave cave, Bear bear) {
		if(cave.getBears() == null) {
			cave.setBears(new ArrayList<Bear>());
		}
		if(cave.getBears().contains(bear)) { //logic check:   object already exsist's
			logger.warn("Bear already in cave!");
			return;
		}
		try(Session session = sf.openSession()){
			Transaction tx = session.beginTransaction();
			cave.getBears().add(bear);
			cave = (Cave) session.merge(cave);
			tx.commit();
		}
	}
	
	//GET
	public Optional<Cave> getCave(int id) {
		try(Session session = sf.openSession()){
			Cave cave = session.get(Cave.class, id);
			
			//Initialize bears in cave
			if(cave == null) return Optional.empty();
			Hibernate.initialize(cave.getBears());
			return Optional.of(cave);
			
//			return Optional.ofNullable(session.get(Cave.class, id)); //return cave by id
		}
	}
	
	//UPDATE
	public Cave updateCave(Cave cave) {
		try(Session session = sf.openSession()){
			//PASS
			Transaction tx = session.beginTransaction(); //used in certian crud's
			cave = (Cave) session.merge(cave);
			tx.commit();
			return cave;
		}
	}
	
	//DELETE
	public void deleteCave(Cave cave) {
		try(Session session = sf.openSession()){
			Transaction tx = session.beginTransaction(); //used in certain crud's
			session.delete(cave);
			tx.commit();
		}
	}
}
