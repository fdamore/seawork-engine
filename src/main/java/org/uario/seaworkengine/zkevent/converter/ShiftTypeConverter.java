package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.ShiftTag;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShiftTypeConverter implements TypeConverter {

	private static final String	ABSENCE	= "false";

	private static final String	WORK	= "true";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (arg0 == null) {
			return "";
		}

		final String shift = arg0.toString();

		if (shift.equals(ShiftTypeConverter.ABSENCE)) {
			return ShiftTag.ABSENCE_SHIFT;

		}

		if (shift.equals(ShiftTypeConverter.WORK)) {
			return ShiftTag.WORK_SHIFT;

		}

		return "";

	}
}
