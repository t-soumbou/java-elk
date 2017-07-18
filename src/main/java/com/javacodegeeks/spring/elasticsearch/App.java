package com.javacodegeeks.spring.elasticsearch;

import java.util.List;

import com.javacodegeeks.spring.elasticsearch.entity.Employee;
import com.javacodegeeks.spring.elasticsearch.services.impl.EmployeeServiceImpl;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EmployeeServiceImpl service = new EmployeeServiceImpl();
//        Employee emp = new Employee();
//        emp.setId(2);
//        emp.setAge(10);
//        emp.setName("toto");
//        service.create(emp);
      System.out.println(service.findById(1));        
    }
}
