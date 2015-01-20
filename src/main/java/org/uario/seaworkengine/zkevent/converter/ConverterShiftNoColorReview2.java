package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;

public class ConverterShiftNoColorReview2 implements TypeConverter {

	private static final String	NO_DATA	= "_";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof ItemRowSchedule) || (arg0 == null)) {
			return arg0;
		}

		final ItemRowSchedule item_schedule = (ItemRowSchedule) arg0;

		if (item_schedule.getAnchor2() == null) {
			final Component comp = arg1;
			final Listcell listCell = (Listcell) comp.getParent();
			final String color_stye = "background-color: white;";
			listCell.setStyle(color_stye);
			return ConverterShiftNoColorReview2.NO_DATA;
		} else {
			return item_schedule.getAnchor2();
		}

	}
}
