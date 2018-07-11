package org.uario.seaworkengine.web.services.webcontroller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.impl.MonitorData;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.Badge;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.TaskRunner;
import org.uario.seaworkengine.web.services.handler.UserStaturation;
import org.uario.seaworkengine.web.services.handler.Worker;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

public class WebControllerImpl implements IWebServiceController {

	private ConfigurationDAO	configurationDAO;

	private final Logger		logger	= Logger.getLogger(WebControllerImpl.class);

	private PersonDAO			personDAO;

	private ISchedule			scheduleDAO;

	private IShiftCache			shiftCache;

	private IScheduleShip		ship_dao;

	private IStatProcedure		stat_procedure;

	private IStatistics			statistics;

	private ITaskCache			taskCache;

	private TasksDAO			taskDAO;

	@Override
	public List<UserStaturation> calculateUserSaturation(final Date date_request) {

		final ArrayList<UserStaturation> ret = new ArrayList<>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		// set saturation month label
		final Calendar cal_saturation_month = DateUtils.toCalendar(date_schedule);
		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date date_to = cal_saturation_month.getTime();

		cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getMinimum(Calendar.DAY_OF_MONTH));
		final Date date_from = cal_saturation_month.getTime();

		final List<Person> list = this.personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			if (person.isBackoffice()) {
				continue;
			}

			if (person.isOperative()) {
				continue;
			}

			final Double sat = this.stat_procedure.calculeHourSaturation(person, date_from, date_to);

			final UserStaturation item = new UserStaturation();
			item.setId_user(person.getId());
			item.setSaturation(sat);

