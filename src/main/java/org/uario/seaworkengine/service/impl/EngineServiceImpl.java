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

			// ASSIGN WORK PROCEDURE

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

			final UserShift daily_employee_shift = this.shiftCache.getDailyShift();
			if (daily_employee_shift == null) {
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
						// assign work, only if nobody assigned it
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

				// assign programmed break
				if (isSunaday) {

					boolean active_sunday_process = true;

					final String processed_sunday = this.params.getParam(ParamsTag.PROCESSED_SUNDAY);
					Date date_to_check = null;
					if (processed_sunday != null) {
						try {
							date_to_check = this.date_fromatter.parse(date_assign);
						} catch (final ParseException ignore) {
							// in this case, run sunday process
						}
						if (date_to_check != null) {

							final long days = (current_day.getTime() - date_to_check.getTime()) / (1000 * 60 * 60 * 24);
							if (days < 15) {
								active_sunday_process = false;
							}

						}
					}

					if (active_sunday_process) {
						// RUN ONLY IF THIS SUNDAY IS JUST THE RIGHT SUNDAY

						// holiday - FIRST WEEK
						if (person.getDailyemployee().booleanValue()) {
							// for daily employee, Saturday and Sunday is bank

							final Calendar saturday = DateUtils.toCalendar(current_day);
							saturday.add(Calendar.DAY_OF_YEAR, 6);
							final Calendar sunday = DateUtils.toCalendar(current_day);
							sunday.add(Calendar.DAY_OF_YEAR, 7);

							this.statProcedure.workAssignProcedure(break_shift, saturday.getTime(), person.getId(), null);
							this.statProcedure.workAssignProcedure(break_shift, sunday.getTime(), person.getId(), null);

						} else {
							if (lenght_series < 10) {
								// only if you not assign waited work
								final Date date_break = this.statProcedure.getARandomDay(current_day, 6);
								this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);

							}
						}

						// holiday - SECOND WEEK
						if (person.getDailyemployee().booleanValue()) {
							// for daily employee, Saturday and Sunday is bank

							final Calendar saturday = DateUtils.toCalendar(current_day);
							saturday.add(Calendar.DAY_OF_YEAR, 13);
							final Calendar sunday = DateUtils.toCalendar(current_day);
							sunday.add(Calendar.DAY_OF_YEAR, 14);

							this.statProcedure.workAssignProcedure(break_shift, saturday.getTime(), person.getId(), null);
							this.statProcedure.workAssignProcedure(break_shift, sunday.getTime(), person.getId(), null);

						} else {
							// only if you not assign waited work
							final Calendar next_week_start = DateUtils.toCalendar(current_day);
							next_week_start.add(Calendar.DAY_OF_YEAR, 7);

							final Date date_break = this.statProcedure.getARandomDay(next_week_start.getTime(), 6);
							this.statProcedure.workAssignProcedure(break_shift, date_break, person.getId(), null);

						}

						// update control check
						if (date_to_check == null) {
							date_to_check = DateUtils.truncate(current_day, Calendar.DATE);
						}
						final String val_date = this.date_fromatter.format(date_to_check);
						this.params.setParam(ParamsTag.PROCESSED_SUNDAY, val_date);

					}
				}

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

}
