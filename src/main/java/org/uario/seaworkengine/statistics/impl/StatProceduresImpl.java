package org.uario.seaworkengine.statistics.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.dao.ISchedule;
import org.uario.seaworkengine.platform.persistence.dao.IStatistics;
import org.uario.seaworkengine.platform.persistence.dao.TasksDAO;
import org.uario.seaworkengine.statistics.IStatProcedure;
import org.uario.seaworkengine.statistics.RateShift;
import org.uario.seaworkengine.utility.Utility;

public class StatProceduresImpl implements IStatProcedure {

	private ISchedule	myScheduleDAO;

	private TasksDAO	myTaskDAO;

	private IStatistics	statisticDAO;

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

		// get a shift - 12 after last shift
		final Integer last_shift = this.myScheduleDAO.getLastShift(calendar.getTime(), user);
		int min_shift = 1;
		if (last_shift != null) {
			if (last_shift == 3) {
				min_shift = 2;
			}
			if (last_shift == 4) {
				min_shift = 3;
			}
		}

		return min_shift;

	}

	public ISchedule getMyScheduleDAO() {
		return this.myScheduleDAO;
	}

	public TasksDAO getMyTaskDAO() {
		return this.myTaskDAO;
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

		// get info from last shift
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(current_date_scheduled);
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		// get a shift - 12 after last shift
		final Integer last_shift = this.myScheduleDAO.getLastShift(calendar.getTime(), user);
		int min_shift = 1;
		if (last_shift != null) {
			if (last_shift == 3) {
				min_shift = 2;
			}
			if (last_shift == 4) {
				min_shift = 3;
			}
		}

		// get my shift
		Integer my_shift = -1;
		if ((averages == null) || (averages.length == 0)) {

			my_shift = min_shift + (int) (Math.random() * 4);

		} else {
			if ((last_shift == null) || (last_shift == 1) || (last_shift == 2)) {
				my_shift = averages[0].getShift();
			}

			else {

				// set current shift
				my_shift = averages[0].getShift();

				final int cutoff = last_shift - 2;

				for (int i = 0; i < averages.length; i++) {

					final RateShift current_shift = averages[i];
					if (current_shift.getShift() > cutoff) {
						my_shift = averages[i].getShift();
						break;
					}

				}

			}
		}
		return my_shift;
	}

	public IStatistics getStatisticDAO() {
		return this.statisticDAO;
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

		final Date my_pick_date = DateUtils.truncate(date, Calendar.DATE);

		// get begin date
		final Calendar calendar = DateUtils.toCalendar(my_pick_date);
		calendar.add(Calendar.DAY_OF_YEAR, -15);
		final Date date_begin = calendar.getTime();

		// get date_working
		final List<Date> list = this.statisticDAO.getDateAtWork(user, date_begin, my_pick_date);

		// current head series
		Date currentdate = null;

		// the current length
		int lenght_series = 0;

		for (final Iterator<Date> iterator = list.iterator(); iterator.hasNext();) {

			// get date
			final Date item = iterator.next();

			if (lenght_series == 0) {
				lenght_series++;
				currentdate = DateUtils.truncate(item, Calendar.DATE);
				continue;
			}

			final Date item_date = DateUtils.truncate(item, Calendar.DATE);

			// Difference between date
			final int diff = Utility.daysBetweenDate(item_date, currentdate);

			if (diff == 1) {
				lenght_series++;
				currentdate = DateUtils.truncate(item, Calendar.DATE);

			} else {

				lenght_series = 1;
				currentdate = DateUtils.truncate(item, Calendar.DATE);

			}

		}

		return lenght_series;

	}

	public void setMyScheduleDAO(final ISchedule myScheduleDAO) {
		this.myScheduleDAO = myScheduleDAO;
	}

	public void setMyTaskDAO(final TasksDAO myTaskDAO) {
		this.myTaskDAO = myTaskDAO;
	}

	public void setStatisticDAO(final IStatistics statisticDAO) {
		this.statisticDAO = statisticDAO;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date,
	 * java.lang.Integer)
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
	 * @see
	 * org.uario.seaworkengine.statistics.IStatProcedure#workAssignProcedure
	 * (org.uario.seaworkengine.model.UserShift, java.util.Date,
	 * java.lang.Integer)
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

			if (shift.getDaily_shift().booleanValue()) {

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
}
