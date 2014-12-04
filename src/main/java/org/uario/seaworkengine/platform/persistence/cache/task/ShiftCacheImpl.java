/**
 *
 */
package org.uario.seaworkengine.platform.persistence.cache.task;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;

/**
 * @author francesco
 *
 */
public class ShiftCacheImpl implements IShiftCache {

	private UserShift							breakShift;

	// internal hash
	private final HashMap<Integer, UserShift>	hash	= new HashMap<Integer, UserShift>();

	private UserShift							standardWorkShift;

	@Override
	public void buildCache(final List<UserShift> caches) {

		// clear internal cache
		this.hash.clear();

		for (final UserShift item : caches) {
			this.hash.put(item.getId(), item);

			// check if item have to be added as beaskShift
			if (item.getBreak_shift().booleanValue()) {
				this.breakShift = item;
			}

			if (item.getStandard_shift().booleanValue()) {
				this.standardWorkShift = item;
			}

		}

	}

	@Override
	public UserShift getBreakShift() {
		return this.breakShift;
	}

	@Override
	public UserShift getStandardWorkShift() {
		return this.standardWorkShift;
	}

	@Override
	public UserShift getUserShift(final Integer id) {
		return this.hash.get(id);
	}

	public void setStandardWorkShift(final UserShift standardWorkShift) {
		this.standardWorkShift = standardWorkShift;
	}
}
