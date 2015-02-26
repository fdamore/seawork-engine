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
import org.uario.seaworkengine.service.IEngineService;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.utility.ParamsTag;

public class EngineServiceImpl implements IEngineService {

	/**
	 * The task to be run
	 *
	 * @author francesco
	 *
	 */
	private class MyRunnable implements Runnable {

		@Override
		public void run() {

			EngineServiceImpl.this.startEngineProcess();
		}

	}

	private static final SimpleDateFormat	formatter_MMdd	= new SimpleDateFormat("MM-dd");

	// logger
	private static Logger					logger			= Logger.getLogger(EngineServiceImpl.class);

	private IBankHolidays					bank_holiday;

	private final SimpleDateFormat			date_fromatter	= new SimpleDateFormat("yyyy-MM-dd");

	private long							initialDelay;

	private IParams							params;

	private long							period;

	private PersonDAO						personDAO;

	private ISchedule						scheduleDAO;

	private IShiftCache						shiftCache;

	private IStatProcedure					statProcedure;

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

	@Override
	public void startEngineProcess() {

		try {
			EngineServiceImpl.logger.info("INIT AUTOMATIC ENGINE SERIVICE");

			final Calendar calendar = Calendar.getInstance();
			final Date current_day = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

			// date today
			calendar.add(Calendar.DATE, 1);
			final Date date_tomorrow = DateUtils.truncate(calendar.getTime(), Calendar.DATE);

			// check if the process ran in current day
			final String date_assign = this.params.getParam(ParamsTag.ASSIGN_SHIFT_DATE);

			if (date_assign != null) {
				Date date_to_check = null;
				try {
					date_to_check = this.date_fromatter.parse(date_assign);
				} catch (final ParseException ignore) {
					// in this case, run the project
				}
				if ((date_to_check != null) && DateUtils.isSameDay(date_to_check, current_day)) {
					return;
				}
			}

			// ASSIGN WORK PROCEDURE

			// take info shift----- check if any info are configured
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

			final UserShift daily_employee_shift = this.shiftCache.getDailyShift();
			if (daily_employee_shift == null) {
				return;
			}

			// ASSIGN WORK FOR TOMORROW-------- BEGIN SUB PROCEDURE
			// get all persons
			final List<Person> list_person = this.personDAO.listWorkerPersons(null);
			for (final Person person : list_person) {

				Schedule schedule = this.scheduleDAO.loadSchedule(date_tomorrow, person.getId());

				if (schedule == null) {
					schedule = new Schedule();
				}

				// check for day working
				final Integer lenght_series_working = this.statProcedure.getWorkingSeries(current_day, person.getId());

				if (lenght_series_working >= 10) {
					// assign work
					this.statProcedure.workAssignProcedure(waited_work_shift, date_tomorrow, person.getId(), null);

				} else {
					if (schedule.getShift() == null) {

						// assign work, only if nobody did

						if (!person.getDailyemployee().booleanValue()) {
							this.statProcedure.workAssignProcedure(work_shift, date_tomorrow, person.getId(), null);
						} else {

							// BUSINESS LOGIC FOR DAILY WORKER
							final String date_t_info = EngineServiceImpl.formatter_MMdd.format(date_tomorrow);
							if (this.bank_holiday.getDays().contains(date_t_info)) {
								// if is a bank holiday, set a break
								this.statProcedure.workAssignProcedure(break_shift, date_tomorrow, person.getId(), null);
							} else {
								// work for daily worker
								this.statProcedure.workAssignProcedure(daily_employee_shift, date_tomorrow, person.getId(), null);
							}

						}
					}
				}

				// END SUB PROCEDURE

				// ASSIGN BREAK PROCEDURE (START ON SUNDAY). BEGIN SUB PROCEDURE

				// check if is Sunday. The flag is used after in the process
				boolean isSunaday = false;

				if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					isSunaday = true;
				}

				if (isSunaday) {

					this.sundayProcess(current_day, person, lenght_series_working);
				}

				// END SUB PROCEDURE

			}

			// update control check
			final String val_date = this.date_fromatter.format(current_day);
			this.params.setParam(ParamsTag.ASSIGN_SHIFT_DATE, val_date);

			EngineServiceImpl.logger.info("END AUTOMATIC ENGINE SERIVICE");

		} catch (final Exception e) {
			EngineServiceImpl.logger.error("ERROR IN AUTOMATIC ENGINE SERIVICE: " + e.getStackTrace());
			throw e;
		}

	}

	/**
	 * Sunday process
	 *
	 * @param current_sunday
	 * @param date_assign
	 * @param break_shift
	 * @param date_tomorrow
	 * @param person
	 * @param lenght_series_working
	 */
	private void sundayProcess(final Date current_sunday, final Person person, final Integer lenght_series_working) {

		final String processed_sunday = this.params.getParam(ParamsTag.PROCESSED_SUNDAY);

		boolean active_sunday_process = true;

		Date date_to_check = null;

		if (processed_sunday != null) {
			try {
				date_to_check = this.date_fromatter.parse(processed_sunday);
			} catch (final ParseException ignore) {
				date_to_check = null;
			}
			if (date_to_check != null) {

				final Calendar next_sunday_to_process = DateUtils.toCalendar(date_to_check);
				next_sunday_to_process.add(Calendar.DAY_OF_YEAR, 14);

				if (!DateUtils.isSameDay(next_sunday_to_process.getTime(), current_sunday)) {
					active_sunday_process = false;
				}

			}
		}

		if (!active_sunday_process) {
			return;
		}

		// get break shift
		final UserShift break_shift = this.shiftCache.getBreakShift();
		if (break_shift == null) {
			return;
		}

		// calculate date tomorrow
		final Calendar monday_cal = DateUtils.toCalendar(current_sunday);
		monday_cal.add(Calendar.DAY_OF_YEAR, 1);
		final Date monday = DateUtils.truncate(monday_cal.getTime(), Calendar.DATE);

		// calculate next Sunday
		final Calendar next_sunday = DateUtils.toCalendar(current_sunday);
		next_sunday.add(Calendar.DAY_OF_YEAR, 7);

		if (person.getDailyemployee().booleanValue()) {
			// for daily employee, Saturday and Sunday is bank

			// FIRST WEEK

			// check is some break is already configured
			final List<Schedule> list_break_first_week = this.statProcedure.searchBreakInCurrentWeek(monday, person.getId());

			if (list_break_first_week == null) {

				final Calendar saturday = DateUtils.toCalendar(current_sunday);
				saturday.add(Calendar.DAY_OF_YEAR, 6);

				this.statProcedure.workAssignProcedure(break_shift, saturday.getTime(), person.getId(), null);
				this.statProcedure.workAssignProcedure(break_shift, next_sunday.getTime(), person.getId(), null);
			}

			// SECOND WEEK

			// check is some break is already configured

			final Calendar next_monday = (Calendar) next_sunday.clone();
			next_monday.add(Calendar.DAY_OF_YEAR, 1);

			final List<Schedule> list_break_second_week = this.statProcedure.searchBreakInCurrentWeek(next_monday.getTime(), person.getId());

			if (list_break_second_week == null) {

				final Calendar next_saturday = DateUtils.toCalendar(next_sunday.getTime());
				next_saturday.add(Calendar.DAY_OF_YEAR, 6);
				final Calendar next_next_sunday = DateUtils.toCalendar(next_sunday.getTime());
				next_next_sunday.add(Calendar.DAY_OF_YEAR, 7);

				this.statProcedure.workAssignProcedure(break_shift, next_saturday.getTime(), person.getId(), null);
				this.statProcedure.workAssignProcedure(break_shift, next_next_sunday.getTime(), person.getId(), null);
			}

		} else {

			// FIRST WEEK - ONLY IF NOT WAITED WORK IS ASSIGNED
			if (lenght_series_working < 10) {

				// check is some break is already setted
				final List<Schedule> list_break = this.statProcedure.searchBreakInCurrentWeek(monday, person.getId());

				if (list_break == null) {
					// only if you not assign waited work
					final Date date_break = this.statProcedure.getARandomDay(current_sunday, 7);
					this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);
				}

			}

			// SECOND WEEK

			// check is some break is already setted
			final Calendar next_monday = (Calendar) next_sunday.clone();
			next_monday.add(Calendar.DAY_OF_YEAR, 1);
			final List<Schedule> list_break_second_week = this.statProcedure.searchBreakInCurrentWeek(next_monday.getTime(), person.getId());

			if (list_break_second_week == null) {

				final Date date_break = this.statProcedure.getARandomDay(next_sunday.getTime(), 7);
				this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);
			}

		}

		// update control check on Sunday process.
		if (date_to_check == null) {
			date_to_check = DateUtils.truncate(current_sunday, Calendar.DATE);
		}
		final String val_date = this.date_fromatter.format(date_to_check);
		this.params.setParam(ParamsTag.PROCESSED_SUNDAY, val_date);

	}
}
