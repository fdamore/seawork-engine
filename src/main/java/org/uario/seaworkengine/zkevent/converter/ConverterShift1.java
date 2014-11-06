package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Toolbarbutton;

public class ConverterShift1 implements TypeConverter {

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

		final Listcell listcell = (Listcell) arg1.getParent();

		// define color
		UtilityProgramRow.defineColorShiftConverter(listcell, item_schedule);

		// after define color.... check if disabled
		final String status = item_schedule.getRowSchedule().getUser_status();
		if (status != null) {
			final Toolbarbutton button = (Toolbarbutton) arg1;
			if (status.equals(ShiftTag.USER_WORKER_NOT_AVAILABLE)) {
				button.setDisabled(true);
			}
		}

		// return info
		if (item_schedule.getAnchor1() == null) {
			return ConverterShift1.NO_DATA;
		} else {
			return item_schedule.getAnchor1();
		}

	}
}
