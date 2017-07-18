package com.javacodegeeks.spring.elasticsearch.services;

import java.util.List;

import com.javacodegeeks.spring.elasticsearch.entity.Employee;


/**
 * Persistence Interface for Employee.
 */
public interface EmployeeService { 

	/**
	 * Tries to find an entity using its Id / Primary Key
	 * @param id
	 * @return entity
	 */
	Employee findById( Integer id  ) ;

	/**
	 * Finds all entities.
	 * @return all entities
	 */
	List<Employee> findAll();

	/**
	 * Saves the given entity in the database (create or update)
	 * @param entity
	 * @return entity
	 */
	Employee save(Employee entity);

	/**
	 * Updates the given entity in the database
	 * @param entity
	 * @return true if the entity has been updated, false if not found and not updated
	 */
	boolean update(Employee entity);

	/**
	 * Creates the given entity in the database
	 * @param entity
	 * @return
	 */
	Employee create(Employee entity);

	/**
	 * Deletes an entity using its Id / Primary Key
	 * @param id
	 * @return true if the entity has been deleted, false if not found and not deleted
	 */
	boolean deleteById( Integer id );

	/**
	 * Deletes an entity using the Id / Primary Key stored in the given object
	 * @param the entity to be deleted (supposed to have a valid Id/PK )
	 * @return true if the entity has been deleted, false if not found and not deleted
	 */
	boolean delete( Employee entity );

}