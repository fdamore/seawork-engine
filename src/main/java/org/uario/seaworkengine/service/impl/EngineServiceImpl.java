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
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
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

	private ConfigurationDAO				configuration;

	private final SimpleDateFormat			date_fromatter	= new SimpleDateFormat("yyyy-MM-dd");

	private long							initialDelay;

	private IParams							params;

	private long							period;

	private PersonDAO						personDAO;

	private ISchedule						scheduleDAO;

	private IStatProcedure					statProcedure;

	/**
	 * Check for legal time (after 3.15
	 */
	private boolean checkForDaylightSavingTime() {

		final Calendar	calendar	= Calendar.getInstance();

		final double	hours		= calendar.get(Calendar.HOUR_OF_DAY);
		final int		minutes		= calendar.get(Calendar.MINUTE);
		if (hours < 3) {
			return false;
		}
		if ((hours == 3) && (minutes <= 15)) {
			return false;
		}

		return true;
	}

	public IBankHolidays getBank_holiday() {
		return this.bank_holiday;
	}

	public ConfigurationDAO getConfiguration() {
		return this.configuration;
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

	public IStatProcedure getStatProcedure() {
		return this.statProcedure;
	}

	/**
	 * initialize bean.. start schedule
	 */
	public void init() {
		// set runnable
		final MyRunnable				runnable	= new MyRunnable();

		final ScheduledExecutorService	scheduler	= Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(runnable, this.initialDelay, this.period, TimeUnit.HOURS);
	}

	public void setBank_holiday(final IBankHolidays bank_holiday) {
		this.bank_holiday = bank_holiday;
	}

	public void setConfiguration(final ConfigurationDAO configuration) {
		this.configuration = configuration;
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

	public void setStatProcedure(final IStatProcedure statProcedure) {
		this.statProcedure = statProcedure;
	}

	@Override
	public void startEngineProcess() {

		try {
			EngineServiceImpl.logger.info("INIT AUTOMATIC ENGINE SERVICE");

			// the process must start after the potential daylight saving split
			final boolean check_daylight_saving = this.checkForDaylightSavingTime();
			if (!check_daylight_saving) {
				return;
			}

			final Calendar	calendar	= Calendar.getInstance();
			final Date		current_day	= DateUtils.truncate(calendar.getTime(), Calendar.DATE);

			// date today
			calendar.add(Calendar.DATE, 1);
			final Date		date_tomorrow	= DateUtils.truncate(calendar.getTime(), Calendar.DATE);

			// check if the process ran in current day
			final String	date_assign		= this.params.getParam(ParamsTag.ASSIGN_SHIFT_DATE);

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

			// ***** define selected shift type ***

			UserShift				work_shift				= null;
			UserShift				waited_work_shift		= null;
			UserShift				break_shift				= null;
			UserShift				daily_employee_shift	= null;

			final List<UserShift>	list_shifts				= this.configuration.listAllDefaultShift();	//
			for (final UserShift item : list_shifts) {

				// check if item have to be added as beaskShift
				if (item.getBreak_shift().booleanValue()) {
					break_shift = item;
				}

				if (item.getStandard_shift().booleanValue()) {
					work_shift = item;
				}

				if (item.getWaitbreak_shift().booleanValue()) {
					waited_work_shift = item;
				}

				if (item.getDaily_shift().booleanValue()) {
					daily_employee_shift = item;
				}

			}

			// check integrity
			if ((work_shift == null) || (waited_work_shift == null) || (break_shift == null) || (daily_employee_shift == null)) {
				return;
			}

			// ASSIGN WORK PROCEDURE

			// ASSIGN WORK FOR TOMORROW-------- BEGIN SUB PROCEDURE
			// get all persons
			final List<Person> list_person = this.personDAO.listWorkerPersons(null, null);
			for (final Person person : list_person) {

				Schedule schedule = this.scheduleDAO.loadSchedule(date_tomorrow, person.getId());

				if (schedule == null) {
					schedule = new Schedule();
				}

				// check for day working
				final Integer lenght_series_working = this.statProcedure.getWorkingSeries(current_day, person.getId());

				if (lenght_series_working >= 10) {

					// remove all break in current weeks
					final List<Schedule> list_break_day = this.statProcedure.searchBreakInCurrentWeek(date_tomorrow, person.getId(), false);
					if (list_break_day != null) {
						for (final Schedule itm : list_break_day) {

							UserShift itm_usr_shift = null;
							if (person.getDailyemployee()) {
								itm_usr_shift = daily_employee_shift;
							} else {
								itm_usr_shift = work_shift;
							}

							this.statProcedure.workAssignProcedure(itm_usr_shift, itm.getDate_schedule(), itm.getUser(), null, null, true);

						}
					}

					// assign waited break work
					this.statProcedure.workAssignProcedure(waited_work_shift, date_tomorrow, person.getId(), null, null, true);

				} else {

					// check if is needed to assign work each shift

					final boolean	isScheduleNull	= schedule.getShift() == null;
					boolean			isDayEmpty		= true;

					if (!isScheduleNull) {

						final List<DetailInitialSchedule> list_check = this.scheduleDAO.loadDetailInitialScheduleByIdSchedule(schedule.getId());

						if ((list_check == null) || (list_check.size() != 0)) {
							isDayEmpty = false;
						}

					}

					if (isScheduleNull || isDayEmpty) {

						// assign work, only if nobody did

						if (!person.getDailyemployee().booleanValue()) {

							UserShift shift_to_assign = null;

							if (!isScheduleNull) {
								shift_to_assign = this.configuration.loadShiftById(schedule.getShift());
							} else {
								shift_to_assign = work_shift;
							}

							this.statProcedure.workAssignProcedure(shift_to_assign, date_tomorrow, person.getId(), null, null, true);

						} else {

							// BUSINESS LOGIC FOR DAILY WORKER
							final String date_t_info = EngineServiceImpl.formatter_MMdd.format(date_tomorrow);
							if (this.bank_holiday.getDays().contains(date_t_info)) {

								UserShift shift_to_assign = null;

								if (!isScheduleNull) {
									shift_to_assign = this.configuration.loadShiftById(schedule.getShift());
								} else {
									shift_to_assign = break_shift;
								}

								// if is a bank holiday, set a break
								this.statProcedure.workAssignProcedure(shift_to_assign, date_tomorrow, person.getId(), null, null, true);
							} else {

								UserShift shift_to_assign = null;

								if (!isScheduleNull) {
									shift_to_assign = this.configuration.loadShiftById(schedule.getShift());
								} else {
									shift_to_assign = daily_employee_shift;
								}

								// work for daily worker
								this.statProcedure.workAssignProcedure(shift_to_assign, date_tomorrow, person.getId(), null, null, true);
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

					this.sundayProcess(current_day, person, break_shift);
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
	public void sundayProcess(final Date current_sunday, final Person person, final UserShift break_shift) {

		// check break shift
		if (break_shift == null) {
			return;
		}

		// calculate next Sunday
		final Calendar next_sunday = DateUtils.toCalendar(current_sunday);
		next_sunday.add(Calendar.DAY_OF_YEAR, 7);

		if (person.getDailyemployee().booleanValue()) {
			// for daily employee, Saturday and Sunday is bank

			// SECOND WEEK

			// check is some break is already configured

			final Calendar next_monday = (Calendar) next_sunday.clone();
			next_monday.add(Calendar.DAY_OF_YEAR, 1);

			final List<Schedule> list_break_second_week = this.statProcedure.searchBreakInCurrentWeek(next_monday.getTime(), person.getId(), true);

			if (list_break_second_week == null) {

				final Calendar next_saturday = DateUtils.toCalendar(next_sunday.getTime());
				next_saturday.add(Calendar.DAY_OF_YEAR, 6);
				final Calendar next_next_sunday = DateUtils.toCalendar(next_sunday.getTime());
				next_next_sunday.add(Calendar.DAY_OF_YEAR, 7);

				this.statProcedure.workAssignProcedure(break_shift, next_saturday.getTime(), person.getId(), null, null, true);
				this.statProcedure.workAssignProcedure(break_shift, next_next_sunday.getTime(), person.getId(), null, null, true);
			}

		} else {

			// SECOND WEEK

			// check is some break is already setted
			final Calendar next_monday = (Calendar) next_sunday.clone();
			next_monday.add(Calendar.DAY_OF_YEAR, 1);
			final List<Schedule> list_break_second_week = this.statProcedure.searchBreakInCurrentWeek(next_monday.getTime(), person.getId(), true);

			if (list_break_second_week == null) {

				final Calendar cal = DateUtils.truncate(next_sunday, Calendar.DATE);
				cal.add(Calendar.DAY_OF_YEAR, -1);

				final List<Schedule>	list_break_first_week	= this.statProcedure.searchBreakInCurrentWeek(cal.getTime(), person.getId(), true);

				int						max_day_to_break		= 7;

				if (list_break_first_week != null) {

					// choose a different max day
					final Schedule	item_schedule	= list_break_first_week.get(list_break_first_week.size() - 1);
					final Calendar	day_calendar	= DateUtils.toCalendar(item_schedule.getDate_schedule());

					if (day_calendar.get(Calendar.DAY_OF_WEEK) < Calendar.THURSDAY) {

						final Date		max_day		= DateUtils.truncate(item_schedule.getDate_schedule(), Calendar.DATE);
						final Calendar	cal_max_day	= DateUtils.toCalendar(max_day);
						cal_max_day.add(Calendar.DAY_OF_YEAR, 10);

						max_day_to_break = cal_max_day.get(Calendar.DAY_OF_WEEK) - 1;
					}

				}

				final Date date_break = this.statProcedure.getARandomDay(next_sunday.getTime(), max_day_to_break);
				this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null, null, true);
			}

		}

	}
}
