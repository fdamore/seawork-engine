package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShiftTypeConverter implements TypeConverter {

	private static final String	ABSENCE			= "A";
	private static final String	ABSENCE_INFO	= "TURNO DI ASSENZA";

	private static final String	WORK			= "W";
	private static final String	WORK_INFO		= "TURNO DI LAVORO";

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
			return ShiftTypeConverter.ABSENCE_INFO;

		}

		if (shift.equals(ShiftTypeConverter.WORK)) {
			return ShiftTypeConverter.WORK_INFO;

		}

		return "";

	}
}
