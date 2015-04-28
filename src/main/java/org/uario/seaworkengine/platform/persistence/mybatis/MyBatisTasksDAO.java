/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;

public class MyBatisTasksDAO extends SqlSessionDaoSupport implements TasksDAO {
	private static Logger	logger	= Logger.getLogger(MyBatisTasksDAO.class);

	@Override
	public void assignTaskToUser(final Integer id_user, final Integer id_task) {
		MyBatisTasksDAO.logger.info("Assign Task To User");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());
		map.put("id_task", id_task.toString());

		this.getSqlSession().insert("tasks.assignTaskToUser", map);

	}

	@Override
	public void deleteTaskToUser(final Integer id_user, final Integer id_usertask) {

		MyBatisTasksDAO.logger.info("Remove task assigment");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());
		map.put("id_usertask", id_usertask.toString());

		this.getSqlSession().delete("tasks.deleteTaskToUser", map);

	}

	@Override
	public UserTask getDefault(final Integer id_user) {
		MyBatisTasksDAO.logger.info("getDefault");

		// return default
		return this.getSqlSession().selectOne("tasks.getDefault", id_user);

	}

	@Override
	public boolean isTaskAssigned(final Integer id_user, final Integer id_task) {

		MyBatisTasksDAO.logger.info("Get Task assigned");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());
		map.put("id_task", id_task.toString());

		final UserTask task = this.getSqlSession().selectOne("tasks.isTaskAssigned", map);
		if (task != null) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;

	}

	@Override
	public List<UserTask> loadTasksByUser(final Integer id_user) {

		MyBatisTasksDAO.logger.info("Get all tasks by user..");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());

		final List<UserTask> list_tasks = this.getSqlSession().selectList("tasks.selectAllTasksByUser", map);
		return list_tasks;

	}

	@Override
	public List<UserTask> loadTasksByUserForMobile(final Integer id_user) {

		MyBatisTasksDAO.logger.info("loadTasksByUserForMobile...");

		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("id_user", id_user.toString());

		final List<UserTask> list_tasks = this.getSqlSession().selectList("tasks.loadTasksByUserForMobile", map);
		return list_tasks;

	}

	@Override
	public void setAsDefault(final Integer id_user, final Integer id_usertask) {
		MyBatisTasksDAO.logger.info("setAsDefault");

		// remove all default
		final HashMap<String, String> map_0 = new HashMap<String, String>();
		map_0.put("id_user", id_user.toString());
		this.getSqlSession().update("tasks.removeAllDefault", map_0);

		final HashMap<String, String> map_1 = new HashMap<String, String>();
		map_1.put("id_user", id_user.toString());
		map_1.put("id_usertask", id_usertask.toString());

		this.getSqlSession().update("tasks.setAsDefault", map_1);

	}

}
