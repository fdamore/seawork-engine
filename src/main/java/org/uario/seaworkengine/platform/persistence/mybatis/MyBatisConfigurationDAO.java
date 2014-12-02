/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;

public class MyBatisConfigurationDAO extends SqlSessionDaoSupport implements ConfigurationDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisConfigurationDAO.class);

	private IShiftCache		shift_cache;

	private ITaskCache		task_cache;

	@Override
	public void addStatus(final String status) {
		MyBatisConfigurationDAO.logger.info("Add Status");

		this.getSqlSession().insert("configuration.addStatus", status);

	}

	@Override
	public void createShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + shift);
		this.getSqlSession().insert("configuration.insertShift", shift);

		// upload cache
		this.shift_cache.buildCache(this.loadShifts());

	}

	@Override
	public void createTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + task);
		this.getSqlSession().insert("configuration.insertTask", task);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());
	}

	public IShiftCache getShift_cache() {
		return this.shift_cache;
	}

	public ITaskCache getTask_cache() {
		return this.task_cache;
	}

	@Override
	public List<UserShift> listAllShifts(final String full_text_search) {
		MyBatisConfigurationDAO.logger.info("listAllShifts like full_text_search");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("my_full_text_search", full_text_search);

		final List<UserShift> list_shift = this.getSqlSession().selectList("configuration.selectAllShiftsFulltextSearchLike", map);
		return list_shift;

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
	public void removeAllAccidentShift() {
		MyBatisConfigurationDAO.logger.info("removeAllAccidentShift");
		this.getSqlSession().update("configuration.removeAllAccidentShift");

	}

	@Override
	public void removeAllBreakShift() {
		MyBatisConfigurationDAO.logger.info("removeAllBreakShift");
		this.getSqlSession().update("configuration.removeAllBreakShift");
	}

	@Override
	public void removeAllDailyShift() {
		MyBatisConfigurationDAO.logger.info("removeAllDailyShift");
		this.getSqlSession().update("configuration.removeAllDailyShift");

	}

	@Override
	public void removeAllDiseaseShift() {
		MyBatisConfigurationDAO.logger.info("removeAllDiseaseShift");
		this.getSqlSession().update("configuration.removeAllDiseaseShift");
	}

	@Override
	public void removeAllStandardShift() {
		MyBatisConfigurationDAO.logger.info("removeAllStandardShift");
		this.getSqlSession().update("configuration.removeAllStandardShift");

	}

	@Override
	public void removeAllWaitBreakShift() {
		this.getSqlSession().update("configuration.removeAllWaitBreakShift");

	}

	@Override
	public void removeShift(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove shift with id " + id);
		this.getSqlSession().delete("configuration.deleteShift", id);

	}

	@Override
	public void removeStatus(final String description) {
		MyBatisConfigurationDAO.logger.info("Removed Status");

		this.getSqlSession().delete("configuration.removeStatus", description);

	}

	@Override
	public void removeTask(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove task with id " + id);
		this.getSqlSession().delete("configuration.deleteTask", id);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());

	}

	@Override
	public List<String> selectAllStatus() {
		MyBatisConfigurationDAO.logger.info("Selected all status");

		return this.getSqlSession().selectList("configuration.selectAllStatus");
	}

	public void setShift_cache(final IShiftCache shift_cache) {
		this.shift_cache = shift_cache;
	}

	@Override
	public void setShiftAsAccident(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsAccident");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllAccidentShift");

		this.getSqlSession().update("configuration.setShiftAsAccident", id_usershift);

	}

	@Override
	public void setShiftAsBreak(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsBreak");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllBreakShift");

		this.getSqlSession().update("configuration.setShiftAsBreak", id_usershift);

	}

	@Override
	public void setShiftAsDailyShift(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsDailyShift");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllDailyShift");

		this.getSqlSession().update("configuration.setShiftAsDailyShift", id_usershift);

	}

	@Override
	public void setShiftAsDisease(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsDisease");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllDiseaseShift");

		this.getSqlSession().update("configuration.setShiftAsDisease", id_usershift);

	}

	@Override
	public void setShiftAsExpectedBreak(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsWaitBreak");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllWaitBreakShift");

		this.getSqlSession().update("configuration.setShiftAsWaitBreak", id_usershift);

	}

	@Override
	public void setShiftAsStandardShift(final Integer id_usershift) {
		MyBatisConfigurationDAO.logger.info("setShiftAsStandard");

		// remove all break shift
		this.getSqlSession().update("configuration.removeAllStandardShift");

		this.getSqlSession().update("configuration.setShiftAsStandardShift", id_usershift);

	}

	public void setTask_cache(final ITaskCache task_cache) {
		this.task_cache = task_cache;
	}

	@Override
	public void updateShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + shift.getId());
		this.getSqlSession().update("configuration.updateShift", shift);

		// upload cache
		this.shift_cache.buildCache(this.loadShifts());
	}

	@Override
	public void updateTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + task.getId());
		this.getSqlSession().update("configuration.updateTask", task);

		// upload cache
		this.task_cache.buildCache(this.loadTasks());

	}

}
