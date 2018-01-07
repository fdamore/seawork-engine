package org.uario.seaworkengine.statistics.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.IShiftCache;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.platform.persistence.dao.UserCompensationDAO;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.RateShift;
import org.uario.seaworkengine.statistics.UserStatistics;
import org.uario.seaworkengine.utility.Utility;

public class StatProceduresImpl implements IStatProcedure {

	private IBankHolidays		bank_holiday;

	private UserCompensationDAO	compensationDAO;

	private ISchedule			myScheduleDAO;

	private TasksDAO			myTaskDAO;

	private IShiftCache			shiftCache;

	private IStatistics			statisticDAO;

	@Override
	public Double calculeHourSaturation(final Person user, final Date date_from_arg, final Date date_to_arg) {

		if ((date_from_arg == null) || (user == null) || (date_to_arg == null)) {
			return null;
		}

		if ((user.getHourswork_w() == null) || (user.getDaywork_w() == null)) {
			return null;
		}

		final Date date_to = DateUtils.truncate(date_to_arg, Calendar.DATE);
		final Date date_from = DateUtils.truncate(date_from_arg, Calendar.DATE);

		// hours working
		final Double hour_current_work_count = this.statisticDAO.getTimeWorkCountByUser(user.getId(), date_from, date_to);

		// validate date
		if (hour_current_work_count == null) {
			return 0.0;
		}

		final int days = Utility.getDayBetweenDate(date_from, date_to);

		// remove some absence from day_worked
		final Integer day_absence = this.statisticDAO.daysToRemoveFromSaturation(user.getId(), date_from, date_to);
		final Integer days_worked = days - day_absence;

		final double hour_per_day = user.getHourswork_w().doubleValue() / user.getDaywork_w().doubleValue();

		// work day amount
		final Double hours_work_ammount = days_worked * hour_per_day;

		// get compensation
		final Double hours_compensate = this.compensationDAO.getTotalHoursInPeriod(user.getId(), date_from, date_to);

		// sat = current work - shift_rec - compensation - work_ammount
		final Double hours_saturation = hour_current_work_count - hours_work_ammount - hours_compensate;

		return hours_saturation;

	}

	/**
	 * Get a random day from current day to current_day+borderday
	 *
	 * @param current_day
	 * @param border_day
	 *            days to add at current day
	 * @return a random day between current day and current_day + border_days
	 */
	@Override
	public Date getARandomDay(final Date current_day, final Integer border_day) {

		final Integer adding_int = 1 + (int) (Math.random() * border_day);

		final Date pick_date = DateUtils.truncate(current_day, Calendar.DATE);
		final Calendar calendar = DateUtils.toCalendar(pick_date);
		calendar.add(Calendar.DAY_OF_YEAR, adding_int);

		return calendar.getTime();

	}

	public IBankHolidays getBank_holiday() {
		return this.bank_holiday;
	}

	public UserCompensationDAO getCompensationDAO() {
		return this.compensationDAO;
	}

	/**
	 * Get Maximum Shift in Day
	 *
	 * @return
	 */
	@Override
	public Integer getFirstShiftInDay(final Date date_calendar_schedule, final Integer user) {

		return this.myScheduleDAO.getFirstShift(date_calendar_schedule, user);

	}

	/**
	 * Get Minimum Shift in same date
	 *
	 * @return
	 */
	@Override
	public Integer getLastShiftInDay(final Date date_calendar_schedule, final Integer user) {

		return this.myScheduleDAO.getLastShift(date_calendar_schedule, user);

	}

	/**
	 * Get Minimum Shift
	 *
	 * @return
	 */
	@Override
	public Integer getMaximumShift(final Date date_calendar_schedule, final Integer user) {

		// get info from last shift
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date_calendar_schedule);
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		int max_shift = 4;

		// get a shift - 12 after last shift
		Integer first_shift = this.myScheduleDAO.getFirstShift(calendar.getTime(), user);

		if (first_shift != null) {

			// define recall. If -9999 a break is found
			if (first_shift == -9999) {

				first_shift = this.getMaximumShift(calendar.getTime(), user);
				return first_shift;
			}

			if (first_shift == 2) {
				max_shift = 3;
			}
			if (first_shift == 1) {
				max_shift = 2;
			}
		}

