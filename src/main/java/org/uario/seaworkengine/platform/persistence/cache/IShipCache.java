package org.uario.seaworkengine.platform.persistence.cache;

import java.util.List;

import org.uario.seaworkengine.model.Ship;

public interface IShipCache {

	/**
	 * Build caches
	 *
	 * @param caches
	 */
	public void buildCache(List<Ship> caches);

	/**
	 * Get UserShift
	 *
	 * @param id
	 * @return
	 */
	public Ship getShip(Integer id);

}
