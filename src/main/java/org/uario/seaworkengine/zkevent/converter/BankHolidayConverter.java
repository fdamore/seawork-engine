package org.uario.seaworkengine.zkevent.converter;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class BankHolidayConverter implements TypeConverter {

	List<String>	Month	= Arrays.asList("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre",
									"Novembre", "Dicembre");

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof String) || (arg0 == null)) {
			return arg0;
		}

		final String dt = (String) arg0;

		final int month = Integer.parseInt(dt.substring(0, 2));

		final String date = dt.substring(3, 5) + " " + this.Month.get(month - 1);

		return date;

	}
}
