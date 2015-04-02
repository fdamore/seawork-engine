package org.uario.seaworkengine.zkevent.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class DateToYearConverter implements TypeConverter {

	SimpleDateFormat	format	= new SimpleDateFormat("yyyy");

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

		return this.format.format(dt);

	}
}
