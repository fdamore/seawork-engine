package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.UserTask;

public interface TasksDAO {

	public void assignTaskToUser(Integer id_user, Integer id_task);

	public void deleteTaskToUser(final Integer id_user, final Integer id_usertask);

	public UserTask getDefault(Integer id_user);

	public boolean isTaskAssigned(Integer id_user, Integer id_task);

	public List<UserTask> loadTasksByUser(Integer id_user);

	public void setAsDefault(final Integer id_user, final Integer id_usertask);

}
