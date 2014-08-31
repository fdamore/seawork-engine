package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;

public interface ConfigurationDAO {

	public void createShift(UserShift shift);

	public void createTask(UserTask task);

	public List<UserShift> loadShifts();

	public List<UserTask> loadTasks();

	public void removeShift(Integer id);

	public void removeTask(Integer id);

	public void updateShift(UserShift shift);

	public void updateTask(UserTask task);

}
