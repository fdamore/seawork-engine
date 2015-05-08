package org.uario.seaworkengine.statistics;

import java.util.Date;
import java.util.List;

public interface IBankHolidays {

	/**
	 * Count current Holidays until now
	 *
	 * @param date_from
	 *            TODO
	 * @param date_to
	 *            TODO
	 *
	 * @return
	 */
	public int countHolidays(Date date_from, Date date_to);

	/**
	 * Count current Sunday until now
	 *
	 * @param date_from
	 *            TODO
	 * @param date_to
	 *            TODO
	 *
	 * @return
	 */
	public int countSundaysUntilDate(Date date_from, Date date_to);

	/**
	 * Get Bank Holidays
	 *
	 * @return
	 */
	public List<String> getDays();

	/**
	 * Get number of holidays
	 * 
	 * @return
	 */
	public Integer getNumberOfHolidays();
}