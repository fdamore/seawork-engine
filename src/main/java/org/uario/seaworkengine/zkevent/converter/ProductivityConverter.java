package org.uario.seaworkengine.zkevent.converter;

import java.text.DecimalFormat;

import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ProductivityConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof ReviewShipWorkAggregate) || (arg0 == null)) {
			return arg0;
		}

		final ReviewShipWorkAggregate reviewShipWork = (ReviewShipWorkAggregate) arg0;

		final Double productivity = reviewShipWork.getVolume() / reviewShipWork.getTime_work();

		final DecimalFormat format = new DecimalFormat("#.##");

		return format.format(productivity);

	}
}
