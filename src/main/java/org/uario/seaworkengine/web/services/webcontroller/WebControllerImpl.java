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
import org.uario.seaworkengine.model.DetailInitialSchedule;
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

	private ITaskCache				taskCache;

	/**
	 * final synch process
	 *
	 * @param list_synch
	 * @return
	 */
	private boolean finalSyncProcess(final Date date_request, final List<WorkerShift> list_synch) {
		// map schedule
		final HashMap<Integer, Schedule> schedule_map = new HashMap<Integer, Schedule>();

		// define data synchronize
		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

		for (final WorkerShift item : list_synch) {

			final Integer no_shift = item.getNumber();

			for (final Worker worker : item.getWorkers()) {

				if (worker.getUtente() == null) {
					continue;
				}

				if (worker.getUtente().intValue() != 19) {
					continue;
				}

				Schedule schedule = null;
				schedule = schedule_map.get(worker.getUtente());
				if (schedule == null) {
					schedule = this.scheduleDAO.loadSchedule(date_schedule, worker.getUtente());
					schedule_map.put(worker.getUtente(), schedule);

					// remove final info in this schedule
					this.scheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());
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
						final Date time_from = this.remote_format_date.parse(task_item.getEntrata());
						final Date time_to = this.remote_format_date.parse(task_item.getUscita());

						final Timestamp timestamp_from = new Timestamp(time_from.getTime());
						final Timestamp timestamp_to = new Timestamp(time_to.getTime());

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

					} catch (final Exception e) {
						this.logger.error("Error in parsing remote date and timestamp. " + e);

					}

					// insert new info
					this.scheduleDAO.createDetailFinalSchedule(final_detail);

				}

			}

		}

		// sign as synchronized
		this.scheduleDAO.updateMobileSynch(true);

		return true;
	}

	public PersonDAO getPersonDAO() {
		return this.personDAO;
	}

	public ISchedule getScheduleDAO() {
		return this.scheduleDAO;
	}

	public IShiftCache getShiftCache() {
		return this.shiftCache;
	}

	public IScheduleShip getShip_dao() {
		return this.ship_dao;
	}

	public ITaskCache getTaskCache() {
		return this.taskCache;
	}

	@Override
	public List<UserShift> getUserShiftConfiguration() {
		return new ArrayList<UserShift>(this.shiftCache.getHash().values());

	}

	@Override
	public List<UserTask> getUserTaskConfiguration() {
		return new ArrayList<UserTask>(this.taskCache.getHash().values());
	}

	@Override
	public List<Ship> listShip(final Date date_request) {

		final Date date_truncate = DateUtils.truncate(date_request, Calendar.DATE);

		return this.ship_dao.loadShipInDate(new Timestamp(date_truncate.getTime()));
	}

	@Override
	public List<InitialSchedule> selectInitialSchedule(final Date date_request) {

		final List<InitialSchedule> ret = new ArrayList<InitialSchedule>();

		final Date date_schedule = DateUtils.truncate(date_request, Calendar.DATE);

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

			List<DetailInitialSchedule> details = null;
			if ((schedule.getSync_mobile() == null) || !schedule.getSync_mobile()) {
				details = this.scheduleDAO.loadDetailInitialScheduleForMobileByIdSchedule(schedule.getId());
			} else {
				details = this.scheduleDAO.loadDetailFinalScheduleForMobileByIdSchedule(schedule.getId());
			}

			if ((details == null) || (details.size() == 0)) {
				continue;
			}

			// set current object
			item.setPerson(person);
			item.setDetail_schedule(details);
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

	public void setTaskCache(final ITaskCache taskCache) {
		this.taskCache = taskCache;
	}

	@Override
	@Transactional
	public boolean synchronizeWork(final Date date_request, final List<WorkerShift> list_synch) {

		if (date_request == null) {
			return false;
		}

		final Date dt = DateUtils.truncate(date_request, Calendar.DATE);

		return this.finalSyncProcess(dt, list_synch);

	}
}
