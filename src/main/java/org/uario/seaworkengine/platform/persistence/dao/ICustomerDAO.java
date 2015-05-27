package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Customer;

public interface ICustomerDAO {

	public void createCustomer(Customer customer);

	public void deleteCustomer(Integer customer_id);

	public List<Customer> listAllCustomers();

	public Customer loadCustomer(Integer id_customer);

	public void updateCustomer(Customer customer);

}
