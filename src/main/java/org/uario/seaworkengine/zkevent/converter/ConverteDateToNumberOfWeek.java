package org.uario.seaworkengine.zkevent.converter;

import java.util.Date;

import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverteDateToNumberOfWeek implements TypeConverter {

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

		final Integer week = Utility.getWeekNumber(dt);

		return week.toString();

	}

}
