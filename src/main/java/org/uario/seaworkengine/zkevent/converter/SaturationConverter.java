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

		if (!(arg0 instanceof Double && arg0 != null)) {
			return arg0;
		}

		final Double sat = (Double) arg0;

		final String text = "";

		// set saturation style
		if (sat < 0) {

			return text + "REC " + Utility.roundTwo(Math.abs(sat));

		} else {
			return text + "OT " + Utility.roundTwo(sat);
		}

	}

}
