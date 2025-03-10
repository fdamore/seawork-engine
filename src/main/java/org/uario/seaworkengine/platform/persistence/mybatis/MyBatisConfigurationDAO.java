/*
 * MyBatisPersonDAO.java
 * Created on 09/mag/2012
 */
package org.uario.seaworkengine.platform.persistence.mybatis;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.uario.seaworkengine.model.Crane;
import org.uario.seaworkengine.model.Service;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.model.WorkerStatus;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;

public class MyBatisConfigurationDAO extends SqlSessionDaoSupport implements ConfigurationDAO {

	private static Logger logger = Logger.getLogger(MyBatisConfigurationDAO.class);

	@Override
	public void addService(final Service service) {
		MyBatisConfigurationDAO.logger.info("Add Service");

		this.getSqlSession().insert("configuration.addService", service);

	}

	@Override
	public void addStatus(final String status, final String note) {
		MyBatisConfigurationDAO.logger.info("Add Status");

		final HashMap<String, String> map = new HashMap<>();
		map.put("description", status);
		map.put("note", note);

		this.getSqlSession().insert("configuration.addStatus", map);

	}

	@Override
	public List<Service> checkServiceExist(final String name) {
		MyBatisConfigurationDAO.logger.info("Check if Service exist");

		return this.getSqlSession().selectList("configuration.checkServiceExist", name);
	}

	@Override
	public void createCrane(final Crane crane) {
		MyBatisConfigurationDAO.logger.info("Create crane");

		this.getSqlSession().insert("configuration.createCrane", crane);
	}

