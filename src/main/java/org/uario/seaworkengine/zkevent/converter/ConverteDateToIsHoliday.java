package org.uario.seaworkengine.zkevent.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.uario.seaworkengine.statistics.IBankHolidays;
import org.uario.seaworkengine.utility.BeansTag;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverteDateToIsHoliday implements TypeConverter {

	SimpleDateFormat format = new SimpleDateFormat("MM-dd");

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Date) || (arg0 == null)) {
			return arg0;
		}

		final Date dt = (Date) arg0;

		final IBankHolidays bank_holiday = (IBankHolidays) SpringUtil.getBean(BeansTag.BANK_HOLIDAYS);

		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		final Calendar cal1 = Calendar.getInstance();

		// count calendar
		for (final String item : bank_holiday.getDays()) {

			try {

				final Date date_item = this.format.parse(item);

				cal1.setTime(date_item);

				if ((cal.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) && (cal.get(Calendar.MONTH) == cal1.get(Calendar.MONTH))) {
					return true;
				}

			} catch (final ParseException e) {
				continue;
			}

		}

		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			return true;
		}

		return false;

	}
}
