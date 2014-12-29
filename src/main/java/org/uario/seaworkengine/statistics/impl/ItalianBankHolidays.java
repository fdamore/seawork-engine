package org.uario.seaworkengine.statistics.impl;

import java.util.List;

import org.uario.seaworkengine.statistics.IBankHolidays;

public class ItalianBankHolidays implements IBankHolidays {

	private List<String>	days;

	/* (non-Javadoc)
	 * @see org.uario.seaworkengine.statistics.impl.IBankHolidays#getDays()
	 */
	@Override
	public List<String> getDays() {
		return this.days;
	}

	public void setDays(final List<String> days) {
		this.days = days;
	}

}