		return max_shift;

	}

	/**
	 * Get Minimum Shift
	 *
	 * @return
	 */
	@Override
	public Integer getMinimumShift(final Date date_calendar_schedule, final Integer user) {

		// get info from last shift
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date_calendar_schedule);
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		int min_shift = 1;

		// get a shift - 12 after last shift
		Integer last_shift = this.myScheduleDAO.getLastShift(calendar.getTime(), user);

		if (last_shift != null) {

			// define recall. If -9999 a break is found
			if (last_shift == -9999) {

				last_shift = this.getMinimumShift(calendar.getTime(), user);
				return last_shift;
			}

			if (last_shift == 3) {
				min_shift = 2;
			}

			if (last_shift == 4) {
				min_shift = 3;
			}

			return min_shift;

		} else {
			return min_shift;
		}

	}

	public ISchedule getMyScheduleDAO() {
		return this.myScheduleDAO;
	}

	public TasksDAO getMyTaskDAO() {
		return this.myTaskDAO;
	}

	public IShiftCache getShiftCache() {
		return this.shiftCache;
	}

	/**
	 * Get the right shift number for a given day
	 *
	 * @param current_date_scheduled
	 * @param user
	 * @return
	 */
	private Integer getShiftNoForDay(final Date current_date_scheduled, final Integer user) {

		// get info for the begin of current year
		final Calendar calendar_first_day = Calendar.getInstance();
		calendar_first_day.set(Calendar.DAY_OF_YEAR, 1);
		final Date date_first_day_year = calendar_first_day.getTime();

		final RateShift[] averages = this.statisticDAO.getAverageForShift(user, current_date_scheduled, date_first_day_year);

		// get a shift - 12 after last shift
		final Integer min_shift = this.getMinimumShift(current_date_scheduled, user);

		// get my shift
		if ((averages == null) || (averages.length == 0)) {

			final Integer my_shift = min_shift + (int) (Math.random() * 4);
			return my_shift;

		}

		if (min_shift == 1) {
			return averages[0].getShift();
		}

		for (final RateShift current_shift : averages) {

			if (current_shift.getShift() >= min_shift) {
				return current_shift.getShift();
			}

		}

		// exit
		return 1;
	}

	public IStatistics getStatisticDAO() {
		return this.statisticDAO;
	}

	@Override
	public UserStatistics getUserStatistics(final Person person, final Date date_from, final Date date_to, final boolean ext_info) {
		final UserStatistics userStatistics = new UserStatistics();

		userStatistics.setDepartment(person.getDepartment());

		// SET WORK SUNDAY
		// count holidays until now
		final int count_allsunday = this.bank_holiday.countSundaysUntilDate(date_from, date_to);

		// get number of Sunday work
		final Integer sunday_work = this.statisticDAO.getSundayWork(person.getId(), date_from, date_to);

		Double perc_sunday;
		if (count_allsunday == 0) {
			perc_sunday = 0.0;
		} else {
			perc_sunday = (100 * (double) sunday_work) / count_allsunday;
		}
		final double pc_sunday = Utility.roundTwo(perc_sunday);
		final String perc_info = "" + sunday_work + " (" + pc_sunday + "%)";

		// set perc for sunday
		userStatistics.setWork_sunday_perc(perc_info);
		userStatistics.setWork_sunday(sunday_work.toString());
		userStatistics.setWork_sunday_onlyperc(pc_sunday + "%");

		// SET WORK SUNDAY HOLIDAYS

		// count holidays until now
		final int count_allholiday = this.bank_holiday.countHolidays(date_from, date_to);

		final Integer holidays_work = this.statisticDAO.getHolidaysWork(person.getId(), date_from, date_to);

		final Double perc_holiday;
		if (count_allholiday == 0) {
			perc_holiday = 0.0;
		} else {
			perc_holiday = (100 * (double) holidays_work) / count_allholiday;
		}
		final double pc_holiday = Utility.roundTwo(perc_holiday);
		final String perc_info_holiday = "" + holidays_work + " (" + pc_holiday + "%)";

		// set perc
		userStatistics.setWork_holiday_perc(perc_info_holiday);
		userStatistics.setWork_holiday(holidays_work.toString());
		userStatistics.setWork_holiday_onlyperc(pc_holiday + "%");

		// get average - review base
		RateShift[] statistic = this.statisticDAO.getAverageForShift(person.getId(), date_to, date_from);

		if (statistic != null) {
			for (final RateShift av : statistic) {

				if (av.getShift() == 1) {
					userStatistics.setShift_perc_1_base("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 2) {
					userStatistics.setShift_perc_2_base("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 3) {
					userStatistics.setShift_perc_3_base("" + Utility.roundTwo(av.getRate()) + "%");
				}
				if (av.getShift() == 4) {
					userStatistics.setShift_perc_4_base("" + Utility.roundTwo(av.getRate()) + "%");
				}
			}
		}

		// get average - set Sunday work percentage
		statistic = this.statisticDAO.getCountSundayForShift(person.getId(), date_to, date_from);

		// define sunday count for stat average.
		int sunday_work_count = 0;
		for (final RateShift av : statistic) {
			sunday_work_count += av.getRate();

		}

		if (statistic != null) {
			for (final RateShift av : statistic) {

				if (sunday_work != 0) {

					if (av.getShift() == 1) {

						final String base = userStatistics.getShift_perc_1_base();
						final double perc = Utility.roundTwo((av.getRate() / sunday_work_count) * 100);
						final String info = base + " (" + perc + "%)";

						userStatistics.setShift_perc_1(info);
						userStatistics.setShift_perc_1_percholiday("" + perc + "%");
					}

					if (av.getShift() == 2) {

						final String base = userStatistics.getShift_perc_2_base();
						final double perc = Utility.roundTwo((av.getRate() / sunday_work_count) * 100);
						final String info = base + " (" + perc + "%)";

						userStatistics.setShift_perc_2(info);
						userStatistics.setShift_perc_2_percholiday("" + perc + "%");
					}
					if (av.getShift() == 3) {

						final String base = userStatistics.getShift_perc_3_base();
						final double perc = Utility.roundTwo((av.getRate() / sunday_work_count) * 100);
						final String info = base + " (" + perc + "%)";

						userStatistics.setShift_perc_3(info);
						userStatistics.setShift_perc_3_percholiday("" + perc + "%");
					}
					if (av.getShift() == 4) {

						final String base = userStatistics.getShift_perc_4_base();
						final double perc = Utility.roundTwo((av.getRate() / sunday_work_count) * 100);
						final String info = base + " (" + perc + "%)";

						userStatistics.setShift_perc_4(info);
						userStatistics.setShift_perc_4_percholiday("" + perc + "%");

					}
				} else {
					if (av.getShift() == 1) {
						final String base = userStatistics.getShift_perc_1_base();
						userStatistics.setShift_perc_1(base + " (0%)");
						userStatistics.setShift_perc_1_percholiday("0%");
					}
					if (av.getShift() == 2) {
						final String base = userStatistics.getShift_perc_2_base();
						userStatistics.setShift_perc_2(base + " (0%)");
						userStatistics.setShift_perc_2_percholiday("0%");
					}
					if (av.getShift() == 3) {
						final String base = userStatistics.getShift_perc_3_base();
						userStatistics.setShift_perc_3(base + " (0%)");
						userStatistics.setShift_perc_3_percholiday("0%");
					}
					if (av.getShift() == 4) {
						final String base = userStatistics.getShift_perc_4_base();
						userStatistics.setShift_perc_4(base + " (0%)");
						userStatistics.setShift_perc_4_percholiday("0%");
					}
				}

			}
		}

		// set saturation label
		final Double sat = this.calculeHourSaturation(person, date_from, date_to);
		userStatistics.setSaturation(sat);

		if (ext_info) {
			// set info about week working
			final Calendar current = DateUtils.toCalendar(date_to);
			current.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			Date date_start = current.getTime();

			Double week_current_hours = this.statisticDAO.getTimeWorked(person.getId(), date_start, date_to);
			if (week_current_hours == null) {
				week_current_hours = 0.0;
			}
			week_current_hours = Utility.roundTwo(week_current_hours);

			userStatistics.setWork_current_week("" + week_current_hours);

			// set info about month working
			current.set(Calendar.DAY_OF_MONTH, current.getActualMinimum(Calendar.DAY_OF_MONTH));
			date_start = current.getTime();

			Double month_current_hours = this.statisticDAO.getTimeWorked(person.getId(), date_start, date_to);
			if (month_current_hours == null) {
				month_current_hours = 0.0;
			}
			month_current_hours = Utility.roundTwo(month_current_hours);

			userStatistics.setWork_current_month("" + month_current_hours);

			// set info about year working
			current.set(Calendar.YEAR, current.get(Calendar.YEAR));
			current.set(Calendar.WEEK_OF_YEAR, 1);
			current.set(Calendar.DAY_OF_YEAR, 1);
			date_start = current.getTime();

			Double year_current_hours = this.statisticDAO.getTimeWorked(person.getId(), date_start, date_to);
			if (year_current_hours == null) {
				year_current_hours = 0.0;
			}
			year_current_hours = Utility.roundTwo(year_current_hours);

			userStatistics.setWork_current_year("" + year_current_hours);

			// working series
			final Calendar take_today = Calendar.getInstance();
			final Integer day_series = this.getWorkingSeries(take_today.getTime(), person.getId());
			String message = "0";
			if (day_series <= 15) {
				message = "" + day_series;
			} else {
				message = ">=" + day_series;
			}
			userStatistics.setWorking_series(message);

			// set saturation month label - current month
			final Calendar cal_saturation_month = Calendar.getInstance();
			cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date date_to_on_month = cal_saturation_month.getTime();
			cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMinimum(Calendar.DAY_OF_MONTH));
			Date date_from_on_month = cal_saturation_month.getTime();

			final Double sat_curr_month = this.calculeHourSaturation(person, date_from_on_month, date_to_on_month);
			userStatistics.setSaturation_month(sat_curr_month);

			// set saturation month label - prec month
			cal_saturation_month.add(Calendar.MONTH, -1);
			cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMaximum(Calendar.DAY_OF_MONTH));
			date_to_on_month = cal_saturation_month.getTime();
			cal_saturation_month.set(Calendar.DAY_OF_MONTH, cal_saturation_month.getActualMinimum(Calendar.DAY_OF_MONTH));
			date_from_on_month = cal_saturation_month.getTime();

			final Double sat_prec_month = this.calculeHourSaturation(person, date_from_on_month, date_to_on_month);
			userStatistics.setSaturation_prec_month(sat_prec_month);

		} else {

			// adding current_work on period
			Double hours_working = this.statisticDAO.getTimeWorked(person.getId(), date_from, date_to);

			hours_working = Utility.roundTwo(hours_working);

			userStatistics.setWork_current("" + hours_working);

		}

		// set label in statistic popup
		userStatistics.setUserRoles("");
		final String roles = person.getRolesDescription();
		if (roles != "") {
			userStatistics.setUserRoles(roles + ".");
		}

		return userStatistics;
	}

	/**
	 * get working series lenght
	 *
	 * @param date
	 * @param user
	 * @return how many day the user worked consequentially. 15 is mas countable
	 *         number
	 */
	@Override
	public Integer getWorkingSeries(final Date date, final Integer user) {

		if ((date == null) || (user == null)) {
			return 0;
		}

		final Date my_pick_date = DateUtils.truncate(date, Calendar.DATE);

		// EXIT CONDITION
		final Calendar exit_condition = Calendar.getInstance();
		exit_condition.set(Calendar.YEAR, 2015);
		exit_condition.set(Calendar.MONTH, Calendar.JANUARY);
		exit_condition.set(Calendar.DAY_OF_YEAR, 1);
		if (my_pick_date.before(exit_condition.getTime())) {
			return 0;
		}

		final Schedule schedule = this.myScheduleDAO.loadSchedule(my_pick_date, user);

		if (schedule == null) {

			final Calendar cal = DateUtils.toCalendar(my_pick_date);
			cal.add(Calendar.DAY_OF_YEAR, -1);

			return 1 + this.getWorkingSeries(cal.getTime(), user);

		} else {
			final Integer shift_type = schedule.getShift();
			if (shift_type == null) {
				return 0;
			}

			final UserShift shift = this.shiftCache.getUserShift(shift_type);
			if (shift.getPresence()) {

				final Calendar cal = DateUtils.toCalendar(my_pick_date);
				cal.add(Calendar.DAY_OF_YEAR, -1);

				return 1 + this.getWorkingSeries(cal.getTime(), user);

			} else {
				return 0;
			}
		}

	}

	/**
	 * reassign shift and task to schedule
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor
	 */
	@Override
	public void reAssignShift(final Schedule schedule, final Integer editor) {

		if (schedule == null) {
			return;
		}

		if (schedule.getShift() == null) {
			return;
		}

		final Date truncDate = DateUtils.truncate(schedule.getDate_schedule(), Calendar.DATE);

		if (editor != null) {
			schedule.setEditor(editor);
		}

		final UserShift myShift = this.shiftCache.getUserShift(schedule.getShift());

		// override
		this.myScheduleDAO.saveOrUpdateSchedule(schedule);

		// if the shift is an absence, delete all details
		if (!myShift.getPresence().booleanValue()) {
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
			this.myScheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());
		} else {

			// check if there is any default task (MANSIONE STANDARD)
			final UserTask task_default = this.myTaskDAO.getDefault(schedule.getUser());
			if (task_default == null) {
				return;
			}

			// get a shift for a day
			final Integer my_no_shift = this.getShiftNoForDay(truncDate, schedule.getUser());

			// remove all detail in any shift
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
			this.myScheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());

			if (myShift.getDaily_shift().booleanValue()) {

				final DetailInitialSchedule item1 = new DetailInitialSchedule();
				item1.setId_schedule(schedule.getId());
				item1.setShift(2);
				item1.setTask(task_default.getId());
				item1.setTime(4.0);

				final DetailInitialSchedule item2 = new DetailInitialSchedule();
				item2.setId_schedule(schedule.getId());
				item2.setShift(3);
				item2.setTask(task_default.getId());
				item2.setTime(4.0);

				// create detail
				this.myScheduleDAO.createDetailInitialSchedule(item1);
				this.myScheduleDAO.createDetailInitialSchedule(item2);

			} else {
				final DetailInitialSchedule item = new DetailInitialSchedule();
				item.setId_schedule(schedule.getId());
				item.setShift(my_no_shift);
				item.setTask(task_default.getId());
				item.setTime(6.0);

				// create detail
				this.myScheduleDAO.createDetailInitialSchedule(item);

			}

		}

	}

	/**
	 * Search for break in current week
	 *
	 * @param date_scheduled
	 * @param user_id
	 * @return null if any break found. List of break if
	 */

	@Override
	public List<Schedule> searchBreakInCurrentWeek(final Date date_scheduled, final Integer user_id, final boolean include_current) {

		final Date date_truncate = DateUtils.truncate(date_scheduled, Calendar.DATE);

		final Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date_truncate);

		final Calendar first = (Calendar) cal.clone();
		first.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		final Calendar last = (Calendar) first.clone();
		last.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		final List<Schedule> scheduleListInWeek = this.myScheduleDAO.selectScheduleInIntervalDateByUserId(user_id, first.getTime(), last.getTime());

		final ArrayList<Schedule> ret = new ArrayList<>();

		for (final Schedule schedule : scheduleListInWeek) {

			final UserShift shiftType = this.shiftCache.getUserShift(schedule.getShift());
			if (shiftType == null) {
				continue;
			}

			if (!include_current && date_scheduled.equals(schedule.getDate_schedule())) {
				continue;

			}

			if (shiftType.getBreak_shift() || shiftType.getWaitbreak_shift() || shiftType.getDisease_shift() || shiftType.getAccident_shift()) {
				ret.add(schedule);
			}
		}

		if (ret.size() == 0) {
			return null;
		}
		return ret;
	}

	public void setBank_holiday(final IBankHolidays bank_holiday) {
		this.bank_holiday = bank_holiday;
	}

	public void setCompensationDAO(final UserCompensationDAO compensationDAO) {
		this.compensationDAO = compensationDAO;
	}

	public void setMyScheduleDAO(final ISchedule myScheduleDAO) {
		this.myScheduleDAO = myScheduleDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date, java.lang.Integer)
	 */

	public void setMyTaskDAO(final TasksDAO myTaskDAO) {
		this.myTaskDAO = myTaskDAO;
	}

	public void setShiftCache(final IShiftCache shiftCache) {
		this.shiftCache = shiftCache;
	}

	public void setStatisticDAO(final IStatistics statisticDAO) {
		this.statisticDAO = statisticDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date, java.lang.Integer)
	 */
	@Override
	public void shiftAssign(final UserShift shift, final Date current_date_scheduled, final Integer user, final Integer editor) {

		final Date truncDate = DateUtils.truncate(current_date_scheduled, Calendar.DATE);

		// refresh info about just saved schedule
		Schedule schedule = this.myScheduleDAO.loadSchedule(truncDate, user);

		// if schedule == null, create it
		if (schedule == null) {
			schedule = new Schedule();
		}

		// override info
		schedule.setShift(shift.getId());
		schedule.setDate_schedule(truncDate);
		schedule.setUser(user);
		if (editor != null) {
			schedule.setEditor(editor);
		}

		// override
		this.myScheduleDAO.saveOrUpdateSchedule(schedule);

		// refresh info about just saved schedule
		schedule = this.myScheduleDAO.loadSchedule(truncDate, user);

		this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date, java.lang.Integer)
	 */
	@Override
	public void workAssignProcedure(final UserShift shift, final Date current_date_scheduled, final Integer user, final Integer editor) {

		final Date truncDate = DateUtils.truncate(current_date_scheduled, Calendar.DATE);

		// refresh info about just saved schedule
		Schedule schedule = this.myScheduleDAO.loadSchedule(truncDate, user);

		// if schedule == null, create it
		if (schedule == null) {
			schedule = new Schedule();
		}

		// override info
		schedule.setShift(shift.getId());
		schedule.setDate_schedule(truncDate);
		schedule.setUser(user);
		if (editor != null) {
			schedule.setEditor(editor);
		}

		// override
		this.myScheduleDAO.saveOrUpdateSchedule(schedule);

		// refresh info about just saved schedule
		schedule = this.myScheduleDAO.loadSchedule(truncDate, user);

		// if the shift is an absence, delete all details
		if (!shift.getPresence().booleanValue()) {
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
			this.myScheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());
		} else {

			// assign work only if current day is today or tomorrow
			final Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
			final Date today = cal.getTime();

			cal.add(Calendar.DAY_OF_YEAR, 1);
			final Date tomorrow = cal.getTime();

			final boolean istoday = DateUtils.isSameDay(truncDate, today);
			final boolean istomorrow = DateUtils.isSameDay(truncDate, tomorrow);

			if (!istoday && !istomorrow) {
				return;
			}

			// ASSIGN WORK

			// check if there is any default task (MANSIONE STANDARD)
			final UserTask task_default = this.myTaskDAO.getDefault(user);
			if (task_default == null) {
				return;
			}

			// get a shift for a day
			final Integer my_no_shift = this.getShiftNoForDay(truncDate, user);

			// remove all detail in any shift
			this.myScheduleDAO.removeAllDetailInitialScheduleBySchedule(schedule.getId());
			this.myScheduleDAO.removeAllDetailFinalScheduleBySchedule(schedule.getId());

			// prepare period
			final Calendar cal_shift_1_time_from = DateUtils.toCalendar(truncDate);
			cal_shift_1_time_from.set(Calendar.HOUR_OF_DAY, 1);
			cal_shift_1_time_from.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_1_time_to = DateUtils.toCalendar(truncDate);
			cal_shift_1_time_to.set(Calendar.HOUR_OF_DAY, 7);
			cal_shift_1_time_to.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_2_time_from = DateUtils.toCalendar(truncDate);
			cal_shift_2_time_from.set(Calendar.HOUR_OF_DAY, 7);
			cal_shift_2_time_from.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_2_time_to = DateUtils.toCalendar(truncDate);
			cal_shift_2_time_to.set(Calendar.HOUR_OF_DAY, 13);
			cal_shift_2_time_to.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_3_time_from = DateUtils.toCalendar(truncDate);
			cal_shift_3_time_from.set(Calendar.HOUR_OF_DAY, 13);
			cal_shift_3_time_from.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_3_time_to = DateUtils.toCalendar(truncDate);
			cal_shift_3_time_to.set(Calendar.HOUR_OF_DAY, 19);
			cal_shift_3_time_to.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_4_time_from = DateUtils.toCalendar(truncDate);
			cal_shift_4_time_from.set(Calendar.HOUR_OF_DAY, 19);
			cal_shift_4_time_from.set(Calendar.MINUTE, 0);

			final Calendar cal_shift_4_time_to = DateUtils.toCalendar(truncDate);
			cal_shift_4_time_to.add(Calendar.DAY_OF_YEAR, 1);
			cal_shift_4_time_to.set(Calendar.HOUR_OF_DAY, 1);
			cal_shift_4_time_to.set(Calendar.MINUTE, 0);

			if (shift.getDaily_shift().booleanValue()) {

				final Calendar cal_shift_2_daily_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_2_daily_time_from.set(Calendar.HOUR_OF_DAY, 8);
				cal_shift_2_daily_time_from.set(Calendar.MINUTE, 30);

				final Calendar cal_shift_2_daily_time_to = DateUtils.toCalendar(truncDate);
				cal_shift_2_daily_time_to.set(Calendar.HOUR_OF_DAY, 13);
				cal_shift_2_daily_time_to.set(Calendar.MINUTE, 0);

				final Calendar cal_shift_3_daily_time_from = DateUtils.toCalendar(truncDate);
				cal_shift_3_daily_time_from.set(Calendar.HOUR_OF_DAY, 14);
				cal_shift_3_daily_time_from.set(Calendar.MINUTE, 30);

				final Calendar cal_shift_3_daily_time_to = DateUtils.toCalendar(truncDate);
				cal_shift_3_daily_time_to.set(Calendar.HOUR_OF_DAY, 18);
				cal_shift_3_daily_time_to.set(Calendar.MINUTE, 0);

				final DetailInitialSchedule item1 = new DetailInitialSchedule();
				item1.setId_schedule(schedule.getId());
				item1.setShift(2);
				item1.setTask(task_default.getId());
				item1.setTime(4.0);
				item1.setTime_from(new Timestamp(cal_shift_2_daily_time_from.getTimeInMillis()));
				item1.setTime_to(new Timestamp(cal_shift_2_daily_time_to.getTimeInMillis()));

				final DetailInitialSchedule item2 = new DetailInitialSchedule();
				item2.setId_schedule(schedule.getId());
				item2.setShift(3);
				item2.setTask(task_default.getId());
				item2.setTime(4.0);
				item2.setTime_from(new Timestamp(cal_shift_3_daily_time_from.getTimeInMillis()));
				item2.setTime_to(new Timestamp(cal_shift_3_daily_time_to.getTimeInMillis()));

				// create detail
				this.myScheduleDAO.createDetailInitialSchedule(item1);
				this.myScheduleDAO.createDetailInitialSchedule(item2);

			} else {
				final DetailInitialSchedule item = new DetailInitialSchedule();
				item.setId_schedule(schedule.getId());
				item.setShift(my_no_shift);
				item.setTask(task_default.getId());
				item.setTime(6.0);

				// set period
				switch (my_no_shift) {

				case 1:

					item.setTime_from(new Timestamp(cal_shift_1_time_from.getTimeInMillis()));
					item.setTime_to(new Timestamp(cal_shift_1_time_to.getTimeInMillis()));
					break;

				case 2:

					item.setTime_from(new Timestamp(cal_shift_2_time_from.getTimeInMillis()));
					item.setTime_to(new Timestamp(cal_shift_2_time_to.getTimeInMillis()));
					break;

				case 3:

					item.setTime_from(new Timestamp(cal_shift_3_time_from.getTimeInMillis()));
					item.setTime_to(new Timestamp(cal_shift_3_time_to.getTimeInMillis()));
					break;

				case 4:

					item.setTime_from(new Timestamp(cal_shift_4_time_from.getTimeInMillis()));
					item.setTime_to(new Timestamp(cal_shift_4_time_to.getTimeInMillis()));
					break;

				}

				// create detail
				this.myScheduleDAO.createDetailInitialSchedule(item);

			}

		}

	}
}
