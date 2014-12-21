package org.uario.seaworkengine.platform.persistence.cache;

import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.UserTask;

public interface ITaskCache {

	/**
	 * Build caches
	 *
	 * @param caches
	 */
	public void buildCache(List<UserTask> caches);

	/**
	 * get current hash
	 *
	 * @return
	 */
	public HashMap<Integer, UserTask> getHash();

	/**
	 * Get UserTask
	 *
	 * @param id
	 * @return
	 */
	public UserTask getUserTask(Integer id);

}
