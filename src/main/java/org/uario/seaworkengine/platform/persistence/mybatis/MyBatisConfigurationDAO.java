/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;

public class MyBatisConfigurationDAO extends SqlSessionDaoSupport implements ConfigurationDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisConfigurationDAO.class);

	private ITaskCache		task_cache;

	@Override
	public void createShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + shift);
		this.getSqlSession().insert("configuration.insertShift", shift);

	}

	@Override
	public void createTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + task);
		this.getSqlSession().insert("configuration.insertTask", task);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());
	}

	public ITaskCache getTask_cache() {
		return this.task_cache;
	}

	@Override
	public List<UserShift> loadShifts() {
		MyBatisConfigurationDAO.logger.info("Get all shifts..");
		final List<UserShift> list_shifts = this.getSqlSession().selectList("configuration.selectAllShifts");
		return list_shifts;
	}

	@Override
	public List<UserTask> loadTasks() {
		MyBatisConfigurationDAO.logger.info("Get all tasks..");
		final List<UserTask> list_tasks = this.getSqlSession().selectList("configuration.selectAllTasks");
		return list_tasks;
	}

	@Override
	public void removeShift(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove shift with id " + id);
		this.getSqlSession().delete("configuration.deleteShift", id);

	}

	@Override
	public void removeTask(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove task with id " + id);
		this.getSqlSession().delete("configuration.deleteTask", id);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());

	}

	public void setTask_cache(final ITaskCache task_cache) {
		this.task_cache = task_cache;
	}

	@Override
	public void updateShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + shift.getId());
		this.getSqlSession().update("configuration.updateShift", shift);
	}

	@Override
	public void updateTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + task.getId());
		this.getSqlSession().update("configuration.updateTask", task);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());

	}

}
