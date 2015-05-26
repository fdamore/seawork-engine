package org.uario.seaworkengine.zkevent.converter;

import java.text.NumberFormat;
import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class MoneyConverter implements TypeConverter {

	// NumberFormat format = new DecimalFormat("#,##0.##");
	NumberFormat format = NumberFormat.getNumberInstance(Locale.ITALY);

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof Double) || (arg0 == null)) {
			return arg0;
		}

		final Double money = (Double) arg0;

		return this.format.format(money);

	}
}
