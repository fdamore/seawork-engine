package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.ReviewShipWork;
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

			return this.defineCraneString(crane_gtw, crane);

		} else {
			if (arg0 instanceof DetailFinalScheduleShip) {

				final DetailFinalScheduleShip item = (DetailFinalScheduleShip) arg0;

				final Boolean crane_gtw = item.getCrane_gtw();
				final String crane = "" + item.getCrane();

				return this.defineCraneString(crane_gtw, crane);

			}

		}

		return "";

	}

	/**
	 * Define crane
	 *
	 * @param crane_gtw
	 * @param crane
	 * @return
	 */
	public Object defineCraneString(final Boolean crane_gtw, final String crane) {
		if (crane_gtw == null) {
			if (crane != null) {
				return crane;
			} else {
				return "";
			}
		} else {

			if (crane_gtw) {
				return "GTW" + crane;
			} else {
				return "CR" + crane;
			}
		}
	}
}
