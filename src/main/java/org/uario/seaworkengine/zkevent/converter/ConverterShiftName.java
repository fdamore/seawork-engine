package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.zkevent.bean.RowSchedule;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;

public class ConverterShiftName implements TypeConverter {

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

		final RowSchedule row = (RowSchedule) arg0;

		// define color
		final Listcell listcell = (Listcell) arg1;
		UtilityProgramRow.defineColorShiftConverter(listcell, row.getItem_3());

		return row.getName_user();

	}

}
