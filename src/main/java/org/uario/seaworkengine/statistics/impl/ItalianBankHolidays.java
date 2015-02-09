package org.uario.seaworkengine.statistics.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.utility.Utility;

public class ItalianBankHolidays implements IBankHolidays {

	private List<String>					days;

	private final SimpleDateFormat			format		= new SimpleDateFormat("MM-dd");

	private final HashMap<Integer, String>	map_easter	= new HashMap<Integer, String>();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.uario.seaworkengine.statistics.impl.IBankHolidays#getDays()
	 */
	@Override
	public List<String> getDays() {

		// check if easter calculate
		final int year = Calendar.getInstance().get(Calendar.YEAR);

		final boolean check = this.map_easter.containsKey(year);
		if (check) {
			return this.days;
		} else {
			final Date easter = Utility.findAfterEaster(year);
			final String to_add = this.format.format(easter);
			this.map_easter.put(year, to_add);
			this.days.add(to_add);
			return this.days;
		}

	}

	public void setDays(final List<String> days) {
		this.days = days;
	}

}
