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
	 * @return
	 */
	public Double getSundayAndHolidaysWorkPercentage(Integer id_user, Date date_from);

	/**
	 * Get percentage working sunday
	 *
	 * @param id_user
	 * @return
	 */
	public Double getSundayWorkPercentage(Integer id_user, Date date_from);

	/**
	 * Get Time Worked
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Integer getTimeWorked(Integer id_user, Date date_from, Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *            TODO
	 * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailFinalSchedule> listDetailFinalSchedule(String full_text_search, Integer shift_number, Integer shift_type, Date date_from,
			Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *            TODO
	 * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailInitialSchedule> listDetailInitialSchedule(String full_text_search, Integer shift_number, Integer shift_type, Date date_from,
			Date date_to);

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
