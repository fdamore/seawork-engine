package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.Service;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.model.WorkerStatus;

public interface ConfigurationDAO {

	void addService(Service service);

	void addStatus(String status, String note);

	List<Service> checkServiceExist(String name);

	void createCrane(Crane crane);

	void createShift(UserShift shift);

	void createTask(UserTask task);

	UserShift getBreakShift();

	UserTask getChangeshiftTask();

	List<Crane> getCrane(Integer id, Integer number, String name, String description);

	UserShift getDailyShift();

	UserTask getDelayOperationTask();

	UserTask getEndOperationTask();

	UserTask getOverflowTask();

	UserShift getStandardWorkShift();

	UserShift getWaitedBreakShift();

	List<UserTask> listAllAbsenceTask();

	List<UserShift> listAllDefaultShift();

	List<UserTask> listAllHiddenTask();

	List<UserTask> listAllJustificatoryTask();

	List<UserShift> listAllShifts(String full_text_search);

	List<UserTask> listAllStandardTask();

	List<UserTask> listAllTasks(String full_text_search);

	List<UserShift> listRecordedShift();

	List<UserTask> listSpecialTaskMobile();

	List<String> loadAllShiftCode();

	List<String> loadAllTaskCode();

	List<UserTask> loadInternalTask();

	UserTask loadPPTask();

	List<UserTask> loadRecordedTask();

	Service loadRZService();

	UserTask loadRZTask();

	Service loadService(Integer id);

	UserShift loadShiftById(Integer id);

	List<UserShift> loadShifts();

	UserTask loadTask(Integer id);

	List<UserTask> loadTasks();

	void removeAllAccidentShift();

	void removeAllBreakShift();

	void removeAllChangeshiftTasks();

	void removeAllDailyShift();

	void removeAllDelayOperationTasks();

	void removeAllDiseaseShift();

	void removeAllEndoperationTasks();

	void removeAllOverflowTasks();

	void removeAllStandardShift();

	void removeAllWaitBreakShift();

	void removeCrane(Integer id);

	void removeService(Integer id);

	void removeShift(Integer id);

	void removeStatus(String description);

	void removeTask(Integer id);

	List<WorkerStatus> selectAllStatus();

	List<Service> selectService(Integer id, String name, String description);

	void setShiftAsAccident(final Integer id_usershift);

	void setShiftAsBreak(final Integer id_usershift);

	void setShiftAsDailyShift(final Integer id_usershift);

	void setShiftAsDisease(final Integer id_usershift);

	void setShiftAsExpectedBreak(final Integer id_usershift);

	void setShiftAsStandardShift(final Integer id_usershift);

	void setTaskAsChangeshift(Integer idTask);

	void setTaskAsDelayOperation(Integer idTask);

	void setTaskAsEndoperation(Integer idTask);

	void setTaskAsOverflow(Integer idTask);

	void updateCrane(Crane crane);

	void updateService(Service service);

	void updateShift(UserShift shift);

	void updateStatus(WorkerStatus itm);

	void updateTask(UserTask task);

}
