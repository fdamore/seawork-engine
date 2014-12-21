package org.uario.seaworkengine.platform.persistence.cache;

import java.util.HashMap;
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
	 * @return
	 */
	public UserShift getDailyShift();

	/**
	 * Get current hash
	 *
	 * @return
	 */
	public HashMap<Integer, UserShift> getHash();

	/**
	 * Standard work shift
	 *
	 * @return
	 */
	public UserShift getStandardWorkShift();

	/**
	 * Get UserShift
	 *
	 * @param id
	 * @return
	 */
	public UserShift getUserShift(Integer id);

	/**
	 * @return
	 */
	public UserShift getWaitedBreakShift();

}
