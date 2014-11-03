package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.utility.ProgramColorTag;
import org.uario.seaworkengine.utility.UserTag;
import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Toolbarbutton;

public class UtilityProgramRow {

	/**
	 * color scheduler shift
	 *
	 * @param arg1
	 * @param item_schedule
	 */
	public static void defineColorShiftConverter(final Component arg1, final ItemRowSchedule item_schedule) {
		final RowSchedule row = item_schedule.getRowSchedule();
		final String status = row.getUser_status();
		String color_stye = "background-color:" + ProgramColorTag.COLOR_WORKER;

		if (status.equals(UserTag.USER_WORKER_AVAILABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_WORKER;
		}
		if (status.equals(UserTag.USER_WORKER_FORZABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_FORZABLE;
		}
		if (status.equals(UserTag.USER_WORKER_NOT_AVAILABLE)) {
			color_stye = "background-color:" + ProgramColorTag.COLOR_NOT_AVAILABLE;
		}

		final Listitem item = (Listitem) arg1.getParent().getParent();
		item.setStyle(color_stye);

		final Toolbarbutton button = (Toolbarbutton) arg1;
		if (button.isDisabled()) {
			final Listcell cell = (Listcell) arg1.getParent();
			color_stye = "background-color:" + ProgramColorTag.DISABLE_COLOR;
			cell.setStyle(color_stye);
		}

	}

}
