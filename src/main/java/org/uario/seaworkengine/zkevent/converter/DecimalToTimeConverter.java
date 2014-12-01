package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class DecimalToTimeConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Double) || (arg0 == null)) {
			return arg0;
		}

		final Double source = (Double) arg0;

		final int hours = (int) source.doubleValue();
		final int cents = (int) ((source - hours) * 100);
		final int minuts = (cents * 60) / 100;

		return "" + hours + "h " + minuts + "m";
	}
}
