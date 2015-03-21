package org.uario.seaworkengine.web.services.webcontroller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
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
import org.uario.seaworkengine.web.services.handler.WorkerShift;

public class WebControllerImpl implements IWebServiceController {

	private final Logger logger = Logger.getLogger(WebControllerImpl.class);

	private PersonDAO personDAO;

	private final SimpleDateFormat remote_format_date = new SimpleDateFormat("dd-MM-yyyy");

	private ISchedule scheduleDAO;

	private IShiftCache shiftCache;

	private IScheduleShip ship_dao;

	private ITaskCache taskCache;

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

		return true;

	}
}
