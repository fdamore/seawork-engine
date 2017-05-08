package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.Service;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.model.WorkerStatus;

public interface ConfigurationDAO {

	public void addService(Service service);

	public void addStatus(String status, String note);
	
	public List<Service> checkServiceExist(String name);

	public void createCrane(Crane crane);

	public void createShift(UserShift shift);

	public void createTask(UserTask task);

	public UserTask getChangeshiftTask();

	public List<Crane> getCrane(Integer id, Integer number, String name, String description);

	public UserTask getDelayOperationTask();

	public UserTask getEndOperationTask();

	public UserTask getOverflowTask();

	public List<UserTask> listAllAbsenceTask();

	public List<UserShift> listAllDefaultShift();

	public List<UserTask> listAllHiddenTask();

	public List<UserTask> listAllJustificatoryTask();

	public List<UserShift> listAllShifts(String full_text_search);

	public List<UserTask> listAllStandardTask();

	public List<UserTask> listAllTasks(String full_text_search);

	public List<UserShift> listRecordedShift();

	public List<UserTask> listSpecialTaskMobile();

	public List<String> loadAllShiftCode();

	public List<String> loadAllTaskCode();

	public List<UserTask> loadInternalTask();

	public UserTask loadPPTask();

	public List<UserTask> loadRecordedTask();

	public Service loadRZService();

	public UserTask loadRZTask();

	public Service loadService(Integer id);

	public UserShift loadShiftById(Integer id);

	public List<UserShift> loadShifts();

	public UserTask loadTask(Integer id);

	public List<UserTask> loadTasks();

	public void updateStatus(WorkerStatus itm);

	public void removeAllAccidentShift();

	public void removeAllBreakShift();

	public void removeAllChangeshiftTasks();

	public void removeAllDailyShift();

	public void removeAllDelayOperationTasks();

	public void removeAllDiseaseShift();

	public void removeAllEndoperationTasks();

	public void removeAllOverflowTasks();

	public void removeAllStandardShift();

	public void removeAllWaitBreakShift();

	public void removeCrane(Integer id);

	public void removeService(Integer id);

	public void removeShift(Integer id);

	public void removeStatus(String description);

	public void removeTask(Integer id);

	public List<WorkerStatus> selectAllStatus();

	public List<Service> selectService(Integer id, String name, String description);

	public void setShiftAsAccident(final Integer id_usershift);

	public void setShiftAsBreak(final Integer id_usershift);

	public void setShiftAsDailyShift(final Integer id_usershift);

	public void setShiftAsDisease(final Integer id_usershift);

	public void setShiftAsExpectedBreak(final Integer id_usershift);

	public void setShiftAsStandardShift(final Integer id_usershift);

	public void setTaskAsChangeshift(Integer idTask);

	public void setTaskAsDelayOperation(Integer idTask);

	public void setTaskAsEndoperation(Integer idTask);

	public void setTaskAsOverflow(Integer idTask);

	public void updateCrane(Crane crane);

	public void updateService(Service service);

	public void updateShift(UserShift shift);

	public void updateTask(UserTask task);

}
