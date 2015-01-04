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
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.IStatProcedure;
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

	private static final SimpleDateFormat	formatter_MMdd	= new SimpleDateFormat("MM-dd");

	// logger
	private static Logger					logger			= Logger.getLogger(WorkAssignService.class);

	private IBankHolidays					bank_holiday;

	private final SimpleDateFormat			date_fromatter	= new SimpleDateFormat("yyyy-MM-dd");

	private long							initialDelay;

	private IParams							params;

	private long							period;

	private PersonDAO						personDAO;

	private ISchedule						scheduleDAO;

	private IShiftCache						shiftCache;

	private IStatProcedure					statProcedure;

	@Override
	public void assignStandardWork() {

		try {
			WorkAssignService.logger.info("INIT AUTOMATIC ASSIGN WORK");

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

			// take info shift
			final UserShift work_shift = this.shiftCache.getStandardWorkShift();
			if (work_shift == null) {
				return;
			}

			final UserShift waited_work_shift = this.shiftCache.getWaitedBreakShift();
			if (waited_work_shift == null) {
				return;
			}

			final UserShift break_shift = this.shiftCache.getBreakShift();
			if (break_shift == null) {
				return;
			}

			// check i Sunday. is needed ion order to set programmed break
			boolean isSunaday = false;
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				isSunaday = true;
			}

			// date today
			calendar.add(Calendar.DATE, 1);
			final Date date_tomorrow = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

			// get all persons
			final List<Person> list_person = this.personDAO.listWorkerPersons(null);
			for (final Person person : list_person) {

				Schedule schedule = this.scheduleDAO.loadSchedule(date_tomorrow, person.getId());

				if (schedule == null) {
					schedule = new Schedule();
				}

				// check for day working
				final Integer lenght_series = this.statProcedure.getWorkingSeries(current_day, person.getId());

				if (lenght_series >= 10) {
					// assign work
					this.statProcedure.workAssignProcedure(waited_work_shift, date_tomorrow, person.getId(), null);
				} else {
					if (schedule.getShift() == null) {
						// assign work
						this.statProcedure.workAssignProcedure(work_shift, date_tomorrow, person.getId(), null);
					}
				}

				// assign programmed break
				if (isSunaday) {

					// for daily employee, Saturday and Sunday is bank holiday
					if (person.getDailyemployee().booleanValue()) {

						final Calendar saturday = DateUtils.toCalendar(current_day);
						saturday.add(Calendar.DAY_OF_YEAR, 6);
						final Calendar sunday = DateUtils.toCalendar(current_day);
						sunday.add(Calendar.DAY_OF_YEAR, 7);

						this.statProcedure.workAssignProcedure(break_shift, saturday.getTime(), person.getId(), null);
						this.statProcedure.workAssignProcedure(break_shift, sunday.getTime(), person.getId(), null);

					} else {

						final Date date_break = this.statProcedure.getARandomDay(current_day, 6);
						if (DateUtils.isSameDay(date_break, date_tomorrow)) {
							if (lenght_series < 10) {
								this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);
							}
						} else {
							this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);
						}
					}
				}

				// check is tomorrow is a bank holiday
				if (person.getDailyemployee().booleanValue()) {
					final String date_t_info = WorkAssignService.formatter_MMdd.format(date_tomorrow);
					if (this.bank_holiday.getDays().contains(date_t_info)) {
						this.statProcedure.workAssignProcedure(break_shift, date_tomorrow, person.getId(), null);
					}
				}

			}

			// update control check
			final String val_date = this.date_fromatter.format(current_day);
			this.params.setParam(ParamsTag.ASSIGN_SHIFT_DATE, val_date);

			WorkAssignService.logger.info("END AUTOMATIC ASSIGN WORK");

		} catch (final Exception e) {
			WorkAssignService.logger.error("ERROR IN AUTOMATIC ASSIGN WORK: " + e);
			throw e;
		}

	}

	public IBankHolidays getBank_holiday() {
		return this.bank_holiday;
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

	public IStatProcedure getStatProcedure() {
		return this.statProcedure;
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

	public void setBank_holiday(final IBankHolidays bank_holiday) {
		this.bank_holiday = bank_holiday;
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

	public void setStatProcedure(final IStatProcedure statProcedure) {
		this.statProcedure = statProcedure;
	}

}
