package org.uario.seaworkengine.platform.persistence.cache.task;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.Customer;
import org.uario.seaworkengine.platform.persistence.cache.ICustomerCache;

public class CustomerCacheImpl implements ICustomerCache {

	// internal hash
	private final HashMap<Integer, Customer>	hash	= new HashMap<Integer, Customer>();

	@Override
	public void buildCache(final List<Customer> caches) {
		// clear internal cache
		this.hash.clear();

		for (final Customer item : caches) {
			this.hash.put(item.getId(), item);
		}

	}

	@Override
	public Customer getCustomer(final Integer id) {
		return this.hash.get(id);
	}

}
