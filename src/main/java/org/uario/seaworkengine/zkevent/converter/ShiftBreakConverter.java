package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ShiftBreakConverter implements TypeConverter {

	private static String	img	= "/img/star-full-icon.png";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Boolean)) {
			return "";
		}

		final Boolean isBreak = (Boolean) arg0;
		if (isBreak == null) {
			return "";
		}
		if (isBreak) {
			return ShiftBreakConverter.img;
		} else {

			return "";
		}

	}

}
