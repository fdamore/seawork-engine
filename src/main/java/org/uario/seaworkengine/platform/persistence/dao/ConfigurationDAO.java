package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;

public interface ConfigurationDAO {

	public void addStatus(String status);

	public void createShift(UserShift shift);

	public void createTask(UserTask task);

	public List<UserShift> loadShifts();

	public List<UserTask> loadTasks();

	public void removeAllAccidentShift();

	public void removeAllBreakShift();

	public void removeAllDiseaseShift();

	public void removeAllWaitBreakShift();

	public void removeShift(Integer id);

	public void removeStatus(String description);

	public void removeTask(Integer id);

	public List<String> selectAllStatus();

	public void setShiftAsBreak(final Integer id_usershift);

	public void setShiftAsDisease(final Integer id_usershift);

	public void setShiftAsExpectedBreak(final Integer id_usershift);

	public void setShiftAsInjury(final Integer id_usershift);

	public void updateShift(UserShift shift);

	public void updateTask(UserTask task);

}
