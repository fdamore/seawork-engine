package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class UserEnableConverter implements TypeConverter {

	private static final String ATTIVO = "ATTIVO";
	private static final String NON_ATTIVO = "NON ATTIVO";

	public static String converterValue(final Boolean enabled) {
		if (enabled.booleanValue()) {
			return UserEnableConverter.ATTIVO;
		} else {
			return UserEnableConverter.NON_ATTIVO;
		}
	}

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Boolean)) {
			return UserEnableConverter.NON_ATTIVO;
		}

		final Boolean ret = (Boolean) arg0;

		if (ret.booleanValue()) {
			return UserEnableConverter.ATTIVO;
		} else {
			return UserEnableConverter.NON_ATTIVO;
		}
	}
}
