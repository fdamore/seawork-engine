package org.uario.seaworkengine.zkevent.converter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class BooleanConverter implements TypeConverter {

	private static final String FALSE = "NO";

	private static final String TRUE = "SI";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (arg0 == null) {
			return BooleanConverter.FALSE;
		}

		if (!(arg0 instanceof Boolean)) {
			return null;
		}

		final Boolean var = (Boolean) arg0;

		if (var) {
			return BooleanConverter.TRUE;
		} else {
			return BooleanConverter.FALSE;
		}

	}
}
