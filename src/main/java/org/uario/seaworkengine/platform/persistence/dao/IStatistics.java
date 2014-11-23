package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;

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
	public RateShift[] getAverageForShift(Integer user, Date date);

	/**
	 * Get percentage working sunday
	 *
	 * @param id_user
	 * @return
	 */
	public Double getSundayWorkPercentage(Integer id_user);

	/**
	 * Get Time Worked
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Integer getTimeWorked(Integer id_user, Date date_from, Date date_to);

}
