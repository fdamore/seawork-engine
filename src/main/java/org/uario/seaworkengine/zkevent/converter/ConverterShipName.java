package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.Ship;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverterShipName implements TypeConverter {

	private static final String NO_DATA = "_";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Ship) || (arg0 == null)) {
			return arg0;
		}

		final Ship ship = (Ship) arg0;

		if (ship.getActivityh() || ship.getNowork()) {
			return ship.getName() + "*";
		} else {
			return ship.getName();
		}

	}

}
