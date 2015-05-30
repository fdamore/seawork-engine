package org.uario.seaworkengine.platform.persistence.cache;

import java.util.List;

import org.uario.seaworkengine.model.Customer;

public interface ICustomerCache {

	/**
	 * Build caches
	 *
	 * @param caches
	 */
	public void buildCache(List<Customer> caches);

	/**
	 * Get Cutomer
	 * 
	 * @param id
	 * @return
	 */
	public Customer getCustomer(Integer id);

}
