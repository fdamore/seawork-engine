/**
 *
 */
package org.uario.seaworkengine.platform.persistence.cache.shift;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;

/**
 * @author francesco
 *
 */
public class ShiftCache implements IShiftCache {

	// internal hash
	private final HashMap<Integer, UserShift>	hash	= new HashMap<Integer, UserShift>();

	@Override
	public void buildCache(final List<UserShift> caches) {

		// clear internal cache
		this.hash.clear();

		for (final UserShift item : caches) {
			this.hash.put(item.getId(), item);
		}

	}

	@Override
	public UserShift getUserShift(final Integer id) {
		return this.hash.get(id);
	}
}
