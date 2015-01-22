package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.UserShift;
import org.uario.seaworkengine.utility.ShiftTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Toolbarbutton;

public class ConverterShiftNoColorReview3 implements TypeConverter {

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

		// get status
		final String status = UtilityProgramRow.getCurrentStatus(item_schedule);

		// define behavior
		UtilityProgramRow.defineRowBeahvior(arg1, item_schedule, status);

		// get user shift
		final UserShift myShift = UtilityProgramRow.getCurrentUserShift(item_schedule);

		// return info
		if (status != null) {
			if (status.equals(ShiftTag.USER_WORKER_NOT_AVAILABLE) || status.equals(ShiftTag.USER_WORKER_FORZABLE)) {
				if ((item_schedule.getSchedule() != null) && (item_schedule.getSchedule().getShift() != null)) {
					if (myShift != null) {
						final Toolbarbutton toolbarbutton = (Toolbarbutton) arg1;
						toolbarbutton.setDisabled(true);

						final Listitem listItem = (Listitem) arg1.getParent().getParent();
						listItem.setDisabled(true);
						listItem.setCheckable(false);

						return myShift.getCode();
					} else {
						return UtilityProgramRow.NO_DATA;
					}

				}
			}
		}

		if (item_schedule.getAnchor3() == null) {
			final Component comp = arg1;
			final Listcell listCell = (Listcell) comp.getParent();
			final String color_stye = "background-color: white;";
			listCell.setStyle(color_stye);
			return ConverterShiftNoColorReview3.NO_DATA;
		} else {
			return item_schedule.getAnchor3();
		}
	}
}
