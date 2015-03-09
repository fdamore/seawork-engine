package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class TabelUserNameName implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		final String name = (String) arg0;

		final String[] info = name.split(" ");
		if (info.length >= 2) {
			final String surname = info[0];
			final String info_name = info[1];

			return surname + " " + info_name.toCharArray()[0] + ".";

		} else {
			return name;
		}

	}
}
