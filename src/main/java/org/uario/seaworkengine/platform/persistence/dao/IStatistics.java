package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.statistics.AverageShift;

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
	public List<AverageShift> getAverageForShift(Integer user, Date date);

	/**
	 * Get percentage working sunday
	 * 
	 * @param id_user
	 * @return
	 */
	public Double getSundayWorkPercentage(Integer id_user);

}
