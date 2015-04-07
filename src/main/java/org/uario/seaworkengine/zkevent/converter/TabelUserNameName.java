package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.Utility;
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

		return Utility.dottedName(name);

	}

}
