package org.uario.seaworkengine.zkevent.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class BankHolidayConverter implements TypeConverter {

	private static SimpleDateFormat	format_input	= new SimpleDateFormat("MM-dd");
	private static SimpleDateFormat	format_output	= new SimpleDateFormat("dd MMMMM");

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		try {
			if (!(arg0 instanceof String) || (arg0 == null)) {
				return arg0;
			}

			final String dt = (String) arg0;

			final Date date_input = BankHolidayConverter.format_input.parse(dt);
			String date_info = BankHolidayConverter.format_output.format(date_input);

			String name = "";
			if (dt.equals("01-01")) {
				name = " (Capodanno)";
			} else if (dt.equals("01-06")) {
				name = " (Epifania)";
			} else if (dt.equals("04-25")) {
				name = " (Liberazione)";
			} else if (dt.equals("05-01")) {
				name = " (Festa del Lavoro)";
			} else if (dt.equals("06-02")) {
				name = " (Festa della Repubblica)";
			} else if (dt.equals("08-15")) {
				name = " (Ferragosto)";
			} else if (dt.equals("09-01")) {
				name = " (Ognissanti)";
			} else if (dt.equals("12-08")) {
				name = " (Immacolata Concezione)";
			} else if (dt.equals("12-25")) {
				name = " (Natale)";
			} else if (dt.equals("12-26")) {
				name = " (Santo Stefano)";
			} else if (dt.equals("08-13")) {
				name = " (Festa Padronale)";
			} else {
				name = " (Lunedì dell'Angelo)";
			}

			date_info = date_info + name;

			return date_info;
		} catch (final ParseException e) {
			return "";
		}

	}
}
