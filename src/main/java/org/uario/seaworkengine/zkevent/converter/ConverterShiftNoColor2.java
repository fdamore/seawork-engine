package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Toolbarbutton;

public class ConverterShiftNoColor2 implements TypeConverter {

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

		// after define color.... check if disabled
		final String status = item_schedule.getRowSchedule().getUser_status();
		if (status != null) {
			final Toolbarbutton button = (Toolbarbutton) arg1;
			if (status.equals(ShiftTag.USER_WORKER_NOT_AVAILABLE)) {
				button.setDisabled(true);
			}
		}

		if (item_schedule.getAnchor2() == null) {
			return ConverterShiftNoColor2.NO_DATA;
		} else {
			return item_schedule.getAnchor2();
		}

	}
}
