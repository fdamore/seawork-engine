package org.uario.seaworkengine.zkevent.converter;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.utility.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

public class DecimalToTimeVacationConverter implements TypeConverter {

	@Override
	public Object coerceToBean(final Object arg0, final Component arg1) {

		return null;
	}

	@Override
	public Object coerceToUi(final Object arg0, final Component arg1) {

		if ((!(arg0 instanceof DetailFinalSchedule) && !(arg0 instanceof DetailInitialSchedule)) || (arg0 == null)) {
			return arg0;
		}

		if (arg0 instanceof DetailFinalSchedule) {
			final DetailFinalSchedule ds = (DetailFinalSchedule) arg0;
			if (ds.getTime() == 0) {
				return Utility.decimatToTime(ds.getTime_vacation());
			} else {
				return Utility.decimatToTime(ds.getTime());
			}
		} else {
			final DetailInitialSchedule ds = (DetailInitialSchedule) arg0;
			if (ds.getTime() == 0) {
				return Utility.decimatToTime(ds.getTime_vacation());
			} else {
				return Utility.decimatToTime(ds.getTime());
			}
		}
	}
}
