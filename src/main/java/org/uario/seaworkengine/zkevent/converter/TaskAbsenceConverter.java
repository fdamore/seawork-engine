package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TaskAbsenceConverter implements TypeConverter {
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

		final Boolean isAbsence = (Boolean) arg0;
		if (isAbsence == null) {
			return "";
		}
		if (isAbsence) {
			return TaskAbsenceConverter.img;
		} else {

			return "";
		}

	}
}
