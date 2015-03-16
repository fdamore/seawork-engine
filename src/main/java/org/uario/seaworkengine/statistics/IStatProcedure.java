package org.uario.seaworkengine.statistics;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserShift;

public interface IStatProcedure {

	/**
	 * get a random day
	 *
	 * @param current_day
	 * @param border_day
	 * @return a random day begin from the next day to current_day until current
	 *         day + border_day
	 */
	public Date getARandomDay(final Date current_day, final Integer border_day);

	public Integer getFirstShiftInDay(Date date_calendar_schedule, Integer user);

	public Integer getLastShiftInDay(Date date_calendar_schedule, Integer user);

	/**
	 * Maximum Shift
	 *
	 * @param date_calendar_schedule
	 * @param user
	 * @return
	 */
	public Integer getMaximumShift(Date date_calendar_schedule, Integer user);

	/**
	 * Get minumim shift
	 *
	 * @param date_calendar_schedule
	 * @param user
	 * @return
	 */
	public Integer getMinimumShift(final Date date_calendar_schedule, Integer user);

	/**
	 * Get series
	 *
	 * @param date
	 * @param user
	 * @return
	 */
	public Integer getWorkingSeries(final Date date, final Integer user);

	/**
	 * reassign shift and task
	 *
	 * @param schedule
	 * @param editor
	 */
	public void reAssignShift(Schedule schedule, Integer editor);

	/**
	 * @param date_scheduled
	 * @param user_id
	 * @return
	 */
	public List<Schedule> searchBreakInCurrentWeek(Date date_scheduled, Integer user_id);

	/**
	 * Assign shift... remove all details
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor
	 */
	public void shiftAssign(UserShift shift, Date current_date_scheduled, Integer user, Integer editor);

	/**
	 * workAssign Procedure
	 *
	 * @param shift
	 * @param current_date_scheduled
	 * @param user
	 * @param editor
	 *
	 */
	public void workAssignProcedure(UserShift shift, Date current_date_scheduled, Integer user, Integer editor);

}