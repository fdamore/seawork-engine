package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.UserTask;

public interface TasksDAO {

	void assignTaskToUser(Integer id_user, Integer id_task);

	void deleteTaskToUser(final Integer id_user, final Integer id_usertask);

	UserTask getDefault(Integer id_user);

	boolean isTaskAssigned(Integer id_user, Integer id_task);

	List<UserTask> listAllTask();

	UserTask loadTask(Integer idTask);

	List<UserTask> loadTasksByUser(Integer id_user);

	List<UserTask> loadTasksByUserForMobile(Integer id_user);

	List<UserTask> loadTasksForMobile();

	void setAsDefault(final Integer id_user, final Integer id_usertask);

}
