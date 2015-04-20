package org.uario.seaworkengine.zkevent.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverteDateToDayOfWeek implements TypeConverter {

	SimpleDateFormat	format	= new SimpleDateFormat("EEE", Locale.ITALIAN);

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

		final String dayOfWeek = this.format.format(dt);

		return dayOfWeek;

	}
}
