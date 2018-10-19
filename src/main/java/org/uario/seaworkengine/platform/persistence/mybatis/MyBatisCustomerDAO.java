/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;

public class MyBatisCustomerDAO extends SqlSessionDaoSupport implements ICustomerDAO {

	private static Logger logger = Logger.getLogger(MyBatisCustomerDAO.class);

	@Override
	public void createCustomer(final Customer customer) {

		MyBatisCustomerDAO.logger.info("createCustomer..");

		this.getSqlSession().insert("customer.createCustomer", customer);

	}

	@Override
	public void deleteCustomer(final Integer customer_id) {
		MyBatisCustomerDAO.logger.info("deleteCustomer..");

		this.getSqlSession().delete("customer.deleteCustomer", customer_id);

	}

	@Override
	public List<Customer> listAllCustomers() {
		MyBatisCustomerDAO.logger.info("listAllCustomers..");

		final List<Customer> list_ret = this.getSqlSession().selectList("customer.selectAllCustomers");

		return list_ret;
	}

	@Override
	public Customer loadCustomer(final Integer id_customer) {
		MyBatisCustomerDAO.logger.info("loadCustomer..");

		final Customer ret = this.getSqlSession().selectOne("customer.selectCustomerById", id_customer);

		return ret;
	}

	@Override
	public List<Customer> selectEnabledCustomer() {
		MyBatisCustomerDAO.logger.info("selectEnabledCustomer..");

		final List<Customer> list_ret = this.getSqlSession().selectList("customer.selectEnabledCustomer");

		return list_ret;
	}

	@Override
	public void updateCustomer(final Customer customer) {
		MyBatisCustomerDAO.logger.info("updateCustomer..");

		this.getSqlSession().update("customer.updateCustomer", customer);

	}
}
