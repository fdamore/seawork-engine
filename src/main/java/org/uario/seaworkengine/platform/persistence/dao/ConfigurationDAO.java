package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;

public interface ConfigurationDAO {

	public void addStatus(String status);

	public void createShift(UserShift shift);

	public void createTask(UserTask task);

	public UserTask getEndOperationTask();

	public UserTask getOverflowTask();

	public List<UserTask> listAllAbsenceTask();

	public List<UserTask> listAllAbsenceTaskForMobile();

	public List<UserShift> listAllDefaultShift();

	public List<UserTask> listAllHiddenOperativeTask();

	public List<UserShift> listAllShifts(String full_text_search);

	public List<UserTask> listAllTasks(String full_text_search);

	public List<String> loadAllShiftCode();

	public List<String> loadAllTaskCode();

	public UserShift loadShiftById(Integer id);

	public List<UserShift> loadShifts();

	public UserTask loadTask(Integer id);

	public List<UserTask> loadTasks();

	public void removeAllAccidentShift();

	public void removeAllBreakShift();

	public void removeAllDailyShift();

	public void removeAllDiseaseShift();

	public void removeAllEndoperationTasks();

	public void removeAllOverflowTasks();

	public void removeAllStandardShift();

	public void removeAllWaitBreakShift();

	public void removeShift(Integer id);

	public void removeStatus(String description);

	public void removeTask(Integer id);

	public List<String> selectAllStatus();

	public void setShiftAsAccident(final Integer id_usershift);

	public void setShiftAsBreak(final Integer id_usershift);

	public void setShiftAsDailyShift(final Integer id_usershift);

	public void setShiftAsDisease(final Integer id_usershift);

	public void setShiftAsExpectedBreak(final Integer id_usershift);

	public void setShiftAsStandardShift(final Integer id_usershift);

	public void setTaskAsEndoperation(Integer idTask);

	public void setTaskAsOverflow(Integer idTask);

	public void updateShift(UserShift shift);

	public void updateTask(UserTask task);

}
