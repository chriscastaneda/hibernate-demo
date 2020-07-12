package com.revature.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.revature.Launcher;
import com.revature.models.Bear;
import com.revature.models.Cave;

public class BearDao {
	
	SessionFactory sf; //reference to SessionFactory

	//Retrieve SessionFactory
	public BearDao() { 
		super();
		sf = Launcher.getSessionFactory();
	}

	//POST
	public void insertBear(Bear bear) {
		//gets session: borrowed connection from pool
		try(Session session = sf.openSession()){ //implement AutoClosable: close when try block is done
			Transaction tx = session.beginTransaction();
			//session.save(bear); //hibernate POST or
			session.persist(bear); // JPA POST
				
			//UPDATE:Automatic Dirty Checking
			bear.setBreed("Brown Bear"); //Modify Bear during transaction
			tx.commit();
		}	
	}
		
	//GetbyId
	public Optional <Bear> selectBear(int id) { 
		try (Session session = sf.openSession()){
			return Optional.ofNullable(session.get(Bear.class, id)); //return bear id. wrapped in Java8 optional for null.
			} 														 //Eager: retrieves from Database, provides persistent object or null
		}
		
	//GET: Lazy Load
	public Bear loadBear(int id) {
		try(Session session = sf.openSession()){
			Bear bear = session.load(Bear.class,  id); //creates/uses proxy when needed, only during on going session
			Hibernate.initialize(bear); //hibernate helper function initialize a proxy object.
			return bear;
		}
	}
		
	//UPDATE
	public void updateBear(Bear bear) {
		try(Session session = sf.openSession()){
			Transaction tx = session.beginTransaction(); //Start transaction(ACID)
			//session.update(bear); //update object
				
			//Merge
			Bear sameBear = session.get(Bear.class, bear.getId()); //copy's bear object ID
			bear = (Bear) session.merge(bear); //merges with same bear object. return sameBear as a different object. point sameBear -> Bear points back to persistient object.
			tx.commit(); //commit update
		}
	}
		
	//DELETE
	public void deleteBear(Bear bear) {
		try(Session session = sf.openSession()){
			Transaction tx = session.beginTransaction();
			session.delete(bear);
		}
	}
	
	//GET: bears by cave id
		public List <Bear> getBearsByCaveId(int caveId){
			try(Session session = sf.openSession()){
				Cave cave = session.get(Cave.class, caveId); //retrieve
				if(cave == null) return null;
				List <Bear> bears = cave.getBears(); //Extract list of bears
				Hibernate.initialize(bears); //initialize bear collection
				return bears;
			}
		}
		
		//Assign cubs to Bears
		public void addCubs(Bear bear, Bear... cubs) {
			try(Session session = sf.openSession()){
				bear = session.get(Bear.class, bear.getId());
				Transaction tx = session.beginTransaction();
				
				if(bear.getCubs() == null) { //create list of cubs if none available
					bear.setCubs(new ArrayList<Bear>());
				}
				
				for(Bear cub : cubs) {
					bear.getCubs().add(cub); //add in cubs
				}
				
				session.merge(bear);
				tx.commit();
			}
		}
}
