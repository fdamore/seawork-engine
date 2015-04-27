package org.uario.seaworkengine.web.services.webcontroller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IScheduleShip;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.TaskRunner;
import org.uario.seaworkengine.web.services.handler.Worker;
import org.uario.seaworkengine.web.services.handler.WorkerShift;

public class WebControllerImpl implements IWebServiceController {

	private final Logger			logger				= Logger.getLogger(WebControllerImpl.class);

	private PersonDAO				personDAO;

	private final SimpleDateFormat	remote_format_date	= new SimpleDateFormat("dd-MM-yyyy");

	private ISchedule				scheduleDAO;

	private IShiftCache				shiftCache;

	private IScheduleShip			ship_dao;

	private IScheduleShip			shipSchedulerDao;

	private ITaskCache				taskCache;

	private TasksDAO				taskDAO;

	@Override
	public void createDetailFinalScheduleShip(final DetailFinalScheduleShip detailFinalScheduleShip) {
		shipSchedulerDao.createDetailFinalScheduleShip(detailFinalScheduleShip);

	}

	@Override
	public void deleteDetailFinalScheduleShipById(final Integer id_detail_final) {

		shipSchedulerDao.deleteDetailFinalScheduleShipById(id_detail_final);

	}

	/**
	 * final synch process
	 *
	 * @param list_synch
	 * @return
	 */
	private boolean finalSyncProcess(final Date date_request, final Integer no_shift, final List<WorkerShift> list_synch) {
		// map schedule
		final HashMap<Integer, Schedule> schedule_map = new HashMap<Integer, Schedule>();

		// define data synchronize
		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		for (final WorkerShift item : list_synch) {

			for (final Worker worker : item.getWorkers()) {

				if (worker.getUtente() == null) {
					continue;
				}

				if (worker.getUtente().intValue() != 19) {
					continue;
				}

				Schedule schedule = null;
				schedule = schedule_map.get(worker.getUtente());

				// select schedule
				if (schedule == null) {
					schedule = scheduleDAO.loadSchedule(date_schedule, worker.getUtente());
					schedule_map.put(worker.getUtente(), schedule);

					// remove final info in this schedule
					scheduleDAO.removeAllDetailFinalScheduleByScheduleAndShift(schedule.getId(), no_shift);
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

					// define time
					try {
						final Date time_from = remote_format_date.parse(task_item.getEntrata());
						final Date time_to = remote_format_date.parse(task_item.getUscita());

						final Timestamp timestamp_from = new Timestamp(time_from.getTime());
						final Timestamp timestamp_to = new Timestamp(time_to.getTime());

						final_detail.setTime_from(timestamp_from);
						final_detail.setTime_to(timestamp_to);

						// get time
						final long millis = Math.abs(time_to.getTime() - time_from.getTime());
						final double h = (double) millis / (1000 * 60 * 60);
						final UserTask currentTask = taskCache.getUserTask(task_item.getID());
						if (currentTask.getIsabsence().booleanValue()) {
							final_detail.setTime(0.0);
							final_detail.setTime_vacation(h);
						} else {
							final_detail.setTime(h);
							final_detail.setTime_vacation(0.0);
						}

					} catch (final Exception e) {
						logger.error("Error in parsing remote date and timestamp. " + e);

					}

					// insert new info
					scheduleDAO.createDetailFinalSchedule(final_detail);

				}

			}

		}

		// sign as synchronized
		scheduleDAO.updateMobileSynch(true, no_shift);

		return true;
	}

	public PersonDAO getPersonDAO() {
		return personDAO;
	}

	public ISchedule getScheduleDAO() {
		return scheduleDAO;
	}

	public IShiftCache getShiftCache() {
		return shiftCache;
	}

	public IScheduleShip getShip_dao() {
		return ship_dao;
	}

	public IScheduleShip getShipSchedulerDao() {
		return shipSchedulerDao;
	}

	public ITaskCache getTaskCache() {
		return taskCache;
	}

	public TasksDAO getTaskDAO() {
		return taskDAO;
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return new ArrayList<UserShift>(shiftCache.getHash().values());

	}

	@Override
	public List<UserTask> getUserTaskConfiguration() {
		return new ArrayList<UserTask>(taskCache.getHash().values());
	}

	@Override
	public List<Ship> listShip(final Date date_request) {

		final Date date_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		return ship_dao.loadShipInDate(new Timestamp(date_truncate.getTime()));
	}

	@Override
	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(final Integer idDetailScheduleShip) {

		final List<DetailFinalScheduleShip> final_details = shipSchedulerDao
				.loadDetailFinalScheduleShipByIdDetailScheduleShip(idDetailScheduleShip);

		return final_details;
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

		final List<DetailScheduleShip> list = shipSchedulerDao.loadDetailScheduleShipByShiftDateAndShipName(
				date_request_truncate, null, null);
		return list;
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(final Date date_request) {

		final List<InitialSchedule> ret = new ArrayList<InitialSchedule>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		final List<Person> list = personDAO.listAllPersonsForMobile(date_schedule);

		for (final Person person : list) {

			if (person.isBackoffice()) {
				continue;
			}

			if (person.isOperative()) {
				continue;
			}

			final InitialSchedule item = new InitialSchedule();

			final Schedule schedule = scheduleDAO.loadSchedule(date_schedule, person.getId());
			if (schedule == null) {
				continue;
			}

			final List<DetailInitialSchedule> merging_details = new ArrayList<DetailInitialSchedule>();

			// ADD SHIFT 1
			if (!schedule.getSync_mobile_1()) {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 1);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 1);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 2
			if (!schedule.getSync_mobile_2()) {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 2);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 2);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 3
			if (!schedule.getSync_mobile_3()) {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 3);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 3);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// ADD SHIFT 4
			if (!schedule.getSync_mobile_4()) {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 4);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			} else {
				final List<DetailInitialSchedule> itm = scheduleDAO.loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(
						schedule.getId(), 4);
				if (itm != null) {
					merging_details.addAll(itm);
				}
			}

			// CHECK IF ANY ITEM TO ADD
			if (merging_details.size() == 0) {
				continue;
			}

			// set info about task
			final List<UserTask> list_tasks = taskDAO.loadTasksByUser(person.getId());
			person.setUserTaskForMobile(list_tasks);

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(merging_details);
			item.setSchedule(schedule);

			ret.add(item);

		}

		return ret;
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

	public void setShip_dao(final IScheduleShip ship_dao) {
		this.ship_dao = ship_dao;
	}

	public void setShipSchedulerDao(final IScheduleShip shipSchedulerDao) {
		this.shipSchedulerDao = shipSchedulerDao;
	}

	public void setTaskCache(final ITaskCache taskCache) {
		this.taskCache = taskCache;
	}

	public void setTaskDAO(final TasksDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	@Override
	@Transactional
	public boolean synchronizeWork(final Date date_request, final Integer no_shift, final List<WorkerShift> list_synch) {

		if (date_request == null) {
			return false;
		}

		final Date dt = DateUtils.truncate(date_request, Calendar.DATE);

		return finalSyncProcess(dt, no_shift, list_synch);

	}

	@Override
	public void updateDetailFinalScheduleShipForMobile(final DetailFinalScheduleShip detailFinalScheduleShip) {
		shipSchedulerDao.updateDetailFinalScheduleShipForMobile(detailFinalScheduleShip);

	}
}
