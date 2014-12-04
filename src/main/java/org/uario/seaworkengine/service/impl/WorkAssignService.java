package org.uario.seaworkengine.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.IParams;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.PersonDAO;
import org.uario.seaworkengine.service.IWorkShiftAssign;
import org.uario.seaworkengine.utility.ParamsTag;

public class WorkAssignService implements IWorkShiftAssign {

	/**
	 * The task to be run
	 *
	 * @author francesco
	 *
	 */
	private class MyRunnable implements Runnable {

		@Override
		public void run() {

			WorkAssignService.this.assignStandardWork();
		}

	}

	// logger
	private static Logger			logger			= Logger.getLogger(WorkAssignService.class);

	private final SimpleDateFormat	date_fromatter	= new SimpleDateFormat("yyyy-MM-dd");

	private long					initialDelay;

	private IParams					params;

	private long					period;

	private PersonDAO				personDAO;

	private ISchedule				scheduleDAO;

	private IShiftCache				shiftCache;

	@Override
	public void assignStandardWork() {

		final Calendar calendar = Calendar.getInstance();
		final Date current_day = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

		// check if the process ran in current day
		final String date_assign = this.params.getParam(ParamsTag.ASSIGN_SHIFT_DATE);
		if (date_assign != null) {
			Date date_to_check = null;
			try {
				date_to_check = this.date_fromatter.parse(date_assign);
			} catch (final ParseException ignore) {
				// in this case, run the project
			}
			if ((date_to_check != null) && date_to_check.equals(current_day)) {
				return;
			}
		}

		final UserShift work_shift = this.shiftCache.getStandardWorkShift();
		if (work_shift == null) {
			return;
		}

		// date today
		calendar.add(Calendar.DATE, 1);
		final Date date_tomorrow = calendar.getTime();

		// get all persons
		final List<Person> list_person = this.personDAO.listWorkerPersons(null);
		for (final Person person : list_person) {

			Schedule schedule = this.scheduleDAO.loadSchedule(date_tomorrow, person.getId());

			if (schedule == null) {
				schedule = new Schedule();
			}

			if ((schedule == null) || (schedule.getShift() == null)) {
				schedule.setDate_schedule(date_tomorrow);
				schedule.setUser(person.getId());
				schedule.setShift(work_shift.getId());
				this.scheduleDAO.saveOrUpdateSchedule(schedule);
			}

		}

		// update control check
		final String val_date = this.date_fromatter.format(current_day);
		this.params.setParam(ParamsTag.ASSIGN_SHIFT_DATE, val_date);

	}

	public long getInitialDelay() {
		return this.initialDelay;
	}

	public IParams getParams() {
		return this.params;
	}

	public long getPeriod() {
		return this.period;
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

	/**
	 * initialize bean.. start schedule
	 */
	public void init() {
		// set runnable
		final MyRunnable runnable = new MyRunnable();

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(runnable, this.initialDelay, this.period, TimeUnit.HOURS);
	}

	public void setInitialDelay(final long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public void setParams(final IParams params) {
		this.params = params;
	}

	public void setPeriod(final long period) {
		this.period = period;
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

}
