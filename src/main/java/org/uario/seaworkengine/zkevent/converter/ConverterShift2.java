package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.zkevent.bean.ItemRowScheduler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class ConverterShift2 implements TypeConverter {

	private static final String	NO_DATA	= "X";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof ItemRowScheduler) || (arg0 == null)) {
			return arg0;
		}

		final ItemRowScheduler item_schedule = (ItemRowScheduler) arg0;

		if (item_schedule.getAnchor2() == null) {
			return ConverterShift2.NO_DATA;
		} else {
			return item_schedule.getAnchor2();
		}

	}
}
