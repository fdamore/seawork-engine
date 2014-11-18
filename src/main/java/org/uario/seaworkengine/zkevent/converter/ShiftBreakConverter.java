package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserShift;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Image;

public class ShiftBreakConverter implements TypeConverter {

	private static String	img	= "/img/star-full-icon.png";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg1 instanceof Image)) {
			return "";
		}

		if (!(arg0 instanceof UserShift)) {
			return "";
		}

		final UserShift shift = (UserShift) arg0;
		if (shift.getBreak_shift() == null) {
			return "";
		}
		if (shift.getBreak_shift().booleanValue()) {
			return ShiftBreakConverter.img;
		} else {

			return "";
		}

	}

}
