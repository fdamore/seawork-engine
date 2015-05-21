package org.uario.seaworkengine.platform.persistence.cache.task;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.platform.persistence.cache.IShipCache;

public class ShipCacheImpl implements IShipCache {

	// internal hash
	private final HashMap<Integer, Ship>	hash	= new HashMap<Integer, Ship>();

	@Override
	public void buildCache(final List<Ship> caches) {
		// clear internal cache
		this.hash.clear();

		for (final Ship item : caches) {
			this.hash.put(item.getId(), item);
		}

	}

	@Override
	public Ship getShip(final Integer id) {
		return this.hash.get(id);
	}

}