	@Override
	public void createShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + shift);
		this.getSqlSession().insert("configuration.insertShift", shift);

	}

	@Override
	public void createTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Insert shift " + task);
		this.getSqlSession().insert("configuration.insertTask", task);

	}

	@Override
	public UserShift getBreakShift() {

		final List<UserShift> list_shifts = this.listAllDefaultShift(); //

		for (final UserShift item : list_shifts) {

			// check if item have to be added as beaskShift
			if (item.getBreak_shift().booleanValue()) {
				return item;
			}

		}

		return null;

	}

	@Override
	public UserTask getChangeshiftTask() {
		MyBatisConfigurationDAO.logger.info("getChangeshiftTask");
		return this.getSqlSession().selectOne("configuration.getChangeshiftTask");
	}

	@Override
	public List<Crane> getCrane(final Integer id, final Integer number, final String name, final String description) {
		MyBatisConfigurationDAO.logger.info("Search crane");

		final HashMap<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("number", number);
		map.put("name", name);
		map.put("description", description);

		return this.getSqlSession().selectList("configuration.getCrane", map);
	}

	@Override
	public UserShift getDailyShift() {

		final List<UserShift> list_shifts = this.listAllDefaultShift(); //

		for (final UserShift item : list_shifts) {

			if (item.getDaily_shift().booleanValue()) {
				return item;
			}

		}

		return null;

	}

	@Override
	public UserTask getDelayOperationTask() {
		MyBatisConfigurationDAO.logger.info("getDelayOperationTask");
		return this.getSqlSession().selectOne("configuration.getDelayOperationTask");
	}

	@Override
	public UserTask getEndOperationTask() {
		MyBatisConfigurationDAO.logger.info("getEndOperationTask");
		return this.getSqlSession().selectOne("configuration.getEndOperationTask");
	}

	@Override
	public UserTask getOverflowTask() {
		MyBatisConfigurationDAO.logger.info("getOverflowTask");
		return this.getSqlSession().selectOne("configuration.getOverflowTask");
	}

	@Override
	public UserShift getStandardWorkShift() {

		final List<UserShift> list_shifts = this.listAllDefaultShift(); //

		for (final UserShift item : list_shifts) {

			if (item.getStandard_shift().booleanValue()) {
				return item;
			}

		}

		return null;

	}

	@Override
	public UserShift getWaitedBreakShift() {

		final List<UserShift> list_shifts = this.listAllDefaultShift(); //

		for (final UserShift item : list_shifts) {

			if (item.getWaitbreak_shift().booleanValue()) {
				return item;
			}

		}

		return null;

	}

	@Override
	public List<UserTask> listAllAbsenceTask() {
		MyBatisConfigurationDAO.logger.info("List All Absence Task..");

		return this.getSqlSession().selectList("configuration.listAllAbsenceTask");

	}

	@Override
	public List<UserShift> listAllDefaultShift() {
		MyBatisConfigurationDAO.logger.info("listAllDefaultShifts");

		final List<UserShift> list_shift = this.getSqlSession().selectList("configuration.selectAllDefaultShifts");
		return list_shift;
	}

	@Override
	public List<UserTask> listAllHiddenTask() {
		MyBatisConfigurationDAO.logger.info("List All Hidden Task..");

		return this.getSqlSession().selectList("configuration.listAllHiddenTask");
	}

	@Override
	public List<UserTask> listAllJustificatoryTask() {
		MyBatisConfigurationDAO.logger.info("List All Justificatory Task..");

		return this.getSqlSession().selectList("configuration.listAllJustificatoryTask");
	}

	@Override
	public List<UserShift> listAllShifts(final String full_text_search) {
		MyBatisConfigurationDAO.logger.info("listAllShifts like full_text_search");

		final HashMap<String, String> map = new HashMap<>();
		map.put("my_full_text_search", full_text_search);

		final List<UserShift> list_shift = this.getSqlSession().selectList("configuration.selectAllShiftsFulltextSearchLike", map);
		return list_shift;

	}

	@Override
	public List<UserTask> listAllStandardTask() {
		MyBatisConfigurationDAO.logger.info("List All Standard Task..");

		return this.getSqlSession().selectList("configuration.listAllStandardTask");
	}

	@Override
	public List<UserTask> listAllTasks(final String full_text_search) {
		MyBatisConfigurationDAO.logger.info("listAllTasks like full_text_search");

		final HashMap<String, String> map = new HashMap<>();
		map.put("my_full_text_search", full_text_search);

		final List<UserTask> list_task = this.getSqlSession().selectList("configuration.selectAllTasksFulltextSearchLike", map);
		return list_task;
	}

	@Override
	public List<UserShift> listRecordedShift() {
		MyBatisConfigurationDAO.logger.info("listRecordedShift");

		return this.getSqlSession().selectList("configuration.listRecordedShift");
	}

	@Override
	public List<UserTask> listSpecialTaskMobile() {
		MyBatisConfigurationDAO.logger.info("Load all shift code");

		return this.getSqlSession().selectList("configuration.listSpecialTaskMobile");
	}

	@Override
	public List<String> loadAllShiftCode() {
		MyBatisConfigurationDAO.logger.info("Load all shift code");

		return this.getSqlSession().selectList("configuration.loadAllShiftCode");
	}

	@Override
	public List<String> loadAllTaskCode() {
		MyBatisConfigurationDAO.logger.info("Load all task code");

		return this.getSqlSession().selectList("configuration.loadAllTaskCode");
	}

	@Override
	public List<UserTask> loadInternalTask() {
		MyBatisConfigurationDAO.logger.info("loadInternalTask");
		return this.getSqlSession().selectList("configuration.loadInternalTask");
	}

	@Override
	public UserTask loadPPTask() {
		MyBatisConfigurationDAO.logger.info("loadPPTask");
		return this.getSqlSession().selectOne("configuration.loadPPTask");
	}

	@Override
	public List<UserTask> loadRecordedTask() {
		MyBatisConfigurationDAO.logger.info("loadRecordedTask");
		return this.getSqlSession().selectList("configuration.loadRecordedTask");
	}

	@Override
	public Service loadRZService() {
		MyBatisConfigurationDAO.logger.info("loadRZService");
		return this.getSqlSession().selectOne("configuration.loadRZService");
	}

	@Override
	public UserTask loadRZTask() {
		MyBatisConfigurationDAO.logger.info("loadRZTask");
		return this.getSqlSession().selectOne("configuration.loadRZTask");
	}

	@Override
	public Service loadService(final Integer id) {
		MyBatisConfigurationDAO.logger.info("loadService");
		return this.getSqlSession().selectOne("configuration.loadService", id);
	}

	@Override
	public UserShift loadShiftById(final Integer id) {
		MyBatisConfigurationDAO.logger.info("loadShiftById " + id);

		return this.getSqlSession().selectOne("configuration.loadShiftById", id);
	}

	@Override
	public List<UserShift> loadShifts() {
		MyBatisConfigurationDAO.logger.info("Get all shifts..");
		final List<UserShift> list_shifts = this.getSqlSession().selectList("configuration.selectAllShifts");
		return list_shifts;
	}

	@Override
	public UserTask loadTask(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Load task by id" + id);

		return this.getSqlSession().selectOne("configuration.loadTask", id);
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
	public void removeAllChangeshiftTasks() {
		MyBatisConfigurationDAO.logger.info("removeAllChangeshiftTasks");
		this.getSqlSession().update("configuration.removeAllChangeshiftTasks");

	}

	@Override
	public void removeAllDailyShift() {
		MyBatisConfigurationDAO.logger.info("removeAllDailyShift");
		this.getSqlSession().update("configuration.removeAllDailyShift");

	}

	@Override
	public void removeAllDelayOperationTasks() {
		MyBatisConfigurationDAO.logger.info("removeAllDelayOperationTasks");
		this.getSqlSession().update("configuration.removeAllDelayOperationTasks");

	}

	@Override
	public void removeAllDiseaseShift() {
		MyBatisConfigurationDAO.logger.info("removeAllDiseaseShift");
		this.getSqlSession().update("configuration.removeAllDiseaseShift");
	}

	@Override
	public void removeAllEndoperationTasks() {
		MyBatisConfigurationDAO.logger.info("removeAllEndoperationTasks");
		this.getSqlSession().update("configuration.removeAllEndoperationTasks");

	}

	@Override
	public void removeAllOverflowTasks() {
		MyBatisConfigurationDAO.logger.info("removeAllOverflowTasks");
		this.getSqlSession().update("configuration.removeAllOverflowTasks");

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
	public void removeCrane(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove crane");

		this.getSqlSession().delete("configuration.removeCrane", id);
	}

	@Override
	public void removeService(final Integer id) {
		MyBatisConfigurationDAO.logger.info("Remove Service");

		this.getSqlSession().delete("configuration.removeService", id);

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

	}

	@Override
	public List<WorkerStatus> selectAllStatus() {
		MyBatisConfigurationDAO.logger.info("Selected all status");

		return this.getSqlSession().selectList("configuration.selectAllStatus");
	}

	@Override
	public List<Service> selectService(final Integer id, final String name, final String description) {
		MyBatisConfigurationDAO.logger.info("Select service");

		final HashMap<String, String> map = new HashMap<>();
		if (id != null) {
			map.put("id", id.toString());
		} else {
			map.put("id", null);
		}
		map.put("name", name);
		map.put("description", description);

		return this.getSqlSession().selectList("configuration.selectService", map);
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

	@Override
	public void setTaskAsChangeshift(final Integer idTask) {
		MyBatisConfigurationDAO.logger.info("setTaskAsChangeshift");

		this.getSqlSession().update("configuration.removeAllChangeshiftTasks");

		this.getSqlSession().update("configuration.setTaskAsChangeshift", idTask);

	}

	@Override
	public void setTaskAsDelayOperation(final Integer idTask) {
		MyBatisConfigurationDAO.logger.info("setTaskAsDelayOperation");

		this.removeAllDelayOperationTasks();

		this.getSqlSession().update("configuration.setTaskAsDelayOperation", idTask);

	}

	@Override
	public void setTaskAsEndoperation(final Integer idTask) {
		MyBatisConfigurationDAO.logger.info("setTaskAsEndoperation");

		this.getSqlSession().update("configuration.removeAllEndoperationTasks");

		this.getSqlSession().update("configuration.setTaskAsEndoperation", idTask);

	}

	@Override
	public void setTaskAsOverflow(final Integer idTask) {
		MyBatisConfigurationDAO.logger.info("setTaskAsEndoperation");

		this.getSqlSession().update("configuration.removeAllOverflowTasks");

		this.getSqlSession().update("configuration.setTaskAsOverflow", idTask);

	}

	@Override
	public void updateCrane(final Crane crane) {
		MyBatisConfigurationDAO.logger.info("Update crane");

		this.getSqlSession().update("configuration.updateCrane", crane);
	}

	@Override
	public void updateService(final Service service) {
		MyBatisConfigurationDAO.logger.info("Update service ");
		this.getSqlSession().update("configuration.updateService", service);

	}

	@Override
	public void updateShift(final UserShift shift) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + shift.getId());
		this.getSqlSession().update("configuration.updateShift", shift);

	}

	@Override
	public void updateStatus(final WorkerStatus itm) {
		MyBatisConfigurationDAO.logger.info("Update Status ");
		this.getSqlSession().update("configuration.updateStatus", itm);

	}

	@Override
	public void updateTask(final UserTask task) {
		MyBatisConfigurationDAO.logger.info("Update person with id " + task.getId());
		this.getSqlSession().update("configuration.updateTask", task);

	}

}
