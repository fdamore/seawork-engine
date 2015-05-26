package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.statistics.RateShift;

public interface IStatistics {

	/**
	 * Get average for shit giving user and date
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getAverageForShift(Integer user, Date date, Date date_from);

	/**
	 * Get average for shit giving user and date. Using program data
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getAverageForShiftOnProgram(Integer user, Date date, Date date_from);

	/**
	 * Get counting sunday work for shift
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getCountSundayForShift(Integer user, Date date, Date date_from);

	/**
	 * Get date at work
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public List<Date> getDateAtWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get Date break for a user in period
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Date getDatesBreak(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get percentage working sunday and holidays
	 *
	 * @param id_user
	 * @param date_to
	 *            TODO
	 * @return
	 */
	public Integer getHolidaysWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get number of day with shift recorded
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Double getShiftRecorded(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get percentage working sunday
	 *
	 * @param id_user
	 * @param date_to
	 *            TODO
	 * @return
	 */
	public Integer getSundayWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get Time Worked
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Double getTimeWorked(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get How many hours user work in period
	 *
	 * @param user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Double getWorkCountByUser(Integer user, Date date_from, Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *
	 * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailFinalSchedule> listDetailFinalSchedule(String full_text_search, Integer shift_number, Integer shift_type, Integer task_id,
			Date date_from, Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *            * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailInitialSchedule> listDetailInitialSchedule(String full_text_search, Integer shift_number, Integer shift_type, Integer task_id,
			Date date_from, Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 *
	 * @param shift_number
	 *
	 * @param date_from
	 *
	 * @param date_to
	 *
	 * @return
	 */
	public List<Schedule> listSchedule(String full_text_search, Integer shift, Date date_from, Date date_to);

}
