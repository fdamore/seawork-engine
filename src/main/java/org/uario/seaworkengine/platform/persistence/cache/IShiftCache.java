package org.uario.seaworkengine.platform.persistence.cache;

import java.util.List;

import org.uario.seaworkengine.model.UserShift;

public interface IShiftCache {

	/**
	 * Build caches
	 *
	 * @param caches
	 */
	public void buildCache(List<UserShift> caches);

	/**
	 * Get break shift
	 * 
	 * @return
	 */
	public UserShift getBreakShift();

	/**
	 * Get UserShift
	 *
	 * @param id
	 * @return
	 */
	public UserShift getUserShift(Integer id);

}
