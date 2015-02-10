package org.uario.seaworkengine.statistics.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.utility.Utility;

public class ItalianBankHolidays implements IBankHolidays {

	private List<String>					days;

	private final SimpleDateFormat			format		= new SimpleDateFormat("MM-dd");

	private final HashMap<Integer, String>	map_easter	= new HashMap<Integer, String>();

	@Override
	public int countCurrentHolidaysUntilNow() {

		// counter
		int count = 0;

		final Calendar current_calendar = Calendar.getInstance();

		// support date array
		final ArrayList<Date> dtlist = new ArrayList<Date>();

		for (final String item : this.getDays()) {

			try {
				Date date_item;
				date_item = this.format.parse(item);
				final Calendar calendar = DateUtils.toCalendar(date_item);
				calendar.set(Calendar.YEAR, current_calendar.get(Calendar.YEAR));
				dtlist.add(calendar.getTime());

			} catch (final ParseException e) {
				continue;
			}

		}

		// sort for date asc
		Collections.sort(dtlist);

		// current date for comparison
		final Date current = current_calendar.getTime();

		for (final Date item : dtlist) {
			if (item.before(current)) {
				count++;
			} else {
				break;
			}
		}

		return count;

	}

	@Override
	public int countCurrentSundaysUntilNow() {

		// counter
		int count = 0;

		final Calendar current_calendar = Calendar.getInstance();

		final Calendar calendar_index = Calendar.getInstance();
		calendar_index.set(Calendar.DAY_OF_YEAR, 1);

		Date date_index = calendar_index.getTime();

		do {
			if (calendar_index.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				count++;
			}
			calendar_index.add(Calendar.DAY_OF_YEAR, 1);
			date_index = calendar_index.getTime();

		} while (!DateUtils.isSameDay(date_index, current_calendar.getTime()));

		return count;

	}

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
