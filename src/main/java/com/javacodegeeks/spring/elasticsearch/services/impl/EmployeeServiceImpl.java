package com.javacodegeeks.spring.elasticsearch.services.impl;
import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.javacodegeeks.spring.elasticsearch.common.GenericDAO;
import com.javacodegeeks.spring.elasticsearch.entity.Employee;
import com.javacodegeeks.spring.elasticsearch.services.EmployeeService;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class EmployeeServiceImpl extends GenericDAO<Employee> implements EmployeeService{

	/**
	 * DAO constructor
	 */
	public EmployeeServiceImpl() {
		super("employee", "Employee", "index1", Employee.class);
	}
	/**
	 * make key in a good format
	 * 
	 * @param bean;
	 * @return key  with a good format
	 */
	@Override
	protected String getKey(Employee bean) {
		return buildString(bean.getId());
	}
	/**
	 * Creates a new instance of the bean
	 * primary value(s)
	 * 
	 * @param key;
	 * @return the new instance
	 */
	private Employee newInstanceWithPrimaryKey(Integer id) {
		Employee employee = new Employee ();
        employee.setId(id); 
		return employee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public Employee findById(Integer id){
        Employee  employee = newInstanceWithPrimaryKey(id);
		return super.doSelect(employee);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public List<Employee> findAll() {
		return super.doSelectAll();
	}

	/**
	 * Loads the given bean, it is supposed to contains the primary key value(s)
	 * in its attribute(s)<br>
	 * If found, the given instance is populated with the values retrieved from
	 * the database<br>
	 * If not found, the given instance remains unchanged
	 */
	public Employee load(Employee employee) {
		return super.doSelect(employee);
	}

	/**
	 * Inserts the given bean in the database
	 * 
	 */
	public boolean insert(Employee employee){
		byte res = super.doInsert(employee);
		return res==0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public Employee save(Employee employee){
		if (super.doExists(employee)) {
			super.doUpdate(employee);
		} else {
			insert(employee);
		}
        return employee;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public boolean update(Employee employee){
		byte res= super.doUpdate(employee);	
		return res == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public Employee create(Employee employee){
		insert(employee);
		return employee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public boolean deleteById(Integer id) {
		Employee employee = newInstanceWithPrimaryKey(id);
		long r = super.doDelete(employee);
		return r > 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see interface
	 */
	public boolean delete(Employee employee) {
		long r = super.doDelete(employee);
		return r > 0L;
	}

	/**
	 * Checks the existence of a record in the database using the given primary
	 * key value(s)
	 
	 * @return
	 */
	public boolean exists(Integer id) {
		Employee employee = newInstanceWithPrimaryKey(id);
		return super.doExists(employee);
	}

	/**
	 * Checks the existence of the given bean in the database
	
	 * @return
	 */
	public boolean exists(Employee employee) {
		return super.doExists(employee);
	}

	/**
	 * Counts all the records present in the database
	 * 
	 * @return
	 */
	public long count() {
		return super.doCountAll();
	}
	
	@Override
	protected Employee populateBean(Employee bean, GetResponse response) {
		System.out.println(response.getSourceAsString());
		bean.setId(Integer.valueOf(response.getField("id").getValue().toString()));
		bean.setAge(Integer.valueOf(response.getField("age").getValue().toString()));
		bean.setName(response.getField("name").getValue().toString());
		return bean;
	}
	
	@Override
	protected XContentBuilder entityToXbuilder(Employee bean) {
		try {
			XContentBuilder builder = jsonBuilder()
				    .startObject()
				        .field("id", ""+bean.getId())
				        .field("name", bean.getName())
				        .field("age", ""+bean.getAge())
				    .endObject();
			return builder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  null;
	}
}