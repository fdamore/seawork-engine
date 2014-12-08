package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.zkevent.bean.ItemRowSchedule;
import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listitem;

public class ReviewConverter implements TypeConverter {

	private static final String	NO_DATA	= "_";

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if (!(arg0 instanceof RowSchedule) || (arg0 == null)) {
			return arg0;
		}

		final RowSchedule item_schedule = (RowSchedule) arg0;

		// compare firsr and second item
		final ItemRowSchedule item_1 = item_schedule.getItem_1();
		final ItemRowSchedule item_2 = item_schedule.getItem_2();

		if (item_1.equals(item_2)) {
			this.noDifference(arg1);
		} else {
			this.signDifference(arg1);
		}

		return item_schedule;

	}

	/**
	 * NO DIFFERENCE
	 *
	 * @param arg
	 */
	private void noDifference(final Object arg) {
		final Listitem itm = (Listitem) arg;
		itm.setStyle("background-color: transparent");

	}

	/**
	 * There are diferences
	 *
	 * @param arg
	 */
	private void signDifference(final Object arg) {
		final Listitem itm = (Listitem) arg;
		itm.setStyle("background-color: #f0ad4e");

	}

}
