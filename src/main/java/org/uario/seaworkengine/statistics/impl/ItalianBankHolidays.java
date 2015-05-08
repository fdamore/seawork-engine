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
	public int countHolidays(final Date date_from, final Date date_to) {

		// counter
		int count = 0;

		final Calendar current_calendar = DateUtils.toCalendar(date_to);

		final Date date_from_truncate = DateUtils.truncate(date_from, Calendar.DATE);
		final Date date_to_truncate = DateUtils.truncate(date_from, Calendar.DATE);

		// support date array
		final ArrayList<Date> dtlist = new ArrayList<Date>();

		for (final String item : getDays()) {

			try {
				Date date_item;
				date_item = format.parse(item);
				final Calendar calendar = DateUtils.toCalendar(date_item);
				calendar.set(Calendar.YEAR, current_calendar.get(Calendar.YEAR));
				dtlist.add(calendar.getTime());

			} catch (final ParseException e) {
				continue;
			}

		}

		// sort for date asc
		Collections.sort(dtlist);

		for (final Date item : dtlist) {
			if (((item.before(date_to_truncate) && item.after(date_from_truncate)) || (DateUtils
					.isSameDay(date_to_truncate, item) || DateUtils.isSameDay(date_from_truncate, item)))) {
				count++;
			} else {
				break;
			}
		}

		return count;

	}

	@Override
	public int countSundaysUntilDate(final Date date_from, final Date date_to) {

		// counter
		int count = 0;

		final Calendar calendar_index = DateUtils.toCalendar(date_from);

		final Calendar current_calendar = DateUtils.toCalendar(date_to);

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

		final boolean check = map_easter.containsKey(year);
		if (check) {
			return days;
		} else {
			final Date easter = Utility.findAfterEaster(year);
			final String to_add = format.format(easter);
			map_easter.put(year, to_add);
			days.add(to_add);
			return days;
		}

	}

	public void setDays(final List<String> days) {
		this.days = days;
	}

}
