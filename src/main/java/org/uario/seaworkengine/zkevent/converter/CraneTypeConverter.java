package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class CraneTypeConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (arg0 == null) {
			return arg0;
		}

		if (!(arg0 instanceof ReviewShipWork) && !(arg0 instanceof DetailFinalScheduleShip)) {
			return arg0;
		}

		if (arg0 instanceof ReviewShipWork) {

			final ReviewShipWork item = (ReviewShipWork) arg0;

			final Boolean crane_gtw = item.getCrane_gtw();
			final String crane = item.getCrane();

			return Utility.defineCraneString(crane_gtw, crane);

		} else {
			if (arg0 instanceof DetailFinalScheduleShip) {

				final DetailFinalScheduleShip item = (DetailFinalScheduleShip) arg0;
				return Utility.bodyCraneConverter(item);

			}

		}

		return "";

	}

}
