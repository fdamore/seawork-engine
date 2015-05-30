/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.platform.persistence.cache.ICustomerCache;
import org.uario.seaworkengine.platform.persistence.dao.ICustomerDAO;

public class MyBatisCustomerDAO extends SqlSessionDaoSupport implements ICustomerDAO {

	private static Logger	logger	= Logger.getLogger(MyBatisCustomerDAO.class);

	private ICustomerCache	customer_cache;

	@Override
	public void createCustomer(final Customer customer) {

		MyBatisCustomerDAO.logger.info("createCustomer..");

		this.getSqlSession().insert("customer.createCustomer", customer);

		final List<Customer> list = this.listAllCustomers();
		this.customer_cache.buildCache(list);

	}

	@Override
	public void deleteCustomer(final Integer customer_id) {
		MyBatisCustomerDAO.logger.info("deleteCustomer..");

		this.getSqlSession().delete("customer.deleteCustomer", customer_id);

		final List<Customer> list = this.listAllCustomers();
		this.customer_cache.buildCache(list);

	}

	public ICustomerCache getCustomer_cache() {
		return this.customer_cache;
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

	public void setCustomer_cache(final ICustomerCache customer_cache) {
		this.customer_cache = customer_cache;
	}

	@Override
	public void updateCustomer(final Customer customer) {
		MyBatisCustomerDAO.logger.info("updateCustomer..");

		this.getSqlSession().update("customer.updateCustomer", customer);

		final List<Customer> list = this.listAllCustomers();
		this.customer_cache.buildCache(list);

	}
}
