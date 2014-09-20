package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Scheduler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverterShift1 implements TypeConverter {

	private static final String	NO_DATA	= "X";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Scheduler) || (arg0 == null)) {
			return arg0;
		}

		final Scheduler shift = (Scheduler) arg0;

		if ((shift.getInitial_shift_1() == null) && (shift.getInitial_time_1() == null)) {
			return NO_DATA;
		}

		if (shift.getInitial_time_1() != null) {
			return "" + shift.getInitial_time_1();
		}

		if (shift.getInitial_shift_1() != null) {
			return "" + shift.getInitial_shift_1();
		}

		return NO_DATA;

	}
}
