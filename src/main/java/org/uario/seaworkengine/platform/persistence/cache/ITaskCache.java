package org.uario.seaworkengine.platform.persistence.cache;

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
	 * Get UserTask
	 *
	 * @param id
	 * @return
	 */
	public UserTask getUserTask(Integer id);

}
