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

			String surname = info[0];
			String info_name = info[1];

			if ((surname.length() == 2) && (info.length >= 3)) {
				surname = info[0] + " " + info[1];
				info_name = info[2];
			}

			return surname + " " + info_name.toCharArray()[0] + ".";

		} else {
			return name;
		}

	}
}
