/**
 *
 */
package org.uario.seaworkengine.platform.persistence.cache.task;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;

/**
 * @author francesco
 *
 */
public class TaskCacheImpl implements ITaskCache {

	// internal hash
	private final HashMap<Integer, UserTask>	hash	= new HashMap<Integer, UserTask>();

	@Override
	public synchronized void buildCache(final List<UserTask> caches) {

		// clear internal cache
		this.hash.clear();

		for (final UserTask item : caches) {
			this.hash.put(item.getId(), item);
		}

	}

	@Override
	public HashMap<Integer, UserTask> getHash() {
		return this.hash;
	}

	@Override
	public UserTask getUserTask(final Integer id) {
		return this.hash.get(id);
	}
}
