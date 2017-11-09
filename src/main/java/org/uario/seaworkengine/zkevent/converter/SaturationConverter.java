package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class SaturationConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!((arg0 instanceof Double) && (arg0 != null))) {
			return arg0;
		}

		final Double sat = (Double) arg0;

		final String type_sat = Utility.getTypeSaturation(sat);

		return type_sat + " " + Utility.roundTwo(Math.abs(sat));

	}

}
