package org.uario.seaworkengine.statistics;

import java.util.List;

public interface IBankHolidays {

	/**
	 * Count current Holidays until now
	 *
	 * @return
	 */
	public int countCurrentHolidaysUntilNow();

	/**
	 * Count current Sunday until now
	 *
	 * @return
	 */
	public int countCurrentSundaysUntilNow();

	/**
	 * Get Bank Holidays
	 *
	 * @return
	 */
	public List<String> getDays();
}