			ret.add(item);
		}

		return ret;
	}

	@Override
	public Boolean checkUser(final String username, final String password) {
		final Person person = this.personDAO.loadPerson(username, password);

		if (person == null) {
			return Boolean.FALSE;
		}

		if (person.isEnabled() && person.isOperative()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public void createBadge(final Badge badge) {
		this.scheduleDAO.createBadge(badge);
	}

	@Override
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		this.ship_dao.createDetailFinalScheduleShip(detailFinalScheduleShip);

	}

	@Override
	public void deleteBadge(final Integer id_badge) {
		this.scheduleDAO.deleteBadge(id_badge);
	}

	@Override
	public void deleteDetailFinalScheduleShipById(final Integer id_detail_final) {

		this.ship_dao.deleteDetailFinalScheduleShipById(id_detail_final);

	}

	/**
	 * final synch process
	 *
	 * @param list_synch
	 * @return
	 */
	private boolean finalSyncProcess(final Date date_request, final Integer no_shift, final WorkerShift worker_shift) {

		if (worker_shift == null) {
			this.logger.error("worker_shift null");
			return false;
		}

		if (no_shift == null) {
			this.logger.error("no_shift null");
			return false;
		}

		if (date_request == null) {
			this.logger.error("date_request null");
			return false;
		}

		// define data synchronize
		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		for (final Worker worker : worker_shift.getWorkers()) {

			if (worker.getUtente() == null) {
				this.logger.error("Worker null");
				continue;
			}

			if (worker.getUtente().intValue() != 19) {
				continue;
			}

			final Schedule schedule = this.scheduleDAO.loadSchedule(date_schedule, worker.getUtente());

			if (schedule == null) {
				this.logger.error("Schedule null");
				continue;
			}

			// remove final info in this schedule
			this.scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(schedule.getId(), no_shift);

			if ((worker.getTasks() == null) || (worker.getTasks().size() == 0)) {
				this.logger.error("Worker Task Collection null or empty");
				continue;
			}

			for (final TaskRunner task_item : worker.getTasks()) {

				final DetailFinalSchedule final_detail = new DetailFinalSchedule();
				final_detail.setDate_schedule(schedule.getDate_schedule());
				final_detail.setId_schedule(schedule.getId());
				final_detail.setShift(no_shift);
				final_detail.setTask(task_item.getID());

				// set ship and crane
				final_detail.setId_ship(task_item.getShip_id());
				final_detail.setCrane(task_item.getCrane());

				// define on/out board
				final_detail.setBoard(task_item.getBoard());

				// define time
				final Date time_from = task_item.getEntrata();
				final Date time_to = task_item.getUscita();

				// manage time
				if ((time_from == null) || (time_to == null)) {
					this.logger.error("Time from or time to null");
					continue;
				}

				final Timestamp timestamp_from = new Timestamp(time_from.getTime());
				final Timestamp timestamp_to = new Timestamp(time_to.getTime());

				// set time stamp
				final_detail.setTime_from(timestamp_from);
				final_detail.setTime_to(timestamp_to);

				// get time
				final long millis = Math.abs(time_to.getTime() - time_from.getTime());
				final double h = (double) millis / (1000 * 60 * 60);
				final UserTask currentTask = this.taskCache.getUserTask(task_item.getID());

				if (currentTask.getIsabsence().booleanValue()) {
					final_detail.setTime(0.0);
					final_detail.setTime_vacation(h);
				} else {
					final_detail.setTime(h);
					final_detail.setTime_vacation(0.0);
				}

				// insert new info
				this.scheduleDAO.createDetailFinalSchedule(final_detail);

			}

			// sign as synchronized
			this.scheduleDAO.updateMobileSynch(schedule.getId(), true, no_shift);

		}

		return true;
	}

	public ConfigurationDAO getConfigurationDAO() {
		return this.configurationDAO;
	}

	@Override
	public UserTask getCTTask() {
		return this.configurationDAO.getChangeshiftTask();
	}

	@Override
	public UserTask getDelayOperation() {
		return this.configurationDAO.getDelayOperationTask();
	}

	@Override
	public String getDetailScheduleShipNote(final Integer id_schedule) {
		return this.ship_dao.getDetailScheduleShipNote(id_schedule);
	}

	@Override
	public UserTask getEndOperationTask() {
		return this.configurationDAO.getEndOperationTask();

	}

	@Override
	public List<MonitorData> getMonitorData(final Date request) {
		return this.statistics.getMonitorData(request);
	}

	@Override
	public UserTask getOverflowTask() {
		return this.configurationDAO.getOverflowTask();
	}

	public PersonDAO getPersonDAO() {
		return this.personDAO;
	}

	public ISchedule getScheduleDAO() {
		return this.scheduleDAO;
	}

	@Override
	public String getScheduleNote(final Integer id_schedule) {
		final Schedule schedule = this.scheduleDAO.loadScheduleById(id_schedule);
		final String note = schedule.getNote();
		return note;
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return new ArrayList<>(this.shiftCache.getHash().values());

	}

	@Override
	public List<Ship> listShip(final Date date_request) {

		final Date date_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		return this.ship_dao.loadShipInDate(new Timestamp(date_truncate.getTime()));
	}

	@Override
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(final Integer idDetailScheduleShip) {

		final List<DetailFinalScheduleShip> final_details = this.ship_dao.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip);

		return final_details;
	}

	@Override
	public List<Badge> loadListBadge(final Integer id_schedule) {
		return this.scheduleDAO.loadBadgeByScheduleId(id_schedule);

	}

	/**
	 * return detail ship by date
	 *
	 * @param date_request
	 * @return
	 */
	@Override
	public List<DetailScheduleShip> selectDetailScheduleShipByShiftDate(final Date date_request) {

		final Date date_request_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		final List<DetailScheduleShip> list = this.ship_dao.searchDetailScheduleShipByDateshit(date_request_truncate, null, null, null, null, null,
				null, null);
		return list;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(final Date date_request) {

		final List<InitialSchedule> ret = new ArrayList<>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		// get special task
		final List<UserTask> list_special = this.configurationDAO.listSpecialTaskMobile();

		final List<Person> list = this.personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			if (person.isBackoffice()) {
				continue;
			}

			if (person.isOperative()) {
				continue;
			}

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = this.scheduleDAO.loadSchedule(date_schedule, person.getId());
			if (schedule == null) {
				continue;
			}

			final List<DetailInitialSchedule> merging_details = new ArrayList<>();

			// ADD SHIFT 1
			if (!schedule.getSync_mobile_1()) {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(schedule.getId(),
						1);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(schedule.getId(), 1);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 2
			if (!schedule.getSync_mobile_2()) {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(schedule.getId(),
						2);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(schedule.getId(), 2);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 3
			if (!schedule.getSync_mobile_3()) {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(schedule.getId(),
						3);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(schedule.getId(), 3);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 4
			if (!schedule.getSync_mobile_4()) {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(schedule.getId(),
						4);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(schedule.getId(), 4);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// CHECK IF ANY ITEM TO ADD
			if (merging_details.size() == 0) {
				continue;
			}

			// set info about task
			final List<UserTask> list_tasks = this.taskDAO.loadTasksByUserForMobile(person.getId());

			// add special task - add only if not exists
			for (final UserTask itm : list_special) {

				if (!list_tasks.contains(itm)) {
					list_tasks.add(itm);
				}

			}

			person.setUserTaskForMobile(list_tasks);

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(merging_details);
			item.setSchedule(schedule);

			ret.add(item);

		}

		return ret;
	}

	public void setConfigurationDAO(final ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	public void setPersonDAO(final PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public void setScheduleDAO(final ISchedule scheduleDAO) {
		this.scheduleDAO = scheduleDAO;
	}

	public void setShiftCache(final IShiftCache shiftCache) {
		this.shiftCache = shiftCache;
	}

	public void setStat_procedure(final IStatProcedure stat_procedure) {
		this.stat_procedure = stat_procedure;
	}

	public void setStatistics(final IStatistics statistics) {
		this.statistics = statistics;
	}

	public void setTaskCache(final ITaskCache taskCache) {
		this.taskCache = taskCache;
	}

	public void setTaskDAO(final TasksDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	@Override
	@Transactional
	public boolean synchronizeWork(final Date date_request, final Integer no_shift, final WorkerShift worker_shift) {

		if (date_request == null) {
			return false;
		}

		final Date dt = DateUtils.truncate(date_request, Calendar.DATE);

		return this.finalSyncProcess(dt, no_shift, worker_shift);

	}

	@Override
	public void updateDetailScheduleShipForMobile(final Integer id, final String operation, final Integer handswork, final Integer menwork,
			final String temperature, final String sky, final String rain, final String wind, final Date first_down, final Date last_down,
			final Date person_down, final Date person_onboard) {

		final DetailScheduleShip sch = new DetailScheduleShip();
		sch.setId(id);
		sch.setOperation(operation);
		sch.setHandswork(handswork);
		sch.setMenwork(menwork);
		sch.setTemperature(temperature);
		sch.setSky(sky);
		sch.setRain(rain);
		sch.setWind(wind);
		sch.setFirst_down(first_down);
		sch.setLast_down(last_down);
		sch.setPerson_down(person_down);
		sch.setPerson_onboard(person_onboard);

		this.ship_dao.updateDetailScheduleShipForMobile(sch);

	}

	@Override
	public void updateScheduleNote(final Integer id_schedule, final String note) {
		this.scheduleDAO.updateScheduleNote(id_schedule, note);
	}

}
